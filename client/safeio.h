#ifndef SAFEIO_H
#define SAFEIO_H

#include <stdio.h>

//Wrapper functions for the corresponding functions defined in stdio.h
//with limited disk access rate

int safeFprintf(FILE* stream, const char* format, ...);

size_t safeFwrite(const void* ptr, size_t size, size_t nmemb, FILE* stream);

int safeFscanf(FILE* stream, const char* format, ...);

int safeGetc(FILE *stream);

int safeFputc(int c, FILE *stream);

#endif

