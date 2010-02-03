#include "global.h"
#include "log.h"
#include "signals.h"
#include "database.h"
#include "configuration.h"

#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <stdio.h>


//The path of the shellscript we're using to execute a solver run
//and an array for storing it's arguments
const char* const jobScript="jobScript.sh";
char* jobScriptArgs[6]={NULL, NULL, NULL, NULL, NULL, NULL};

//An array for keeping track of the jobs we're currently processing
job* jobs;
int jobsLen;

//The timeout in seconds for each solver run in string form
char timeOutStr[20];


char* pidToFileName(pid_t pid) {
	static char fileName[20];
	snprintf(fileName, 20, "%d.tmp", pid);
	return fileName;
}

char* instanceIdToFileName(int id) {
	static char fileName[20];
	snprintf(fileName, 20, "%d.cnf", id);
	return fileName;
}

status init() {
	experiment exp;
	status s;
	char* const md5FileName="md5sums.txt";
	char* const md5CheckScript="md5check.sh";
	char* const md5CheckScriptArgs[3]={md5CheckScript, md5FileName, NULL};
	FILE* dst;
	FILE* md5File;
	char* instanceName;
	pid_t pid;
	int i, retval;

	s=dbFetchExperimentData(&exp);
	if(s!=success)
		return s;
	jobsLen=exp.numNodes;
	snprintf(timeOutStr, 20, "%d", exp.timeOut);

	//Create a file for saving the md5sums and names of the solver binaries
	//and the instance files as we receive them from the database.
	md5File=fopen(md5FileName, "w+");
	if(md5File==NULL) {
		logError("Error in fopen(): %s\n", strerror(errno));
		return sysError;
	}

	for(i=0; i<exp.numSolvers; ++i) {
		//Create the solver binary
		dst=fopen(exp.solverNames[i], "w+");
		if(dst==NULL) {
			logError("Error in fopen(): %s\n", strerror(errno));
			return sysError;
		}
		fwrite(exp.solvers[i], sizeof(char), exp.lengthSolver[i], dst);
		//There's no need to check for errors in fwrite here. If something goes wrong,
		//we'll get an error in the md5 check anyway.
		fclose(dst);

		//Assure the binary is executable
		if(chmod(exp.solverNames[i], 0111)==-1) {
			logError("Error in chmod(): %s\n", strerror(errno));
			return sysError;
		}

		//Write the md5sum and file name of the solver binary to md5file
		if(fprintf(md5File, "%s  %s\n", exp.md5Solvers[i], exp.solverNames[i])<0) {
			logError("Unable to write to %s\n", md5FileName);
			return sysError;
		}
	}

	for(i=0; i<exp.numInstances; ++i) {
		//Create the instance file
		instanceName=instanceIdToFileName(exp.idInstances[i]);
		dst=fopen(instanceName, "w+");
		if(dst==NULL) {
			logError("Error in fopen(): %s\n", strerror(errno));
			return sysError;
		}
		fprintf(dst, "%s", exp.instances[i]);
		//There's no need to check for errors in fprintf here. If something goes wrong,
		//we'll get an error in the md5 check anyway.
		fclose(dst);

		//Assure the file is readable
		if(chmod(instanceName, 0444)==-1) {
			logError("Error in chmod(): %s\n", strerror(errno));
			return sysError;
		}

		//Write the md5sum and file name of the instance file to md5file
		if(fprintf(md5File, "%s  %s\n", exp.md5Instances[i], instanceName)<0) {
			logError("Unable to write to %s\n", md5FileName);
			return sysError;
		}
	}
	fclose(md5File);

	//Verify the md5sums of the solver binaries and instance files agains the contents of md5file
	pid=fork();
	if(pid==-1) {
		logError("Error in fork(): %s\n", strerror(errno));
		return sysError;
	} else if(pid==0) {
		//This is the child process. Run the local md5sum program to verify the md5 sums.
		if(execve(md5CheckScript, md5CheckScriptArgs, NULL)==-1) {
			logError("Error in execve(): %s\n", strerror(errno));
			return sysError;
		}
	}
	//This is the parent process. Wait for the md5sum check and interpret the return value.
	pid=wait(&retval);
	if(pid==-1){
		logError("Error in wait(): %s\n", strerror(errno));
		return sysError;
	}
	if(retval!=0) {
		logError("The md5 sums of the local files don't match the values from the database\n");
		return sysError;
	}
	remove(md5FileName);

	jobs=calloc(jobsLen, sizeof(job));
	if(jobs==NULL) {
		return sysError;
	}

	return success;
}

