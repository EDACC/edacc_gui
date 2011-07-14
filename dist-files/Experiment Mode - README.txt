
---------- Experiment Mode ----------

1.  Setting up R
1.1 Linux
1.2 Windows
1.3 Mac OS X

1.  Setting up R
---------------

To use the analyse tab in experiment mode you have to setup r as follows:

1.1 Linux
---------

* install r (use your package manager or get it from www.r-project.org)
* note: if installing packages fails you might have to run "R CMD javareconf" which will configure your java environment 
  settings for R.
* install rJava: in r type install.packages('rJava')
* install JavaGD: in r type install.packages('JavaGD')
* install np library for kernel density plot: in r type install.packages('np')

The paths here are just examples. Depending on your setup they might differ. (you can call .libPaths() in R which sould 
output the library directories)

* export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/<username>/R/i686-pc-linux-gnu-library/2.11/rJava/jri/:/usr/lib/R/lib/:
                                          /home/<username>/R/i686-pc-linux-gnu-library/2.11/JavaGD/libs
-> unable to load R. -> set R_HOME
* export R_HOME=/usr/lib/R
* export R_LIBS=/home/<username>/R/i686-pc-linux-gnu-library/2.11/


1.2 Windows
-----------

* install r (get it from www.r-project.org)
* install rJava: in r type install.packages('rJava')
* install JavaGD: in r type install.packages('JavaGD')
* install np library for kernel density plot: in r type install.packages('np')

locate the r, rJava\jri and JavaGD\libs directory, add them to your PATH and set the R_LIBS variable.

for example:
if C:\Program Files (x86)\R\R-2.11.1\bin is your R-directory,
C:\Users\<username>\Documents\R\win-library\2.11\rJava\jri is your rJava\jri directory and
C:\Users\<username>\Documents\R\win-library\2.11\JavaGD\libs is your JavaGD\libs directory you have to set the PATH variable to
PATH=<other PATH-variables>;C:\Program Files (x86)\R\R-2.11.1\bin;D:\Users\<username>\Documents\R\win-library\2.11\rJava\jri;
                            C:\Users\<username>\Documents\R\win-library\2.11\JavaGD\libs
and 
R_LIBS=C:\Users\<username>\Documents\R\win-library\2.11

To set these variables go to Control panel -> System -> System properties -> Advanced -> Environment variables 
and edit your existing PATH-variable and add the R_LIBS variable.

You will have to log out and log in again in order to let windows use your new variables.

1.3 Mac OS X
------------

* install r (get it from www.r-project.org)
* install rJava, JavaGD & np: 
    open R (in your applications folder in finder), select Packages & Data -> Package Installer, 
    Get List and search for and install them or use the method described in Linux section.
* set the environment variables
    in R type .libPaths(), this sould output the R library path which we call %R_LIB%.
    The directory %R_LIB%/rJava/jri should contain libjri.jnilib.
    Set your DYLD_LIBRARY_PATH:
      export DYLD_LIBRARY_PATH=$DYLD_LIBRARY_PATH:%R_LIB%/rJava/jri
