#!/bin/bash

SOLVER=$1
ARGS=$2
INST_FILE=$3
RES_FILE=$4
TIMEOUT=$5

cd $6

ulimit -S -t $TIMEOUT

if [ -n "$ARGS" ]; then
	/usr/bin/time -a -o "$RES_FILE" -f "%U;%x" "$SOLVER" "$INST_FILE" >> "$RES_FILE"
else
	/usr/bin/time -a -o "$RES_FILE" -f "%U;%x" "$SOLVER" "$ARGS" "$INST_FILE" >> "$RES_FILE"
fi

exit 0
