#include <string.h>
#include <time.h>
#include <stdio.h>
#include "database.h"
#include "global.h"
#include "log.h"
#include "safeio.h"



#define QUERY_EXPERIMENT_INFO "" \
	"SELECT " \
	"       timeOut " \
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
	"   WHERE (status = -1 OR status = -2) " \
	"      AND Experiment_idExperiment = %i "


#define QUERY_JOB "" \
	"SELECT " \
	"       e.idJob, " \
	"       s.binaryName, " \
	"       i.name, " \
	"       e.resultFileName " \
	"   FROM ExperimentResults AS e " \
	"   LEFT JOIN SolverConfig AS sc " \
	"       ON e.SolverConfig_idSolverConfig = sc.IdSolverConfig " \
	"   LEFT JOIN Solver AS s " \
	"       ON s.idSolver = sc.Solver_idSolver " \
	"   LEFT JOIN Instances AS i " \
	"       ON e.Instances_idInstance = i.idInstance " \
	"   WHERE e.idJob = %i "


#define QUERY_JOB_PARAMS "" \
	"SELECT " \
	"       p.name, " \
	"       p.prefix, " \
	"       p.hasValue, " \
	"       p.value, " \
	"       scp.value " \
	"   FROM ExperimentResults AS er " \
	"   LEFT JOIN SolverConfig AS sc " \
	"       ON er.SolverConfig_idSolverConfig = sc.idSolverConfig " \
	"   LEFT JOIN Solver AS s " \
	"       ON sc.Solver_idSolver = s.idSolver " \
	"   LEFT JOIN Parameters AS p " \
	"       ON p.Solver_idSolver = s.idSolver " \
	"   LEFT JOIN SolverConfig_has_Parameters AS scp " \
	"       ON p.idParameter = scp.Parameters_idParameter " \
	"   WHERE er.idJob = %i "



#define UPDATE_JOB ""           \
	"UPDATE ExperimentResults SET " \
	"       resultFileName = '%s', "  \
	"       status = %i, "          \
	"       seed = %i, "            \
	"       time = %f, "            \
	"       statusCode = %i, "      \
	"       resultFile = '%s', "       \
	"       startTime = '%s' " \
	"   WHERE idJob = %i "


#define QUERY_SOLVER "" \
	"SELECT " \
	"       binary, " \
	"       md5 " \
	"   FROM Solver " \
	"   WHERE name = '%s' "

#define QUERY_INSTANCE "" \
	"SELECT " \
	"       instance, " \
	"       md5 " \
	"   FROM Instances " \
	"   WHERE name = '%s' "


