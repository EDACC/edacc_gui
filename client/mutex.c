#include "mutex.h"
#include "log.h"

#include <fcntl.h>
#include <sys/stat.h>
#include <semaphore.h>
#include <string.h>
#include <errno.h>

//The semaphore used for mutual exclusion
static const char* const mutexName="clientLock";
static sem_t* mutex=NULL;

//The semaphore used for the reference counting
static const char* const refCntName="clientRefCnt";
static sem_t* refCnt=NULL;

//This variable is 1 if and only if this process currently has the lock
static int haveLock=0;

status lockMutex() {
	if(refCnt==NULL) {
		//Open the semaphores, creating them if needed
		mutex=sem_open(mutexName, O_CREAT, 0600, 1);
		if(mutex==SEM_FAILED) {
			mutex=NULL;
			logError("Unable to open posix semaphore named %s: %s\n", mutexName, strerror(errno));
			return sysError;
		}
		refCnt=sem_open(refCntName, O_CREAT, 0600, 0);
		if(refCnt==SEM_FAILED) {
			refCnt=NULL;
			logError("Unable to open posix semaphore named %s: %s\n", refCntName, strerror(errno));
			return sysError;
		}
		//Increment refCnt
		if(sem_post(refCnt)==-1) {
			logError("Unable to increment posix semaphore named %s: %s\n", refCntName, strerror(errno));
			return sysError;
		}
	}
	//Lock mutex
	if(sem_wait(mutex)==-1) {
		logError("Error in sem_wait() for semaphore named %s: %s\n", mutexName, strerror(errno));
		return sysError;
	}
	haveLock=1;

	return success;
}

status unlockMutex() {
	if(sem_post(mutex)==-1) {
		logError("Error in sem_post() for semaphore named %s: %s\n", mutexName, strerror(errno));
		return sysError;
	}
	haveLock=0;
	return success;
}

void unrefMutex() {
	int numRefs;

	if(haveLock)
		unlockMutex();

	if(refCnt!=NULL) {
		//Both mutex and refCnt are opened
		if(sem_getvalue(refCnt, &numRefs)==-1) {
			logError("Error in sem_getvalue() for semaphore named %s: %s\n", refCntName, strerror(errno));
			//Something is seriously wrong. Try to destroy the semaphores.
			sem_close(mutex);
			sem_unlink(mutexName);
			sem_close(refCnt);
			sem_unlink(refCntName);
		} else if(numRefs==1) {
			//This is the last process having mutex opened. Destroy the semaphores.
			if(sem_close(mutex)==-1)
				logError("Error in sem_close() for semaphore named %s: %s\n", mutexName, strerror(errno));
			if(sem_unlink(mutexName)==-1)
				logError("Error in sem_unlink() for semaphore named %s: %s\n", mutexName, strerror(errno));
			if(sem_close(refCnt)==-1)
				logError("Error in sem_close() for semaphore named %s: %s\n", refCntName, strerror(errno));
			if(sem_unlink(refCntName)==-1)
				logError("Error in sem_unlink() for semaphore named %s: %s\n", refCntName, strerror(errno));
		} else {
			//There are other processes having mutex opened at this time. Decrement refCnt.
			if(sem_trywait(refCnt)==-1) {
				logError("Error in sem_trywait() for semaphore named %s: %s\n", refCntName, strerror(errno));
				//Something is seriously wrong. Try to destroy the semaphores.
				sem_close(mutex);
				sem_unlink(mutexName);
				sem_close(refCnt);
				sem_unlink(refCntName);
			} else {
				//Close the semaphores, but don't destroy them.
				if(sem_close(mutex)==-1)
					logError("Error in sem_close() for semaphore named %s: %s\n", mutexName, strerror(errno));
				if(sem_close(refCnt)==-1)
					logError("Error in sem_close() for semaphore named %s: %s\n", refCntName, strerror(errno));
			}
		}
	} else if(mutex!=NULL) {
		//mutex is opened but refCnt is not. Try to destroy mutex.
		sem_close(mutex);
		sem_unlink(mutexName);
	}
}

void cleanMutex() {
	logComment(4,"Trying to close all mutex...");
	sem_close(mutex);
	sem_unlink(mutexName);
	sem_close(refCnt);
	sem_unlink(refCntName);
	logComment(4," closed!\n");

}