inline int fetchJob(job* j, status* s) {
	int retval;

	deferSignals();
	retval=dbFetchJob(j, s);
	resetSignalHandler();

	return retval;
}

inline status update(const job* j) {
	status retval;

	deferSignals();
	retval=dbUpdate(j);
	resetSignalHandler();

	return retval;
}

//Abort all running process and terminate the application with exit code retval
void shutdown(status retval) {
	int i;

	deferSignals();

	//Massacre all other processes in the group including any child they might have
	kill(0, SIGTERM);

	//Update the database entries of the processes that were still running
	for(i=0; i<jobsLen; ++i) {
		if(jobs[i].pid!=0) {
			jobs[i].status=-2;
			update(&(jobs[i]));
		}
	}

	//Try to give the child processes time to shut down cleanly
	for(i=0; i<jobsLen; ++i) {
		if(jobs[i].pid!=0) {
			waitpid(jobs[i].pid, NULL, 0);
		}
	}

	free(jobs);
	exit(retval);
}

void signalHandler(int signum) {
	if(signum==SIGUSR1)
		shutdown(sysError);
	else if(signum==SIGUSR2)
		shutdown(dbError);
	else
		shutdown(success);
}

//Process the results of a normally terminated job j
status processResults(job* j) {
	char* fileName=pidToFileName(j->pid);

	//TODO: Parse filename, fill j accordingly, append the correct line to j->resultFileName

	return success;
}

//Wait for cnt child processes to terminate and handle the results
status handleChildren(int cnt) {
	int i, retval;
	status s;
	job* j;
	pid_t pid;

	for(i=0; i<cnt; ++i) {
		//Wait until a child process terminates
		pid=wait(&retval);
		if(pid==-1){
			logError("Error in wait(): %s\n", strerror(errno));
			return sysError;
		}

		//Point j to the entry in jobs corresponding to the terminated child process
		for(j=jobs; j->pid!=pid; ++j);

		j->pid=0;
		if(WIFEXITED(retval) && (WEXITSTATUS(retval)==0)) {
			//The process terminated normally
			s=processResults(j);
			if(s!=success) {
				return s;
			}
			s=update(j);
			if(s!=success) {
				return s;
			}
		} else {
			//The process terminated abnormally
			j->status=-2;
			s=update(j);
			if(s!=success) {
				return s;
			}
		}
	}

	return success;
}

int main() {
	int numJobs;
	status s;
	job* j;
	pid_t pid;

	read_config();

	s=init();
	if(s!=success) {
		exit(s);
	}

	setSignalHandler(signalHandler);

	for(numJobs=0; ;--numJobs) {
		//Run jobsLen child processes, each of them processing one job
		for(; numJobs<jobsLen; ++numJobs) {
			//Point j to a free slot in the jobs array
			for(j=jobs; j->pid!=0; ++j);

			//Try to load a job from the database and write it to j
			if(fetchJob(j, &s)!=0) {
				//Unable to retrieve a job from the database
				if(s==success) {
					//No error occured, but there's no job left in the database.
					//Wait until all child processes have terminated.
					s=handleChildren(numJobs);
				}
				shutdown(s);
			}

			//Create a process for processing the job
			pid=fork();
			if(pid==-1) {
				logError("Error in fork(): %s\n", strerror(errno));
				shutdown(sysError);
			} else if(pid==0) {
				//This is the child process.
				deferSignals(); //Disable the inherited signal handler.
				//Set the job state to running in the database
				j->status=0;
				s=update(j);
				//Set up the jobScriptArgs array
				jobScriptArgs[0]=(char*)jobScript;
				jobScriptArgs[1]=j->solverName;
				jobScriptArgs[2]=instanceIdToFileName(j->idInstance);
				jobScriptArgs[3]=pidToFileName(getpid());
				jobScriptArgs[4]=timeOutStr;
				if(s==success) {
					if(execve(jobScript, jobScriptArgs, NULL)==-1) {
						logError("Error in execve(): %s\n", strerror(errno));
						s=sysError;
					}
				}
				//Something is seriously wrong. Send the father process a signal indicating the error.
				j->status=-2;
				update(j);
				if(s==sysError) {
					if(kill(getppid(), SIGUSR1)!=0) {
						logError("Error in kill(): %s\n", strerror(errno));
					}
				} else {
					if(kill(getppid(), SIGUSR2)!=0) {
						logError("Error in kill(): %s\n", strerror(errno));
					}
				}
				//Now that was a shitty short life :-/
				exit(sysError);
			}
			j->pid=pid;
		}

		//Wait until one child process terminates and handle the result
		s=handleChildren(1);
		if(s!=success) {
			shutdown(s);
		}
	}

	//Avoid compiler warnings
	return success;
}

