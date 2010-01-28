#include "log.h"

#include <stdarg.h>
#include <stdio.h>


void logError(const char* format, ...) {
	va_list args;

	va_start(args, format);
	vfprintf(stderr, format, args);
	va_end(args);
}

void logComment(const char* format, ...) {
	va_list args;

	va_start(args, format);
	vfprintf(stdout, format, args);
	va_end(args);
}

