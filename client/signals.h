#ifndef SIGNALS_H
#define SIGNALS_H

#include "global.h"


//Install handler as the default handler for all relevant signals
void setSignalHandler(void(*handler)(int));

//Ignore any of the relevant signals, but remember which ones are being received
void deferSignals();

//Restore the disposition before the last call to deferSignals() for all relevant signals.
//Then, raise the signals that were remembered since the last call of deferSignals().
//If deferSignals() has not been called before, the behaviour is undefined!
void resetSignalHandler();


#endif

