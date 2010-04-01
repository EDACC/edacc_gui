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

//This function is for debugging. If after a crash of the application any of the
//functions above make problems, this function can be called in order to remove
//anything that might have been left in the system from the errorneous run.
//Hopefully, the functions above should work again afterwards ;)
void cleanMutex();

#endif

