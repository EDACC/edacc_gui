#include "log.h"
#include "global.h"

#include <stdarg.h>
#include <stdio.h>


void LOGERROR(const char *location,const char* format, ...) {
	va_list args;

	va_start(args, format);
	fprintf(stderr,"ERROR at %s:",location);
	vfprintf(stderr, format, args);
	va_end(args);
}


/*void logLauncher(job* j,char** str, const char* format, ...) {
	va_list args;
	sprintfAlloc(&j->launcherOutput, "%s %s", j->launcherOutput, str);

	if (v_level<=verbosity){
	va_start(args, format);
	vfprintf(stdout, format, args);
	fflush(stdout);
	va_end(args);
	}
}*/

void logComment(int v_level,const char* format, ...) {
	va_list args;
	if (v_level<=verbosity){
	va_start(args, format);
	vfprintf(stdout, format, args);
	fflush(stdout);
	va_end(args);
	}
}

/*
void logComment(const char* format, ...) {
		va_list args;
//		if (v_level>=verbosity){
		va_start(args, format);
		vfprintf(stdout, format, args);
		va_end(args);
	//	}
*/


