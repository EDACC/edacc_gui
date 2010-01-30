#include "global.h"
#include "log.h"
#include "signals.h"
#include "database.h"
#include "configuration.h"

#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>


//The path of the shellscript we're using to execute a solver run
//and an array for storing it's arguments
const char* const jobScript="jobScript.sh";
char* const jobScriptArgs[5]={NULL, NULL, NULL, NULL, NULL};

//An array for keeping track of the jobs we're currently processing
job* jobs;
int jobsLen;

//The timeout in seconds for each solver run
int timeOut;


status init() {
	experiment exp;
	status s;

	s=dbFetchExperimentData(&exp);
	if(s!=success)
		return s;
	jobsLen=e.numNodes;
	timeOut=e.timeOut;

	//TODO: Create the solver binaries and instance files, verify the md5 sums

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

inline status updateSuccess(const job* j) {
	status retval;

	deferSignals();
	retval=dbUpdateSuccess(j);
	resetSignalHandler();

	return retval;
}

inline status updateError(const job* j) {
	status retval;

	deferSignals();
	retval=dbUpdateError(j);
	resetSignalHandler();

	return retval;
}

inline status updateRunning(const job* j) {
	status retval;

	deferSignals();
	retval=dbUpdateRunning(j);
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
			updateError(&(jobs[i]));
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
			//The process terminated successfully
			s=updateSuccess(j);
			if(s!=success) {
				return s;
			}
		} else {
			//The process terminated abnormally
			s=updateError(j);
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
				s=updateRunning(j);
				//TODO: Set up the jobScriptArgs array
				if(s==success) {
					if(execve(jobScript, jobScriptArgs, NULL)==-1) {
						logError("Error in execve(): %s\n", strerror(errno));
						s=sysError;
					}
				}
				//Something is seriously wrong. Send the father process a signal indicating the error.
				updateError(j);
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

