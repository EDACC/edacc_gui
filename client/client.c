#include "global.h"
#include "log.h"
#include "signals.h"
#include "database.h"
#include "configuration.h"
#include "safeio.h"
#include "md5sum.h"

#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <stdio.h>
#include <stdarg.h>
#include <time.h>





//This array stores the arguments for a solver run we execute via execve
static char* jobArgs[4]={NULL, NULL, NULL, NULL};

//An array for keeping track of the jobs we're currently processing
static job* jobs;
static int jobsLen;


//The timeout in seconds for each solver run
int CPUTimeLimit;

//If the experiment is used only to see if a instance is solvable.
int solveOnce=0;

int fileExists(const char* fileName) {
	struct stat buf;
	if(stat(fileName, &buf)==-1 && errno==ENOENT)
		return 0;
	return 1;
}


//Create a file and verify the md5 sum
status createFile(const char* fileName, const char* content, size_t contentLen, const char* md5sum, mode_t mode) {
	FILE* dst;
	unsigned char md5Buffer[16];
	char md5String[33];
	int i;
	char* md5StringPtr;
	int posDiff;

	//Create the file
	dst=fopen(fileName, "w+");
	if(dst==NULL) {
		LOGERROR(" Unable to open %s: %s\n", fileName, strerror(errno));
		return sysError;
	}

	//Write the file content. There's no need to check for errors in fwrite here;
	//if something goes wrong, we'll get an error in the md5 check anyway.
	fwrite(content, sizeof(char), contentLen, dst);

	//Verify the md5sum
	rewind(dst);
	if(md5_stream(dst, &md5Buffer)!=0) {
		LOGERROR(AT,"Error in md5_stream()\n");
		fclose(dst);
		return sysError;
	}
	for(i=0, md5StringPtr=md5String; i<16; ++i, md5StringPtr+=2)
		sprintf(md5StringPtr, "%02x", md5Buffer[i]);
	md5String[32]='\0';
	posDiff=strcmp(md5String, md5sum);
	if(posDiff!=0) {
		LOGERROR(AT,"\nThere might be a problem with the md5 sums for file: %s\n", fileName);
		LOGERROR(AT,"%20s = %s\n","DB md5 sum",md5sum);
		LOGERROR(AT,"%20s = %s\n","Computed md5 sum",md5String);
		LOGERROR(AT,"position where they start to differ = %d\n",posDiff);
		//fclose(dst);
		//remove(fileName);
		//return sysError;
	}

	fclose(dst);

	//Set the file permissions
	if(chmod(fileName, mode)==-1) {
		LOGERROR(AT,"Unable to change permissions for %s: %s\n", fileName, strerror(errno));
		return sysError;
	}

	return success;
}




status initExpData(experiment* exp) {

	//char* fileName;
	status s;
	//int i;

	logComment(2,"starting to fetch experiment data:\n------------------------------------------------------------\n");

	s=dbFetchExperimentData(exp);

	logComment(2,"------------------------------------------------------------\n");
	if(s!=success)
		return s;

	jobsLen=exp->numCPUs;
	CPUTimeLimit=exp->CPUTimeLimit;



	jobs=calloc(jobsLen, sizeof(job));
	if(jobs==NULL) {
		return sysError;
	}

	return success;
}

