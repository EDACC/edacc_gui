#include "safeio.h"

#include <unistd.h>
#include <stdlib.h>
#include <time.h>

//The maximal disk access rate in bytes per seconds for safeGetc and safeFputc
const long bps=190*1000;

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

int safeGetc(FILE *stream) {
	int retval;
	struct timespec req;

	req.tv_sec=0;
	req.tv_nsec=1000000L/((long)bps/1000L)+1L;

	retval=getc(stream);
	if(nanosleep(&req, NULL)!=0)
		return EOF;

	return retval;
}

int safeFputc(int c, FILE *stream) {
	int retval;
	struct timespec req;

	req.tv_sec=0;
	req.tv_nsec=1000000L/((long)bps/1000L)+1L;

	retval=fputc(c, stream);
	if(nanosleep(&req, NULL)!=0)
		return EOF;
	
	return retval;
}

