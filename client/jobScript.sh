#!/bin/bash

rm -f tmp.$$.txt
ulimit -t 2000
/usr/bin/time -a -o tmp.$$.txt -f "%U" ./Sparrow025_sp35 unif-k3-r4.2-v8000-c33600-S1760662955-050.cnf >> tmp.$$.txt
