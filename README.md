EDACC GUI
=========

Experiment Design and Administration for Computer Clusters.
See http://sourceforge.net/projects/edacc/ for the EDACC project.

Installation
============

Copy the contents of edacc-<version>.tar.bz2 to a directory with
read/write permission.

Software Dependencies
=====================

EDACC depends on

* Sun Java 1.6
	
Optional dependencies

* R (see Experiment Mode - README.txt for more details)
	
Usage
=====

Compile via `ant jar`

To start EDACC simply double click the EDACC.jar file (Windows).
To start EDACC from the command line type `java -jar EDACC.jar`.

Client
======

This package provides a client binary for both 32 bit and 64 bit architectures.

Client binaries depend on

* glibc-2.3.2 / libc6 or higher

If you need a client for another architecture or the binary doesn't work for some
reason, you can simply compile the client on the destination machine using the
instructions provided by the client/README file.