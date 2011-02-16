#include "safeio.h"



//The maximal disk access rate in bytes per seconds for safeGetc and safeFputc
const long bps=190*1000;


//The path where we want to create solvers, instances and resultFiles files
static char* basename="./";
static char* solverPath = "solvers/";
static char* instancePath = "instances/";
static char* resultPath = "results/";
static int  solverPathLen,instancePathLen,resultPathLen;
//The length of basename without the terminating '\0' character
static int basenameLen;

//Test if a file exists

void checkPath(){
	if (!fileExists(prependBasename(solverPath))){
		mkdir(prependBasename(solverPath), S_IRWXU | S_IRWXG | S_IRWXO);
		logComment(4,"created folder: %s\n",prependBasename(solverPath));
	}
	if (!fileExists(prependBasename(instancePath))){
		mkdir(prependBasename(instancePath), S_IRWXU | S_IRWXG | S_IRWXO);
		logComment(4,"created folder: %s\n",prependBasename(instancePath));
	}
	if (!fileExists(prependBasename(resultPath))){
		mkdir(prependBasename(resultPath), S_IRWXU | S_IRWXG | S_IRWXO);
		logComment(4,"created folder: %s\n",prependBasename(resultPath));
	}
	return;
}

void initPath(){
	basenameLen=strlen(basename);
	solverPathLen=strlen(solverPath);
	instancePathLen=strlen(instancePath);
	resultPathLen=strlen(resultPath);
	logComment(1,"%20s : \n","PATH variables");
	logComment(1,"%20s : %s\n","base path", basename);
	logComment(1,"%20s : %s\n","solver path",solverPath);
	logComment(1,"%20s : %s\n","instance path",instancePath);
	logComment(1,"%20s : %s\n","result path",resultPath);
	logComment(1,"------------------------------\n");
}

int sprintfAlloc(char** str, const char* format, ...) {
	va_list args;
	int retval;

	//Use vsnprintf to calculate the length of the expanded format string
	va_start(args, format);
	retval=vsnprintf(*str, 0, format, args);
	va_end(args);
	if(retval<0)
		return -1;

	//Allocate memory of the correct size
	*str=(char*)malloc(retval+1);
	if(*str==NULL)
		return -1;

	//Use vsnprintf to write the expanded format string to *str
	va_start(args, format);
	retval=vsnprintf(*str, retval+1, format, args);
	va_end(args);
	if(retval<0)
		free(*str);

	return retval;
}

//Returns a unique file name based on pid. The string is located in static memory
//and must not be freed.
char* pidToFileName(pid_t pid) {
	static char fileName[40];
	char* host;
	host = getenv("HOSTNAME");
	snprintf(fileName, 40, "%s_%ld.tmp",host, (long)pid); //TODO: Sollte heissen Node.pid (pid ist nur fuer ein node eindeutig)
	return fileName;
}

//Prepend fileName by basename. On success, the function returns a pointer
//to the string in newly allocated memory that needs to be freed.
//If the memory allocation fails, the return value is NULL.
char* prependBasename(const char* fileName) {
	char* str;
	int fileNameLen=strlen(fileName);

	str=malloc(basenameLen+fileNameLen+1);
	if(str==NULL)
		return NULL;
	strcpy(str, basename);
	strcpy(str+basenameLen, fileName);
	str[basenameLen+fileNameLen]='\0';

	return str;
}

char* prependSolverPath(const char* fileName) {
	char* str;

	int fileNameLen=strlen(fileName);

	str=malloc(solverPathLen+fileNameLen+1);
	if(str==NULL)
		return NULL;
	strcpy(str, solverPath);
	strcpy(str+solverPathLen, fileName);
	str[solverPathLen+fileNameLen]='\0';

	return prependBasename(str);
}

char* prependInstancePath(const char* fileName) {
	char* str;
	int fileNameLen=strlen(fileName);

	str=malloc(instancePathLen+fileNameLen+1);
	if(str==NULL)
		return NULL;
	strcpy(str, instancePath);
	strcpy(str+instancePathLen, fileName);
	str[instancePathLen+fileNameLen]='\0';

	return prependBasename(str);
}

char* prependResultPath(const char* fileName) {
	char* str;
	int fileNameLen=strlen(fileName);

	str=malloc(resultPathLen+fileNameLen+1);
	if(str==NULL)
		return NULL;
	strcpy(str,resultPath);
	strcpy(str+resultPathLen, fileName);
	str[resultPathLen+fileNameLen]='\0';

	return prependBasename(str);
}

int safeGetc(FILE *stream) {
	/*	int retval;
	struct timespec req;

	req.tv_sec=0;
	req.tv_nsec=1000000L/((long)bps/1000L)+1L;

	retval=getc(stream);
	if(nanosleep(&req, NULL)!=0)
		return EOF;

	return retval;*/
	return getc(stream);
}

int safeFputc(int c, FILE *stream) {
	/*	int retval;
	struct timespec req;

	req.tv_sec=0;
	req.tv_nsec=1000000L/((long)bps/1000L)+1L;

	retval=fputc(c, stream);
	if(nanosleep(&req, NULL)!=0)
		return EOF;

	return retval;*/
	return fputc(c, stream);
}

