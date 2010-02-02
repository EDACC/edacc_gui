#!/bin/bash

md5sum -c $1 > /dev/null 2>&1
exit $?
