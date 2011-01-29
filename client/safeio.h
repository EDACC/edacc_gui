#ifndef SAFEIO_H
#define SAFEIO_H

#include <stdio.h>
#include <stdarg.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include <sys/stat.h>
#include "log.h"
#include <errno.h>

//A version of sprintf that sets str to newly allocated memory containing the output string.
//If the function succeeds (i.e. returns a non-negative value), the memory pointed to by str
//needs to be freed.
int sprintfAlloc(char** str, const char* format, ...);

//Returns a unique file name based on pid. The string is located in static memory
//and must not be freed.
char* pidToFileName(pid_t p);

int fileExists(const char* fileName);

void checkPath();
void initPath();

//Prepend fileName by basename. On success, the function returns a pointer
//to the string in newly allocated memory that needs to be freed.
//If the memory allocation fails, the return value is NULL.
char* prependBasename(const char* fileName);

char* prependSolverPath(const char* fileName);

char* prependInstancePath(const char* fileName);

char* prependResultPath(const char* fileName);


//Below are some wrapper functions for the corresponding functions defined in stdio.h
//with limited disk access rate

int safeGetc(FILE *stream);

int safeFputc(int c, FILE *stream);

#endif

