#include "safeio.h"

#include <fcntl.h>
#include <sys/stat.h>
#include <semaphore.h>
#include <stdarg.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <time.h>

//The semaphore used for mutual file access exclusion
static const char* const semName="clientLock";
static sem_t* sem=NULL;

//The maximal disk access rate in bytes per seconds
const size_t bps=190*1000;

static inline int lock() {
	if(sem==NULL) {
		sem=sem_open(semName, O_CREAT, 0600, 1);
		if(sem==SEM_FAILED)
			return -1;
	}
	return sem_wait(sem);
}

static inline int unlock() {
	return sem_post(sem);
}

FILE* safeFopen(const char* path, const char* mode) {
	FILE* retval;

	if(lock()!=0)
		return NULL;
	retval=fopen(path, mode);
	if(unlock()!=0)
		return NULL;

	return retval;
}

int safeFprintf(FILE* stream, const char* format, ...) {
	va_list args;
	int retval, buflen=1;
	char* buf;
	char* formatPtr;

	//Calculate an upper bound of the length of the expanded format string format
	for(formatPtr=(char*)format; *formatPtr!='\0'; ++formatPtr) {
		if(*formatPtr=='%')
			buflen+=50;
		else
			++buflen;
	}

	//Allocate the buffer
	buf=(char*)malloc(buflen);
	if(buf==NULL)
		return -1;

	//Write the expanded format string format to buf
	va_start(args, format);
	retval=vsnprintf(buf, buflen, format, args);
	va_end(args);
	if(retval<0 || retval>=buflen) {
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
	void* data=(void*)ptr;

	if(lock()!=0)
		return 0;

	//Write at most bps/size bytes per second to stream
	do {
		if(nmemb<itemsToWrite)
			itemsToWrite=nmemb;
		itemsWritten=fwrite(data, size, itemsToWrite, stream);
		retval+=itemsWritten;
		nmemb-=itemsWritten;
		data=(char*)data+size;
		sleep(1);
	} while(nmemb>0 && itemsWritten==itemsToWrite);

	if(unlock()!=0)
		return 0;

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

	if(lock()!=0) {
		free(buf);
		return EOF;
	}

	//Read at most bps/sizeof(char) bytes per second into buf
	do {
		if(len<itemsToRead)
			itemsToRead=len;
		itemsRead=fread(bufptr, sizeof(char), itemsToRead, stream);
		if(itemsRead!=itemsToRead) {
			free(buf);
			unlock();
			return EOF;
		}
		len-=itemsRead;
		bufptr+=itemsRead;
		sleep(1);
	} while(len>0);

	if(unlock()!=0) {
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

	if(lock()!=0) {
		return EOF;
	}
	retval=getc(stream);
	if(nanosleep(&req, NULL)!=0) {
		unlock();
		return EOF;
	}
	if(unlock()!=0) {
		return EOF;
	}

	return retval;
}

int safeFputc(int c, FILE *stream) {
	int retval;
	struct timespec req;

	req.tv_sec=0;
	req.tv_nsec=1000000L/((long)bps/1000L)+1;

	if(lock()!=0) {
		return EOF;
	}
	retval=fputc(c, stream);
	if(nanosleep(&req, NULL)!=0) {
		unlock();
		return EOF;
	}
	if(unlock()!=0) {
		return EOF;
	}

	return retval;
}

