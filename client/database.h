#ifndef DATABASE_H
#define DATABASE_H

#include <stdlib.h>
#include <mysql/mysql.h>
#include "global.h"

#define QUERY_EXPERIMENT_INFO "" \
		"SELECT " \
		"       CPUTimeLimit, " \
		"       wallClockTimeLimit, " \
		"       memoryLimit, " \
		"       stackSizeLimit, " \
		"       outputSizeLimit, " \
		"       name " \
		"   FROM Experiment " \
		"   WHERE idExperiment = %i "

#define QUERY_GRID_SETTINGS "" \
		"SELECT " \
		"       numCPUs " \
		"   FROM gridQueue " \
		"   WHERE idgridQueue = %i " \

#define QUERY_INSTANCES "" \
		"SELECT " \
		"       i.idInstance, " \
		"       i.name, " \
		"       i.instance, " \
		"       i.md5 " \
		"   FROM ExperimentResults AS er " \
		"   LEFT JOIN Instances AS i " \
		"       ON er.Instances_idInstance = i.idInstance " \
		"   WHERE er.Experiment_idExperiment = %i " \
		"   GROUP BY i.idInstance "

#define QUERY_SOLVERS "" \
		"SELECT " \
		"       s.idSolver, " \
		"       s.name, " \
		"       s.binaryName, " \
		"       s.binary, " \
		"       s.md5 " \
		"   FROM ExperimentResults AS er " \
		"   LEFT JOIN SolverConfig AS sc " \
		"       ON er.SolverConfig_idSolverConfig = sc.idSolverConfig " \
		"   LEFT JOIN Solver AS s " \
		"       ON sc.Solver_idSolver = s.idSolver " \
		"   WHERE er.Experiment_idExperiment = %i " \
		"   GROUP BY s.idSolver "

/* Will be needed for the randomized job pick */
#define QUERY_EXPERIMENT_JOBS "" \
		"SELECT " \
		"       idJob " \
		"   FROM ExperimentResults " \
		"   WHERE status <0 " \
		"      AND Experiment_idExperiment = %i "
//This is the old one, that produced good results but was still buggy
//#define QUERY_RANDOM_JOB "SELECT idJob FROM ExperimentResults WHERE status<0 "\"AND Experiment_idExperiment= %d ORDER BY RAND() LIMIT 1 FOR UPDATE"

#define QUERY_RANDOM_JOB "SELECT idJob FROM ExperimentResults WHERE status<0 "\
				"AND Experiment_idExperiment= %d AND priority>=0 AND "\
				"priority= (SELECT MAX(priority) FROM ExperimentResults where Experiment_idExperiment=%d AND status<0) LIMIT 1 FOR UPDATE"

#define QUERY_LOCK_JOB "UPDATE ExperimentResults SET status=0 WHERE idJob= %i AND status<0"

#define QUERY_RANDOM_JOB1 "select Floor(rand()*count(idJob)) as ra  from ExperimentResults WHERE status=-1 "\
				"AND Experiment_idExperiment= %d AND priority>=0 "

#define QUERY_RANDOM_JOB2 "select idJob from ExperimentResults WHERE status=-1 AND Experiment_idExperiment= %d AND priority>=0 LIMIT %d,1 FOR UPDATE"

//TODO: der obige querey geht viel schneller wenn man folgendes verwendet:
// select Floor(rand()*count()) as ra from experimentResults where status...
// select idJob from experimentResults where status= ... limit ra,1;
//Unfortunately, variables cannot be used in the LIMIT clause,
//otherwise the entire thing could be done completely in SQL.

#define QUERY_JOB "" \
		"SELECT " \
		"       e.idJob, " \
		"       s.name, " \
		"       s.version, " \
		"       i.name, " \
		"       e.seed, " \
		"       e.solverOutputFN, " \
		"       e.launcherOutputFN, " \
		"       e.watcherOutputFN, " \
		"       e.verifierOutputFN " \
		"   FROM ExperimentResults AS e " \
		"   LEFT JOIN SolverConfig AS sc " \
		"       ON e.SolverConfig_idSolverConfig = sc.IdSolverConfig " \
		"   LEFT JOIN Solver AS s " \
		"       ON s.idSolver = sc.Solver_idSolver " \
		"   LEFT JOIN Instances AS i " \
		"       ON e.Instances_idInstance = i.idInstance " \
		"   WHERE e.idJob = %i "