status dbFetchExperimentData(experiment *e) {
	MYSQL *conn;
	MYSQL_RES *res;
	MYSQL_ROW row;

	if (mysql_library_init(0, NULL, NULL)) {
		logError("could not initialize MySQL library\n");
		return dbError;
	}


	char *queryExperimentInfo = NULL;
	char *queryGridInfo = NULL;
	char *querySolver = NULL;
	char *queryInstance = NULL;

	int i;
	unsigned long *lengths = NULL;

	e->md5Instances = NULL;
	e->instances = NULL;
	e->instanceNames = NULL;

	e->lengthSolver = NULL;
	e->md5Solvers = NULL;
	e->solvers = NULL;
	e->solverNames = NULL;

	e->id=experimentId;

	sprintfAlloc(&queryExperimentInfo, QUERY_EXPERIMENT_INFO, experimentId);
	sprintfAlloc(&queryGridInfo, QUERY_GRID_SETTINGS, gridQueueId);
	sprintfAlloc(&querySolver, QUERY_SOLVERS, experimentId);
	sprintfAlloc(&queryInstance, QUERY_INSTANCES, experimentId);

	conn = mysql_init(NULL);

	if(mysql_real_connect(conn, host, username, password, database, 0, NULL, 0) == NULL) {
		logError("could not establish a mysql connection!\n error message: %s",mysql_error(conn));
		return dbError;
	}
	logComment(1,"mysql-connection established!\n");


	/* fetch experiment information */
	if(mysql_query(conn, queryExperimentInfo) != 0) {
		return dbError;
		logError("DB error message: %s",mysql_error(conn));
		//TODO:Error Meldung?
	}

	if((res = mysql_store_result(conn)) == NULL) {
		return dbError;
		logError("DB error message: %s",mysql_error(conn));
		//TODO:Error Meldung?
	}

	if((row = mysql_fetch_row(res)) != NULL) {
		e->timeOut = atoi(row[0]);
		//TODO:Error Meldung?
	}


	/* fetch grid information */
	if(mysql_query(conn, queryGridInfo) != 0) {
		mysql_free_result(res);
		logError("DB error message: %s",mysql_error(conn));
		return dbError;
	}

	mysql_free_result(res);
	if((res = mysql_store_result(conn)) == NULL) {
		logError("DB error message: %s",mysql_error(conn));
		return dbError;
	}

	if((row = mysql_fetch_row(res)) != NULL) {
		e->numCPUs = atoi(row[0]);
	}


	/* fetch instances */
	if(mysql_query(conn, queryInstance) != 0) {
		mysql_free_result(res);
		logError("DB error message: %s",mysql_error(conn));
		return dbError;

	}

	mysql_free_result(res);
	if((res = mysql_store_result(conn)) == NULL) {
		logError("DB error message: %s",mysql_error(conn));
		return dbError;
	}

	e->numInstances = mysql_num_rows(res);

	if((e->md5Instances = malloc(e->numInstances*sizeof(char *))) == NULL) {
		mysql_free_result(res);
		logError("out of memory!\n");
		return sysError;
	}

	if((e->instances = malloc(e->numInstances*sizeof(char *))) == NULL) {
		mysql_free_result(res);
		logError("out of memory!\n");
		return sysError;
	}

	if((e->instanceNames = malloc(e->numInstances*sizeof(char *))) == NULL) {
		mysql_free_result(res);
		logError("out of memory!\n");
		return sysError;
	}

	i=0;
	while((row = mysql_fetch_row(res)) != NULL) {
		lengths = mysql_fetch_lengths(res);
		//TODO: Fehler wegen callocs
		e->md5Instances[i] = malloc(60*sizeof(char));
		strncpy(e->md5Instances[i], row[3], 60);

		/* Size + 1, to fit the lest nullbyte */
		e->instances[i] = calloc(lengths[2]+1,sizeof(char));
		memcpy(e->instances[i], row[2], lengths[2]);

		e->instanceNames[i] = calloc(lengths[1],sizeof(char*));
		strncpy(e->instanceNames[i], row[1], lengths[1]);

		i++;
	}



	/* fetch solver binaries */
	if(mysql_query(conn, querySolver) != 0) {
		mysql_free_result(res);
		logError("DB error message: %s",mysql_error(conn));
		return dbError;
	}

	mysql_free_result(res);
	if((res = mysql_store_result(conn)) == NULL) {
		logError("DB error message: %s",mysql_error(conn));
		return dbError;
	}

	e->numSolvers = mysql_num_rows(res);

	if((e->lengthSolver = malloc(e->numSolvers*sizeof(char *))) == NULL) {
		mysql_free_result(res);
		return sysError;
	}

	if((e->md5Solvers = malloc(e->numSolvers*sizeof(char *))) == NULL) {
		mysql_free_result(res);
		return sysError;
	}

	if((e->solvers = malloc(e->numSolvers*sizeof(char *))) == NULL) {
		mysql_free_result(res);
		return sysError;
	}

	if((e->solverNames = malloc(e->numSolvers*sizeof(char *))) == NULL) {
		mysql_free_result(res);
		return sysError;
	}

	i=0;
	while((row = mysql_fetch_row(res)) != NULL) {
		lengths = mysql_fetch_lengths(res);
//TODO: allocs fehler abfangen
		e->lengthSolver[i] = lengths[3];

		e->md5Solvers[i] = (char *)malloc(60*sizeof(char));
		strncpy(e->md5Solvers[i], row[4], 60);

		e->solvers[i] = (char *)malloc(lengths[3]*sizeof(char));
		memcpy(e->solvers[i], row[3], lengths[3]);

		if((e->solverNames[i] = calloc(lengths[2],sizeof(char))) == NULL) {
			mysql_free_result(res);
			return sysError;
		}
		strncpy(e->solverNames[i], row[2], lengths[2]);

		i++;
	}

	mysql_free_result(res);
	mysql_close(conn);
	mysql_library_end();
	return success;
}

