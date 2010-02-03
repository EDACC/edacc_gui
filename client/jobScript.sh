#!/bin/bash

SOLVER=$1
INST_FILE=$2
RES_FILE=$3
TIMEOUT=$4

ulimit -S -t $TIMEOUT
/usr/bin/time -a -o $RES_FILE -f "%U;%x" "./"$SOLVER $INST_FILE >> $RES_FILE
exit 0