//Set the startTime field in j to the current local date - time
status setStartTime(job *j) {
	return setMySQLTime(j);
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

inline status updateResults(const job* j) {
	status retval;

	deferSignals();
	retval=dbUpdateResults(j);
	resetSignalHandler();

	return retval;
}

//Abort all running process and terminate the application with exit code retval
void exitClient(status retval) {
	char* fileName;
	int i;

	deferSignals();
	//Massacre all other processes in the group including any child they might have
	kill(0, SIGTERM);

	//Update the database entries of the processes that were still running
	for(i=0; i<jobsLen; ++i) {
		if(jobs[i].pid!=0) {
			jobs[i].status=-5;
			update(&(jobs[i]));
			fileName=prependResultPath(pidToFileName(jobs[i].pid));
			if(fileName!=NULL) {
				remove(fileName);
				free(fileName);
			}
		}
	}

	//Try to give the child processes time to shut down cleanly
	for(i=0; i<jobsLen; ++i) {
		if(jobs[i].pid!=0) {
			waitpid(jobs[i].pid, NULL, 0);
			freeJob(&(jobs[i]));
		}
	}

	free(jobs);
	exit(retval);
}

status loadFile(const char *filename, char **result)
{
	unsigned int size = 0;
	FILE *f = fopen(filename, "rb");
	if (f == NULL)
	{
		*result = NULL;
		LOGERROR(AT,"Error: Not able to open file: %s\n",filename);
		return sysError; // -1 means file opening fail
	}
	fseek(f, 0, SEEK_END);
	size = ftell(f);
	fseek(f, 0, SEEK_SET);
	*result = (char *)malloc(size+1);
	if (size != fread(*result, sizeof(char), size, f))
	{
		free(*result);
		LOGERROR(AT,"Error: Not able to read from file: %s\n",filename);
		return sysError; // -2 means file reading fail
	}
	fclose(f);
	(*result)[size] = 0;
	return success;
}

void signalHandler(int signum) {
	if(signum==SIGUSR1)
		exitClient(sysError);
	else if(signum==SIGUSR2)
		exitClient(dbError);
	else
		exitClient(success);
}

//Process the results of a normally terminated job j
status processResultsN(job* j) {
	/*TODO: GANZ WICHTIG
	 *  If you want to insert binary data into a string column (such as a BLOB column), the following characters must be represented by escape sequences.
NUL 	NUL byte (0x00). Represent this character by “\0” (a backslash followed by an ASCII “0” character).
\ 	Backslash (ASCII 92). Represent this character by “\\”.
' 	Single quote (ASCII 39). Represent this character by “\'”.
" 	Double quote (ASCII 34). Represent this character by “\"”.
	 */
	if (loadFile(j->solverOutputFN,&j->solverOutput)!=success)
		return sysError;
	if (loadFile(j->watcherOutputFN,&j->watcherOutput)!=success)
		return sysError;


	//printf("%s",j->watcherOutput);
	//TODO: LauncherOutput erstenllen
	j->launcherOutput=(char*)malloc(5*sizeof(char));
	strcpy(j->launcherOutput,"NULL");

	j->verifierOutput=(char*)malloc(5*sizeof(char));
	strcpy(j->verifierOutput,"NULL");
	//TODO: to be uncommented as soon as there is a verifier output
	//if (loadFile(j->verifierOutputFN,&j->verifierOutput)!=success)
	//		return sysError;
	//TODO: resultTime parsen

	char prefix[32];
	char dummy[80];
	float time;
	int n;
	const char *ptr =j->watcherOutput;
	while (sscanf(ptr,"%31[^\n]\n%n", prefix,&n)==1 ){ //read line by line maximum of 31 chars
		if (sscanf(prefix,"CPU time (s): %f",&time)==1){
			printf("time extracted for jobID: %d =%f\n",j->id,time);
			j->status=1;
			break;
		}
		ptr+=n;
		//printf("Gelesen: %s \n Length: %d\n ",prefix,n);
		fflush(stdout);
	}
	j->resultTime=time;


	ptr =j->watcherOutput;
	while (sscanf(ptr,"%31[^\n]\n%n", prefix,&n)==1 ){ //read line by line maximum of 31 chars
		if (sscanf(prefix,"Maximum CPU time exceeded:%1s",&dummy)==1){
			j->status=21;
			j->resultCode=-21;
			printf("Limit gefunden");fflush(stdout);
			return success;
			break;
		}
		ptr+=n;
	}
	if (j->status!=21){	//limit not exceeded
		ptr =j->solverOutput;
		while (sscanf(ptr,"%31[^\n]\n%n", prefix,&n)==1 ){ //read line by line maximum of 31 chars
			//printf("try to matching in line: %s\n", prefix);
			if (sscanf(prefix,"%*s %*s %s %*s",&dummy)==1){
				if (strcmp(dummy,"UNSATISFIABLE")==0){
					j->resultCode=10;
					printf("s UNSATISFIABLE found\n");fflush(stdout);
					return success;
					break;
				}
			}
			if (sscanf(prefix,"%*s s %s %*s",&dummy)==1){
				if (strcmp(dummy,"UNKNOWN")==0){
					j->resultCode=0;
					printf("s UNKNOWN found\n");fflush(stdout);
					return success;
					break;
				}
			}
			if (sscanf(prefix,"%*s s %s %*s",&dummy)==1){
				if (strcmp(dummy,"SATISFIABLE")==0){
					j->resultCode=11;
					printf("s SATISFIABLE found\n");fflush(stdout);
					return success;
					break;
				}
			}
			ptr+=n;
		}
	}
	//Hier kommt die Suche nach dem Resultcode im solveroutput




	/*status s=updateResults(j);
	if (s!=success){
		j->status=-5;
		update(j);
		return s;
	}*/



	//TODO: exitCodeSolver parsen
	//TODO: exitCodeVerifier
	//TODO:

	//TODO: Verfier anschmeisen

	//TODO: resultCode berechnen

	return success;
}

status processResults(job* j) {
	char* fileName;
	char* resultFilePtr;
	FILE* filePtr;
	FILE* resultFile;
	int signum, c;
	long outputLen;

	//Set the status to -2 in case anything goes wrong in this function
	j->status=-2;

	fileName=prependResultPath(pidToFileName(j->pid));
	if(fileName==NULL) {
		LOGERROR(AT,"Error: Out of memory\n");
		return sysError;
	}

	//time_t rawtime;
	//time ( &rawtime );


	//Parse the temporary result file of j

	logComment(4, "Trying to open for parsing results: %s\n",fileName);
	filePtr=fopen(fileName, "r");
	if(filePtr==NULL) {
		LOGERROR(AT,"Unable to open %s: %s\n", fileName, strerror(errno));
		free(fileName);
		return sysError;
	}

	/*	if(fscanf(filePtr, "Command terminated by signal %d", &signum)==1) {//TODO: das hier kann nicht stimmen
		logComment(4,"Terminating solver due to signal %d\n",signum);
		//The solver was terminated by signal signum
		if(signum==SIGXCPU)
			j->status=2;
		else
			j->status=3;
	} else {


		logComment(4,"No signal detected! Trying to parse run-time!\n");*/

	//Extract the solver output
	{do {
		if(fscanf(filePtr, "Command terminated by signal %d", &signum)==1){ //TODO: das hier kann nicht stimmen
			logComment(4,"Terminating solver due to signal %d\n",signum);
			if(signum==SIGXCPU)
				j->status=-21;
			else
				j->status=3;
			continue;
		}
		c=getc(filePtr);

		if(c==EOF) {
			LOGERROR(AT,"Error parsing %s: Unexpected format\n", fileName);
			free(fileName);
			return sysError;
		}
	} while(c!=(int)'$');


	outputLen=ftell(filePtr);
	if(outputLen==-1) {
		LOGERROR(AT,"Error in ftell(): %s\n", strerror(errno));
		free(fileName);
		return sysError;
	}
	j->solverOutput=malloc(outputLen);
	if(j->solverOutput==NULL) {
		LOGERROR(AT,"Error: Out of memory\n");
		free(fileName);
		return sysError;
	}
	rewind(filePtr);
	resultFilePtr=j->solverOutput;
	do {
		c=getc(filePtr);
		if(c==EOF) {
			LOGERROR(AT,"Error parsing %s: Unexpected format\n", fileName);
			free(fileName);
			return sysError;
		}
		*resultFilePtr=(char)c;
	} while(c!=(int)'$');
	*resultFilePtr='\0';
	//Skip the file content until the next '§' character
	do {
		c=getc(filePtr);
		if(c==EOF) {
			LOGERROR(AT,"Error parsing %s: Unexpected format\n", fileName);
			free(fileName);
			return sysError;
		}
	} while(c!=(int)'$');
	//Extract the runtime and return value of the solver
	if(fscanf(filePtr, "%f$%d", &(j->resultTime), &(j->resultCode))!=2){
		LOGERROR(AT,"Error parsing %s: Unexpected format\n", fileName);
		free(fileName);
		return sysError;
	}
	j->solverOutput[outputLen-1]='\0';
	if (j->status<1)
		j->status=1;
	//Append the content of fileName to j->resultFileName
	rewind(filePtr);
	resultFile=fopen(j->solverOutputFN, "a");
	if(resultFile==NULL) {
		LOGERROR(AT,"Unable to open %s: %s\n", j->solverOutputFN, strerror(errno));
		free(fileName);
		return sysError;
	}
	while((c=safeGetc(filePtr))!=EOF) {
		if(safeFputc(c, resultFile)==EOF) {
			LOGERROR(AT,"An error occured while copying a character from %s to %s\n", fileName, j->solverOutputFN);
			free(fileName);
			return sysError;
		}
	}
	if(safeFputc((int)'\n', resultFile)==EOF) {
		LOGERROR(AT,"An error occured while writing a character to %s\n", j->solverOutputFN);
		free(fileName);
		return sysError;
	}
	//time ( &rawtime );
	logComment(4, "parsing results from : %s  finished\n",fileName);
	fclose(resultFile);
	}

	fclose(filePtr);
	remove(fileName);
	free(fileName);

	return success;
}

//Wait for cnt child processes to terminate and handle the results
status handleChildren(int cnt) {
	int i, retval;
	status s;
	job* j;
	pid_t pid;
	//FILE* solverOutput;

	for(i=0; i<cnt; ++i) {
		//Wait until a child process terminates
		pid=wait(&retval);
		if(pid==-1){
			LOGERROR(AT,"Error in wait(): %s\n", strerror(errno));
			return sysError;
		}

		//Point j to the entry in jobs corresponding to the terminated child process
		for(j=jobs; j->pid!=pid; ++j);
		if (WIFEXITED(retval)) { //watcher terminated normally
			j->watcherExitCode=WEXITSTATUS(retval); //save exitCode of watcher
			s=processResultsN(j);
			j->pid=0;
			if(s!=success) {
				j->status=-5;
				update(j);
				freeJob(j);
				return success;
			}
			s=updateResults(j);

			if(s!=success) {
				j->status=-5;
				update(j);
				freeJob(j);
				return success;
			}
			if (!keepResults){
				remove(j->solverOutputFN);
				remove(j->watcherOutputFN);
			}
			freeJob(j);
		}
		if (WIFSIGNALED(retval)) { //watcher was terminated by a signal
			j->status=-4*100-WTERMSIG(retval); //watcher crash has code: -4xx : xx=signal number
			//The process terminated abnormally

			//solverOutput=prependResultPath(j->solverOutputFN);
			j->pid=0;
			s=update(j);
			freeJob(j);
			/*if(solverOutput==NULL) {
				LOGERROR(AT,"Error: Out of memory\n");
				if(s==success)
					s=sysError;
				return s;
			}
			remove(solverOutput);
			free(solverOutput);
			if(s!=success) {
				return s;
			}*/
		}
	}
	return success;
}


//Fill jobArgs with the information in j. If the function succeeds, some parts of
//jobArgs might be set to allocated memory that can be freed with freeJobArg().
status setJobArgs(const job* j) {
	char* fileName;
	char* command;
	char* solverName;
	char* instanceName;

	fileName=prependResultPath(pidToFileName(getpid()));
	if(fileName==NULL) {
		LOGERROR(AT,"Error: Out of memory\n");
		return sysError;
	}
	solverName=prependSolverPath(j->solverName);
	if(solverName==NULL) {
		LOGERROR(AT,"Error: Out of memory\n");
		free(fileName);
		return sysError;
	}
	instanceName=prependInstancePath(j->instanceName);
	if(instanceName==NULL) {
		LOGERROR(AT,"Error: Out of memory\n");
		free(fileName);
		free(solverName);
		return sysError;
	}
	//TODO: hier kann runsolver hinzugefuegt werden
	if(sprintfAlloc(&command,
			"ulimit -S -t %d && /usr/bin/time -a -o %s -f \"$%%C$%%U$%%x\" %s %s %s >> %s",
			CPUTimeLimit, fileName, solverName, j->params, instanceName, fileName          ) < 0) {
		LOGERROR(AT,"Error in sprintfAlloc()\n");
		free(fileName);
		free(solverName);
		free(instanceName);
		return sysError;
	}
	logComment(1,"starting solver with command: %s\n", command);

	jobArgs[0]="/bin/bash";
	jobArgs[1]="-c";
	jobArgs[2]=command;
	jobArgs[3]=NULL;

	return success;
}


status setWatcherArgs(const job* j, const experiment exp) {
	char* command;
	char* solverBinary;
	char* instanceName;
	char* solverOutput;
	char* watcherOutput;
	char temp[1024];

	solverBinary=prependSolverPath(j->binaryName);
	if(solverBinary==NULL) {
		LOGERROR(AT,"Error: Out of memory\n");
		free(solverBinary);
		return sysError;
	}
	instanceName=prependInstancePath(j->instanceName);
	if(instanceName==NULL) {
		LOGERROR(AT,"Error: Out of memory\n");
		free(solverBinary);
		free(instanceName);
		return sysError;
	}
	solverOutput=prependBasename(j->solverOutputFN);
	//solverOutput=prependResultPath(j->solverOutputFN);
	if(solverOutput==NULL) {
		LOGERROR(AT,"Error: Out of memory\n");
		free(solverBinary);
		free(instanceName);
		free(solverOutput);
		return sysError;
	}
	watcherOutput=prependBasename(j->watcherOutputFN);
	//watcherOutput=prependResultPath(j->watcherOutputFN);
	if(watcherOutput==NULL) {
		LOGERROR(AT,"Error: Out of memory\n");
		free(solverBinary);
		free(instanceName);
		free(solverOutput);
		free(watcherOutput);
		return sysError;
	}

	//no limitation of the output length yet
	//if a limit is -1 then this limit should not be imposed
	//TODO: Den Aufbau von command schoener gestallten. und den Speicher freigeben
	strcpy(temp,"./runsolver --timestamp -w %s -o %s ");

	if(sprintfAlloc(&command,temp,watcherOutput,solverOutput) < 0)
		LOGERROR(AT,"Error in sprintfAlloc()\n");//hier noch free...
	strcpy(temp,command);

	if (exp.CPUTimeLimit!=-1){
		strcat(temp,"-C %d ");
		if(sprintfAlloc(&command,temp,exp.CPUTimeLimit) < 0)
			LOGERROR(AT,"Error in sprintfAlloc()\n");
	}

	strcpy(temp,command);

	if (exp.wallClockTimeLimit!=-1){
		strcat(temp,"-W %d ");
		if(sprintfAlloc(&command,temp,exp.wallClockTimeLimit) < 0)
			LOGERROR(AT,"Error in sprintfAlloc()\n");
	}

	strcpy(temp,command);

	if (exp.memoryLimit!=-1){
		strcat(temp,"-M %d ");
		if(sprintfAlloc(&command,temp,exp.memoryLimit) < 0)
			LOGERROR(AT,"Error in sprintfAlloc()\n");
	}

	strcpy(temp,command);

	if (exp.stackSizeLimit!=-1){
		strcat(temp,"-S %d ");
		if(sprintfAlloc(&command,temp,exp.stackSizeLimit) < 0)
			LOGERROR(AT,"Error in sprintfAlloc()\n");
	}
	strcpy(temp,command);
	strcat(temp,"%s %s");

	if(sprintfAlloc(&command,temp,solverBinary,j->params) < 0) {
		LOGERROR(AT,"Error in sprintfAlloc()\n");
		free(solverBinary);
		free(instanceName);
		free(solverOutput);
		free(solverOutput);
		return sysError;
	}
	logComment(1,"starting solver with command: %s\n", command);

	jobArgs[0]="/bin/bash";
	jobArgs[1]="-c";
	jobArgs[2]=command;
	jobArgs[3]=NULL;

	return success;
}


void freeJobArgs() {
	free(jobArgs[2]);
}


void printUsage(){
	//TODO: Print usage
}

void initDefaultParameters(){
	verbosity=4;
	keepResults=0;
	waitForDB=20; //60sec.
	connectAttempts=5;
	waitForJobs=3600;
	scanForJobs=60;
}

int main(int argc, char **argv) {

	initDefaultParameters();



	static const struct option long_options[] =	{
			{ "verbosity", required_argument,       0, 'v' },
			{ "solve_once", no_argument,       0, 's' },
			{ "keep_results", no_argument,       0, 'k' },
			{ "wait_for_db", required_argument,       0, 'w' },
			{ "wait_for_jobs", required_argument,       0, 'j' },
			{ "connect_attempts", required_argument,       0, 'c' },
			0	};

	while (optind < argc)
	{
		int index = -1;
		struct option * opt = 0;
		int result = getopt_long(argc, argv,"v:sk",long_options, &index);
		if (result == -1) break; /* end of list */
		switch (result)
		{
		case 'v': verbosity=atoi(optarg);		break;
		case 's': solveOnce=1;		break;
		case 'k': keepResults=1;		break;
		case 'w': waitForDB=atoi(optarg);		break;
		case 'j': waitForJobs=atoi(optarg);		break;
		case 'c': connectAttempts=atoi(optarg);		break;
		case 0: /* all parameter that do not */
			/* appear in the optstring */
			opt = (struct option *)&(long_options[index]);
			printf("'%s' was specified.",
					opt->name);
			if (opt->has_arg == required_argument)
				printf("Arg: <%s>", optarg);
			printf("\n");
			break;
		default:
			printf("parameter not known!");
			printUsage();
			exit(0);
			break;
		}
	}

	//TODO: die startParameter angeben!

	int jobTries;
	experiment exp;
	int numJobs;
	status s;
	job* j;
	pid_t pid;
	solver solv;
	instance inst;
	char* fileName;

	logComment(1,"reading from configuration file...\n------------------------------\n");
	s=read_config();
	logComment(1,"------------------------------\n");
	if(s!=success) {
		LOGERROR(AT,"couldn't read configuration of the experiment successfully.");
		exit(s);
	}
	logComment(1,"starting the init-process for experiment with ID %d \n------------------------------\n",experimentId);
	initPath();
	checkPath();
	s=initExpData(&exp);
	logComment(1,"\n------------------------------\n");
	if(s!=success) {
		LOGERROR(AT,"couldn't init successfully, for experiment %i.\n", experimentId);
		exit(s);
	}
	logComment(1,"init-process finished\n------------------------------\n");


	setSignalHandler(signalHandler);

	for(numJobs=0; ;--numJobs) {
		//Run jobsLen child processes, each of them processing one job
		for(; numJobs<jobsLen; ++numJobs) {
			//Point j to a free slot in the jobs array
			for(j=jobs; j->pid!=0; ++j);

			//Try to load a job from the database and write it to j
			logComment(2,"\n\nloading job from DB...\n");
			//for (jobTries=0;jobTries<waitForJobs/scanForJobs;jobTries++,sleep(scanForJobs)){
			if(fetchJob(j, &s)!=0) {
				//Unable to retrieve a job from the database
				if(s==success) {
					//No error occured, but there's no job left in the database.
					//Wait until all child processes have terminated.
					logComment(2,"no more jobs found!\n");
					s=handleChildren(numJobs);
					//						if (jobTries+1!=waitForJobs/scanForJobs){
					//							logComment(2,"No jobs in DB going to sleep for %d seconds ", scanForJobs);
					//							continue;
				}
				//						else //waited a lot
				//							s=handleChildren(numJobs);
				//					}
				exitClient(s);
			}
			//				break;
			//}

			logComment(1,"------------------------------\n");
			logComment(2,"job details: \n");
			logComment(1,"%20s : %d\n","jobID", j->id);
			logComment(1,"%20s : %s\n","solver", j->solverName);
			logComment(1,"%20s : %s\n","binary", j->binaryName);
			logComment(1,"%20s : %s\n","parameters", j->params);
			logComment(1,"%20s : %d\n","seed", j->seed);
			logComment(1,"%20s : %s\n","instance", j->instanceName);
			logComment(1,"%20s : %s\n","resultFile", j->solverOutputFN);



			//Set j->startTime to the DB-time
			s=setStartTime(j);
			if(s!=success) {
				exitClient(s);
			}

			//Set the job state to running in the database
			j->status=0;
			j->resultTime=0.0;
			j->computeQueue=gridQueueId;
			s=update(j);
			if(s!=success) {
				exitClient(s);
			}

			//Create the solver binary if it doesn't exist yet

			fileName=prependSolverPath(j->binaryName);

			logComment(2,"checking for solver binary: %s ...",fileName);
			if(fileName==NULL) {
				LOGERROR(AT,"Error: Out of memory\n");

				j->status=-5;
				s=update(j);
				exitClient(sysError);
			}
			if(!fileExists(fileName)) {
				s=dbFetchSolver(j->solverName,j->solverVersion, &solv);
				if(s!=success) {
					free(fileName);
					j->status=-5;
					s=update(j);
					exitClient(s);
				}
				if(createFile(fileName, solv.solver, solv.length, solv.md5, 0555)!=success) {

					free(fileName);
					freeSolver(&solv);
					j->status=-5;
					s=update(j);
					exitClient(sysError);
				}
				freeSolver(&solv);
				logComment(2,"%d Bytes downloaded!\n",solv.length);
			}
			else{
				logComment(2,"present locally!\n");
			}
			free(fileName);

			//Create the instance file if it doesn't exist yet

			fileName=prependInstancePath(j->instanceName);
			logComment(2,"checking for instance: %s ...",fileName);
			if(fileName==NULL) {
				LOGERROR(AT,"Error: Out of memory\n");
				j->status=-5;
				s=update(j);
				exitClient(sysError);
			}
			if(!fileExists(fileName)) {
				s=dbFetchInstance(j->instanceName, &inst);
				if(s!=success) {
					free(fileName);
					j->status=-5;
					s=update(j);
					exitClient(s);
				}
				if(createFile(fileName, inst.instance, strlen(inst.instance), inst.md5, 0444)!=success) {
					free(fileName);
					freeInstance(&inst);
					j->status=-5;
					s=update(j);
					exitClient(sysError);
				}
				logComment(2,"%d Bytes downloaded!\n",strlen(inst.instance));
				freeInstance(&inst);
			}
			else{
				logComment(2,"present locally!\n");
			}
			free(fileName);

			//Create a process for processing the job

			pid=fork();
			if (pid!=0)
				logComment(1,"starting job with pid=%d\n",pid);

			if(pid==-1) {
				LOGERROR(AT,"Error in fork(): %s\n", strerror(errno));
				j->status=-5;
				update(j);
				exitClient(sysError);
			} else if(pid==0) {
				//This is the child process. Disable the inherited signal handler.
				deferSignals();
				//Set up jobArgs and run the command
				if(s==success && (s=setWatcherArgs(j,exp))==success) {
					/*                     for(t = jobArgs; t!=NULL; ++t) {
					 *                         printf("jobArgs: %s\n", t);
					 *                     }
					 */
					logComment(1,"------------------------------\n");
					if(execve("/bin/bash", jobArgs, NULL)==-1) {
						LOGERROR(AT,"Error in execve(): %s\n", strerror(errno));
						freeJobArgs();
						s=sysError;
					}
				}
				//Something is seriously wrong. Send the father process a signal indicating the error.
				j->status=-5;
				update(j);
				if(s==sysError) {
					if(kill(getppid(), SIGUSR1)!=0) {
						LOGERROR(AT,"Error in kill(): %s\n", strerror(errno));
					}
				} else {
					if(kill(getppid(), SIGUSR2)!=0) {
						LOGERROR(AT,"Error in kill(): %s\n", strerror(errno));
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

			exitClient(s);
		}
	}
	freeExperimentData(&exp);
	//Avoid compiler warnings
	return success;
}


