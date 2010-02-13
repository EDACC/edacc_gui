#include "safeio.h"

#include <stdarg.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <time.h>

//The maximal disk access rate in bytes per seconds
const size_t bps=190*1000;

int safeFprintf(FILE* stream, const char* format, ...) {
	va_list args;
	int retval;
	char* buf=NULL;

	//Use vsnprintf to calculate the length of the expanded format string
	va_start(args, format);
	retval=vsnprintf(buf, 0, format, args);
	va_end(args);
	if(retval<0)
		return -1;

	//Allocate a buffer of the correct size
	buf=(char*)malloc(retval);
	if(buf==NULL)
		return -1;

	//Use vsnprintf to write the expanded format string to buf
	va_start(args, format);
	retval=vsnprintf(buf, retval, format, args);
	va_end(args);
	if(retval<0) {
		free(buf);
		return -1;
	}

	//Write buf to stream
	retval=safeFwrite(buf, sizeof(char), retval, stream);
	free(buf);

	return retval;
}

size_t safeFwrite(const void* ptr, size_t size, size_t nmemb, FILE* stream) {
	size_t itemsWritten, retval=0, itemsToWrite=bps/size;
	char* data=(char*)ptr;

	//Write at most bps/size bytes per second to stream
	do {
		if(nmemb<itemsToWrite)
			itemsToWrite=nmemb;
		itemsWritten=fwrite(data, size, itemsToWrite, stream);
		retval+=itemsWritten;
		nmemb-=itemsWritten;
		data+=itemsWritten*size;
		sleep(1);
	} while(nmemb>0 && itemsWritten==itemsToWrite);

	return retval;
}

int safeFscanf(FILE* stream, const char* format, ...) {
	long currPos, endPos;
	char* buf;
	char* bufptr;
	size_t len, itemsRead, itemsToRead=bps/sizeof(char);
	va_list args;
	int retval;

	//Set len to the number of bytes left in stream
	currPos=ftell(stream);
	if(fseek(stream, 0L, SEEK_END)!=0)
		return EOF;
	endPos=ftell(stream);
	if(fseek(stream, currPos, SEEK_SET)!=0)
		return EOF;
	len=(size_t)(endPos-currPos);

	//Allocate a buffer for storing the bytes left in stream
	buf=(char*)malloc(len+1);
	if(buf==NULL)
		return EOF;
	buf[len]='\0';
	bufptr=buf;

	//Read at most bps/sizeof(char) bytes per second into buf
	do {
		if(len<itemsToRead)
			itemsToRead=len;
		itemsRead=fread(bufptr, sizeof(char), itemsToRead, stream);
		if(itemsRead!=itemsToRead) {
			free(buf);
			fseek(stream, currPos, SEEK_SET);
			return EOF;
		}
		len-=itemsRead;
		bufptr+=itemsRead;
		sleep(1);
	} while(len>0);

	if(fseek(stream, currPos, SEEK_SET)!=0) {
		free(buf);
		return EOF;
	}

	//Scan buf using the format string format
	va_start(args, format);
	retval=vsscanf(buf, format, args);
	va_end(args);

	free(buf);
	return retval;
}

int safeGetc(FILE *stream) {
	int retval;
	struct timespec req;

	req.tv_sec=0;
	req.tv_nsec=1000000L/((long)bps/1000L)+1;

	retval=getc(stream);
	if(nanosleep(&req, NULL)!=0)
		return EOF;

	return retval;
}

int safeFputc(int c, FILE *stream) {
	int retval;
	struct timespec req;

	req.tv_sec=0;
	req.tv_nsec=1000000L/((long)bps/1000L)+1;

	retval=fputc(c, stream);
	if(nanosleep(&req, NULL)!=0)
		return EOF;
	
	return retval;
}