void freeExperimentData(experiment *e) {
	int i;

	for(i=0; i<e->numInstances; i++) {
		if(e->md5Instances[i] != NULL) {
			free(e->md5Instances[i]);
		}

		if(e->instances[i] != NULL) {
			free(e->instances[i]);
		}

		if(e->instanceNames[i] != NULL) {
			free(e->instanceNames[i]);
		}
	}

	for(i=0; i<e->numSolvers; i++) {
		if(e->md5Solvers[i] != NULL) {
			free(e->md5Solvers[i]);
		}

		if(e->solverNames[i] != NULL) {
			free(e->solverNames[i]);
		}
	}

	if(e->md5Instances != NULL) {
		free(e->md5Instances);
	}

	if(e->instances != NULL) {
		free(e->instances);
	}

	if(e->instanceNames != NULL) {
		free(e->instanceNames);
	}

	if(e->lengthSolver != NULL) {
		free(e->lengthSolver);
	}

	if(e->md5Solvers != NULL) {
		free(e->md5Solvers);
	}

	if(e->solvers != NULL) {
		free(e->solvers);
	}

	if(e->solverNames != NULL) {
		free(e->solverNames);
	}

}

int dbFetchJob(job* j, status* s) {
	MYSQL *conn = NULL;
	MYSQL_RES *res = NULL;
	MYSQL_ROW row;

	char *queryExpJob = NULL;
	char *queryJob = NULL;
	char *queryJobParams = NULL;

	char *params = NULL;

	int lastId = 0;

	int i;
	unsigned long *lengths = NULL;

	j->solverName = NULL;
	j->resultFile = NULL;


	conn = mysql_init(NULL);
	if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
		logError("could not establish mysql connection!\n");
		*s = dbError;
		return 1;
	}

	sprintfAlloc(&queryExpJob, QUERY_EXPERIMENT_JOBS, experimentId);

	/* fetch the id's of the experiment jobs */
	if(mysql_query(conn, queryExpJob) != 0) {
		*s = dbError;
		return 1;
	}

	if((res = mysql_store_result(conn)) == NULL) {
		*s = dbError;
		return 1;
	}

	i = mysql_num_rows(res);

	if(i==0) {
		mysql_free_result(res);
		*s = success;
		return 1;
	}
	srand(clock());
	mysql_data_seek(res, rand() % i);

	if((row = mysql_fetch_row(res)) != NULL) {
		lastId = atoi(row[0]);
	}

	sprintfAlloc(&queryJob, QUERY_JOB, lastId);
	sprintfAlloc(&queryJobParams, QUERY_JOB_PARAMS, lastId);

	/* fetch job information */
	if(mysql_query(conn, queryJob) != 0) {
		mysql_free_result(res);
		*s = dbError;
		return 1;
	}

	mysql_free_result(res);
	if((res = mysql_store_result(conn)) == NULL) {
		*s = dbError;
		return 1;
	}


	if((row = mysql_fetch_row(res)) != NULL) {
		lengths = mysql_fetch_lengths(res);

		j->id = atoi(row[0]);

		j->solverName = (char *)calloc(lengths[1]+1,sizeof(char));
		strncpy(j->solverName, row[1], lengths[1]);

		j->instanceName = (char *)calloc(lengths[2]+1,sizeof(char));
		strncpy(j->instanceName, row[2], lengths[2]);

		j->resultFileName = (char *)calloc(lengths[3]+1,sizeof(char));
		strncpy(j->resultFileName, row[3], lengths[3]);
	}

	/* fetch params information */
	if(mysql_query(conn, queryJobParams) != 0) {
		mysql_free_result(res);
		*s = dbError;
		return 1;
	}

	mysql_free_result(res);
	if((res = mysql_store_result(conn)) == NULL) {
		*s = dbError;
		logError("No params found for job %i.\n", lastId);
		return 1;
	}


	params = (char *)calloc(256,sizeof(char));

	while((row = mysql_fetch_row(res)) != NULL) {
		if (row[2]==NULL){
			//fprintf(stderr,"No parameteres");
			fflush(stderr);;}
		else{

			if(strcmp(row[2],"")!=0) {
				params = strcat(params, row[1]);
				params = strcat(params, row[0]);
				params = strcat(params, " ");

				if(row[2] != NULL) {
					if(row[4] == NULL) {
						params = strcat(params, row[3]);
					} else {
						params = strcat(params, row[4]);
					}
				}
				params = strcat(params, " ");
			}
		}
	}

	strcpy(j->params, params);

	free(params);
	mysql_free_result(res);
	mysql_close(conn);
	*s = success;
	return 0;
}

