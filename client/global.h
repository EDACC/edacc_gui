#ifndef GLOBAL_H
#define GLOBAL_H

#include <sys/types.h>

#define VALUELENGTH 64
int verbosity;

/* experiment info */
int numRuns;
int timeOut;
int autoGeneratedSeeds;
char *name;

/* grid info */
int numCPUs;
int maxRuntime;
int maxJobsInQueue;

/* config file values */
char *host;
char *username;
char *password;
char *database;
unsigned int port;
int experimentId;
int gridQueueId;

typedef enum {success=0, sysError, dbError} status;

typedef struct {
    int id; //experiment id
    int numCPUs; // number of cpus
    int timeOut; // timeout for each solver run in seconds
    int numInstances; // number of used instances
    char **md5Instances; // md5 sums of instance files
    // the instances, manually appended with 0x00,
    // because column type in table is mediumblob
    char **instances;
    char **instanceNames; //the name of the instances
    /* This is probably not needed anymore
    int *idInstances; // the instances ids*/
    int numSolvers; // number of used solvers
    int *lengthSolver; // length of each solver
    char **md5Solvers; // the md5 sums for each solver
    // the binary for each solver, NOT terminated by 0x00
    char **solvers;
    char **solverNames; // the names for each solver
} experiment;

typedef struct {
    //Needed for the process handling
    pid_t pid;
    // identifies the row in the result table
    int id;
    // temporary filename for the results of one SolverConfig.
    // the file pattern is something like:
    // <solvername>_<instance>_<solverconfigid>.cnf
    char *resultFileName;
    // the status of the run
    // -2: an error occured, -1: not started, 0: running, 1: finished normaly by solver,
    // 2: terminated by ulimit maxtime, 3: terminated by ulimit maxmem
    int status;
    // int idSolverConfig;  needed?
    int seed;  // saves what seed was used
    float time; // the runtime of the solver
    char *solverName; // the used solver for this job.
    char params[256]; // the full param string
    char *instanceName; //the name of the instance
    char *resultFile; // the output of the solver (stdout and stderr)
    /* This is probably not needed anymore
    int idInstance; // the id of the used instance*/
    int statusCode; // solver return value, when the run is finished
    char startTime[7]; //The time the run started as a '\0' terminated string
} job;

typedef struct {
    int length; // length of the solver
    char *md5; // the md5 sum of the solver
    // the binary of the solver, NOT terminated by 0x00
    char *solver;
} solver;

typedef struct {
    char *md5; // md5 sum of the instance file
    // the instance, manually appended with 0x00,
    // because column type in table is mediumblob
    char *instance;
} instance;

#endif
