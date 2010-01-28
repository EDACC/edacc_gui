#include "signals.h"
#include "global.h"
#include "log.h"

#include <signal.h>
#include <stdlib.h>


static const int signalsLen=10;
static const int signals[10]={SIGHUP, SIGINT, SIGQUIT, SIGABRT, SIGSEGV, SIGTERM, SIGUSR1, SIGUSR2, SIGXCPU, SIGXFSZ};
static int signalsPending[10];
static struct sigaction oldActions[10];


static void rememberSignal(int signum) {
	int i;
	for(i=0; i<signalsLen; ++i) {
		if(signals[i]==signum) {
			signalsPending[i]=1;
			return;
		}
	}
}

void setSignalHandler(void(*handler)(int)) {
	int i;
	struct sigaction newAction;

	//Initialize newAction
	newAction.sa_handler=handler;
	sigemptyset(&newAction.sa_mask);
	newAction.sa_flags=0;

	//Install newAction as the signal handler for all signals in the signals array
	for(i=0; i<signalsLen; ++i) {
		sigaction(signals[i], &newAction, NULL);
	}
}

void deferSignals() {
	int i;
	struct sigaction newAction;

	//Set the signalsPending array to 0
	for(i=0; i<signalsLen; ++i) {
		signalsPending[i]=0;
	}

	//Initialize newAction
	newAction.sa_handler=rememberSignal;
	sigemptyset(&newAction.sa_mask);
	newAction.sa_flags=0;

	//Install newAction as the signal handler for all signals in the signals array
	//and remember the previous signal handlers in the oldActions array
	for(i=0; i<signalsLen; ++i) {
		sigaction(signals[i], &newAction, &(oldActions[i]));
	}
}

void resetSignalHandler() {
	int i;

	//Install the handlers in the oldActions array for all signals in the signals array
	for(i=0; i<signalsLen; ++i) {
		sigaction(signals[i], &(oldActions[i]), NULL);
	}

	//Raise all signals remembered in the signalsPending array
	for(i=0; i<signalsLen; ++i) {
		if(signalsPending[i]) {
			raise(signals[i]);
		}
	}
}