void freeJob(job *j) {
	if(j->resultFileName != NULL) {
		free(j->resultFileName);
	}

	if(j->solverName != NULL) {
		free(j->solverName);
	}

	if(j->instanceName != NULL) {
		free(j->instanceName);
	}
}


status dbUpdate(const job* j) {
	MYSQL *conn = NULL;
	char *queryJob = NULL;


	sprintfAlloc(&queryJob, UPDATE_JOB,
			j->resultFileName,
			j->status,
			j->seed,
			j->time,
			j->statusCode,
			j->resultFile,
			j->startTime,
			j->id);


	conn = mysql_init(NULL);
	if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
		logError("could not establish mysql connection!\n");
		return dbError;
	}

	if(mysql_query(conn, queryJob) != 0) {
		logError("db update query error, message: %s\n", mysql_error(conn));
		return dbError;
	}

	mysql_commit(conn);

	mysql_close(conn);
	return success;
}

//Try to fetch the solver named solverName from the database
status dbFetchSolver(const char* solverName, solver* s) {
	MYSQL *conn = NULL;
	MYSQL_RES *res = NULL;
	MYSQL_ROW row;

	char *querySolver = NULL;
	char *queryJob = NULL;
	unsigned long *lengths = NULL;

	sprintfAlloc(&queryJob, QUERY_SOLVER, solverName);

	conn = mysql_init(NULL);
	if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
		return dbError;
	}


	/* fetch job information */
	if(mysql_query(conn, querySolver) != 0) {
		return dbError;
	}

	if((res = mysql_store_result(conn)) == NULL) {
		return dbError;
	}

	if((row = mysql_fetch_row(res)) != NULL) {
		lengths = mysql_fetch_lengths(res);

		s->length = lengths[0];
		s->md5 = row[1];

		s->solver = (char *)malloc(lengths[0]*sizeof(char));
		memcpy(s->solver, row[0], lengths[0]);
	}

	mysql_close(conn);
	return success;
}

void freeSolver(solver* s) {
	if(s->solver != NULL) {
		free(s->solver);
	}
}

//Try to fetch the instance named instanceName from the database
status dbFetchInstance(const char* instanceName, instance* i) {
	MYSQL *conn = NULL;
	MYSQL_RES *res;
	MYSQL_ROW row;

	char *queryInstance = NULL;
	unsigned long *lengths;

	sprintfAlloc(&queryInstance, QUERY_INSTANCE, instanceName);

	conn = mysql_init(NULL);
	if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
		logError("could not establish mysql connection!\n");
		return dbError;
	}


	/* fetch job information */
	if(mysql_query(conn, queryInstance) != 0) {
		logError("Query error. Message: %s\n", mysql_error(conn));
		return dbError;
	}

	if((res = mysql_store_result(conn)) == NULL) {
		logError("NULL result.\n");
		return dbError;
	}

	if((row = mysql_fetch_row(res)) != NULL) {
		lengths = mysql_fetch_lengths(res);

		i->md5 = row[1];

		i->instance = (char *)calloc(lengths[0]+1,sizeof(char));
		memcpy(i->instance, row[0], lengths[0]);
	}

	mysql_close(conn);
	return success;
}

void freeInstance(instance *i) {
	if(i->instance != NULL) {
		free(i->instance);
	}
}