/*#define QUERY_JOB "" \
		"SELECT " \
		"       e.idJob, " \
		"       s.name, " \
		"       s.version, " \
		"       i.name, " \
		"       e.seed, " \
		"       e.solverOutputFN, " \
		"       e.launcherOutputFN, " \
		"       e.watcherOutputFN, " \
		"       e.verifierOutputFN " \
		"   FROM ExperimentResults AS e " \
		"   LEFT JOIN SolverConfig AS sc " \
		"       ON e.SolverConfig_idSolverConfig = sc.IdSolverConfig " \
		"   LEFT JOIN Solver AS s " \
		"       ON s.idSolver = sc.Solver_idSolver " \
		"   LEFT JOIN Instances AS i " \
		"       ON e.Instances_idInstance = i.idInstance " \
		"   WHERE status<-1 limit %d,1 "
 */

#define QUERY_JOB_PARAMS "" \
		"SELECT " \
		"       p.name, " \
		"       p.prefix, " \
		"       p.hasValue, " \
		"       scp.value, " \
		"       `order` " \
		"   FROM ExperimentResults AS er " \
		"   LEFT JOIN SolverConfig_has_Parameters AS scp " \
		"       ON er.SolverConfig_idSolverConfig = scp.SolverConfig_idSolverConfig " \
		"   RIGHT JOIN Parameters AS p " \
		"       ON scp.Parameters_idParameter = p.idParameter " \
		"   WHERE er.idJob = %i " \
		"   ORDER BY `order` "

#define UPDATE_JOB ""           \
		"UPDATE ExperimentResults SET " \
		"       status = %i, "          \
		"       startTime = '%s', " \
		"       resultTime = %f, " \
		"       computeQueue = %d, " \
		"       launcherOutput = '%s' " \
		"   WHERE idJob = %i "

#define UPDATE_JOB_RESULTS ""           \
		"UPDATE ExperimentResults SET " \
		"       status = %d, "          \
		"       startTime = '%s', " \
		"       resultTime = %f, " \
		"       resultCode = %d, " \
		"       solverOutput = '%s', " \
		"       launcherOutput = '%s', " \
		"       watcherOutput = '%s', " \
		"       verifierOutput = '%s', " \
		"       solverExitCode = %d, " \
		"       watcherExitCode = %d, " \
		"       verifierExitCode = %d, " \
		"       computeQueue = %d " \
		"   WHERE idJob = %i "

#define QUERY_SOLVER "" \
		"SELECT " \
		"       `binary`, " \
		"       md5 " \
		"   FROM Solver " \
		"   WHERE (name = '%s' AND version = '%s')  "

#define QUERY_INSTANCE "" \
		"SELECT " \
		"       instance, " \
		"       md5 " \
		"   FROM Instances " \
		"   WHERE name = '%s' "

#define QUERY_TIME "" \
		"SELECT " \
		"  NOW()"

//Try to fetch all neede information about the experiment
//we want to execute from the database.
status dbFetchExperimentData(experiment *e);
void freeExperimentData(experiment *e);

//Try to fetch a new job for processing from the database.
//If all goes well, the function returns zero.
//If no job could be fetched, the return value is non-zero
//and s gives further information about the reason. s is set to
// - success if there's no job left an no error occured
// - sysError if an error in a system function occured
// - dbError if an error in the database occured.
int dbFetchJob(job* j, status* s);
void freeJob(job *j);

//Store information about the job j that just started running
status dbUpdate(const job* j);

//Store all information about the finished job
status dbUpdateResults(const job* j);

//Try to fetch the solver named solverName from the database
status dbFetchSolver(const char* solverName, const char* solverVersion,
		solver* s);

//Try to fetch the instance named instanceName from the database
status dbFetchInstance(const char* instanceName, instance* i);

void freeSolver(solver *s);
void freeInstance(instance *s);

status setMySQLTime(job *j);
#endif

