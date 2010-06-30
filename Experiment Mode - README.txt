-- Setting up R --

To use the analyse tab in experiment mode you have to setup r as follows:

Linux:

* install r
* install rJava: in r type install.packages('rJava')
* install JavaGD: in r type install.packages('JavaGD')

The pathes here are just examples. Depending on your setup they might differ.

* export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/<username>/R/i686-pc-linux-gnu-library/2.11/rJava/jri/:/usr/lib/R/lib/:/home/<username>/R/i686-pc-linux-gnu-library/2.11/JavaGD/libs
-> unable to load R. -> set R_HOME
* export R_HOME=/usr/lib/R
* export R_LIBS=/home/<username>/R/i686-pc-linux-gnu-library/2.11/