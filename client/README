EDACC Client
============

This is the computation client of the EDACC (Experiment Design and Analysis for Computer Clusters)
system. See http://sourceforge.net/projects/edacc/ for more info.

After creating experiments with the EDACC GUI Application the client can be started on
large computer clusters and grids to process the generated computation jobs in a multi-processing,
distributed environment.

Dependencies
------------

- GCC
- zlib
- MySQL libraries: The compilation requires the MySQL C Connector development libraries.
This includes a working mysql_config command which will point GCC to the
directories with the necessary header files and libraries.
Ideally you should compile the client in the target environment, otherwise
you can consider a static compilation which might work if the build and target
systems are not too different.

Compilation
-----------

Run "make" in the top level directory. Binaries are put into bin/.

Usage
-----

Use ./client --help to see a list of command line options the binary accepts.
Configure the database settings in the 'config' text file and make sure
"runsolver" and verifier binaries that are specified in the configuration file exist
in the correct paths. The client will call these programs with paths relative to the
working(!) directory at runtime.
Furthermore, the client has to be able to create directories and write files in either the working directory
or the directory that can be specified as "base path" (see --help).

The EDACC client is is able to run on any system where individual nodes can access the EDACC
database, i.e. establish a TCP connection. If direct internet access from the nodes is not
possible, this can often be achieved by tunneling to the database server over the cluster's login node via SSH.
