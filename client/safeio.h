#ifndef SAFEIO_H
#define SAFEIO_H

#include <stdio.h>

//Wrapper functions for the corresponding functions defined in stdio.h
//with limited disk access rate and mutual file access exclusion across
//different application instances implemented with a named semaphore

FILE* safeFopen(const char* path, const char* mode);

int safeFprintf(FILE* stream, const char* format, ...);

size_t safeFwrite(const void* ptr, size_t size, size_t nmemb, FILE* stream);

int safeFscanf(FILE* stream, const char* format, ...);

int safeGetc(FILE *stream);

int safeFputc(int c, FILE *stream);

//This function unlinks the named semaphore and has to be called after the last
//call to one of the wrapper function above.
void safeioUnlink();

#endif

