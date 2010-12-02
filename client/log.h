#ifndef LOG_H
#define LOG_H

//#define LOGERROR(arg,...) logError(__FILE,__LINE___,arg,...)
#define STRINGIFY(x) #x
#define TOSTRING(x) STRINGIFY(x)
#define AT __FILE__ ":" TOSTRING(__LINE__)

void LOGERROR(const char *location,const char* format, ...);

void logComment(int v_level, const char* format, ...);


#endif

