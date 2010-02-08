#!/bin/bash

SOLVER=$1
ARGS=$2
INST_FILE=$3
RES_FILE=$4
TIMEOUT=$5

ulimit -S -t $TIMEOUT
/usr/bin/time -a -o "$RES_FILE" -f "%U;%x" "./"$SOLVER "$ARGS" "$INST_FILE" >> "$RES_FILE"
exit 0
