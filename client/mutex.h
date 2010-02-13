#ifndef MUTEX_H
#define MUTEX_H

#include "global.h"

//These functions provide mutual exclusion among different
//instances of the application. Code between lockMutex() and
//unlockMutex() will only be run by one instance at a time.

status lockMutex();

status unlockMutex();

//This function has to be called once before the application terminates.
//Afterwards, the functions lockMutex() or unlockMutex() shouldn't be
//called anymore.
void unrefMutex();

#endif

