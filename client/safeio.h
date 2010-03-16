#ifndef SAFEIO_H
#define SAFEIO_H

#include <stdio.h>
#include <stdarg.h>

//A version of sprintf that sets str to newly allocated memory containing the output string.
//If the function succeeds (i.e. returns a non-negative value), the memory pointed to by str
//needs to be freed.
int sprintfAlloc(char** str, const char* format, ...);

//Below are some wrapper functions for the corresponding functions defined in stdio.h
//with limited disk access rate

int safeGetc(FILE *stream);

int safeFputc(int c, FILE *stream);

#endif

