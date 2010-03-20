#include <string.h>
#include <time.h>
#include <stdio.h>
#include "database.h"
#include "global.h"
#include "log.h"



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
"   WHERE Experiment_idExperiment = %i "


#define QUERY_JOB "" \
"SELECT " \
"       e.idJob, " \
"       s.name, " \
"       i.name " \
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
"       p.value, " \
"       scp.value " \
"   FROM ExperimentResults AS er " \
"   LEFT JOIN SolverConfig AS sc " \
"       ON er.SolverConfig_idSolverConfig = sc.idSolverConfig " \
"   LEFT JOIN SolverConfig_has_Parameters AS scp " \
"       ON sc.idSolverConfig = scp.SolverConfig_idSolverConfig " \
"   LEFT JOIN Parameters AS p " \
"       ON scp.Parameters_idParameter = p.idParameter " \
"   WHERE e.idJob = %i "


#define UPDATE_JOB "" \
"UPDATE ExperimentResults SET " \
"       resultFileName = %s, " \
"       status = %i, " \
"       seed = %i, " \
"       time = %d, " \
"       statusCode " \
"   WHERE idJob = %i "


#define QUERY_SOLVER "" \
"SELECT " \
"       binary, " \
"       md5 " \
"   FROM Solver " \
"   WHERE name = %s "

#define QUERY_INSTANCE "" \
"SELECT " \
"       instance, " \
"       md5 " \
"   FROM Instances " \
"   WHERE name = %s "

status dbFetchExperimentData(experiment *e) {
    MYSQL *conn = NULL;
    MYSQL_RES *res;
    MYSQL_ROW row;

    char *queryExperimentInfo = NULL;
    char *queryGridInfo = NULL;
    char *querySolver = NULL;
    char *queryInstance = NULL;

    //int numRows;
    //int lastId;

    int i;
    unsigned long *lengths;

    e->md5Instances = NULL;
    e->instances = NULL;
    e->instanceNames = NULL;

    e->lengthSolver = NULL;
    e->md5Solvers = NULL;
    e->solvers = NULL;
    e->solverNames = NULL;

    sprintf(queryExperimentInfo, QUERY_EXPERIMENT_INFO, experimentId);
    sprintf(queryGridInfo, QUERY_GRID_SETTINGS, gridQueueId);
    sprintf(querySolver, QUERY_SOLVERS, experimentId);
    sprintf(queryInstance, QUERY_INSTANCES, experimentId);

    if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
        return dbError;
    }


    /* fetch experiment information */
    if(mysql_query(conn, queryExperimentInfo) != 0) {
        return dbError;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dbError;
    }

    if((row = mysql_fetch_row(res)) != NULL) {
        e->timeOut = atoi(row[0]);
    }


    /* fetch grid information */
    if(mysql_query(conn, queryGridInfo) != 0) {
        mysql_free_result(res);
        return dbError;
    }

    mysql_free_result(res);
    if((res = mysql_store_result(conn)) == NULL) {
        return dbError;
    }

    if((row = mysql_fetch_row(res)) != NULL) {
        e->numCPUs = atoi(row[0]);
    }


    /* fetch instances */
    if(mysql_query(conn, queryInstance) != 0) {
        mysql_free_result(res);
        return dbError;
    }

    mysql_free_result(res);
    if((res = mysql_store_result(conn)) == NULL) {
        return dbError;
    }

    e->numInstances = mysql_num_rows(res);

    if((e->md5Instances = malloc(e->numInstances*sizeof(char *))) == NULL) {
        mysql_free_result(res);
        return sysError;
    }

    if((e->instances = malloc(e->numInstances*sizeof(char *))) == NULL) {
        mysql_free_result(res);
        return sysError;
    }

    if((e->instanceNames = malloc(e->numInstances*sizeof(char *))) == NULL) {
        mysql_free_result(res);
        return sysError;
    }

    i=0;
    while((row = mysql_fetch_row(res)) != NULL) {
        lengths = mysql_fetch_lengths(res);

        e->md5Instances[i] = (char *)malloc(60*sizeof(char));
        e->md5Instances[i] = row[3];

        /* Size + 1, to fit the lest nullbyte */
        e->instances[i] = (char *)calloc(lengths[2]+1,sizeof(char));
        memcpy(e->instances[i], row[2], lengths[2]);

        e->instanceNames[i] = (char *)malloc(lengths[1]*sizeof(char*));
        strncpy(e->instanceNames[i], row[1], lengths[1]);

        i++;
    }



    /* fetch solver binaries */
    if(mysql_query(conn, querySolver) != 0) {
        mysql_free_result(res);
        return dbError;
    }

    mysql_free_result(res);
    if((res = mysql_store_result(conn)) == NULL) {
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

        e->lengthSolver[i] = lengths[3];

        e->md5Solvers[i] = (char *)malloc(60*sizeof(char));
        e->md5Solvers[i] = row[4];

        e->solvers[i] = (char *)malloc(lengths[3]*sizeof(char));
        memcpy(e->solvers[i], row[3], lengths[3]);

        e->solverNames[i] = (char *)malloc(lengths[2]*sizeof(char));
        strncpy(e->instanceNames[i], row[2], lengths[2]);

        i++;
    }

    mysql_free_result(res);
    mysql_close(conn);
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
    MYSQL_RES *res;
    MYSQL_ROW row;

    char *queryExpJob = NULL;
    char *queryJob = NULL;
    char *queryJobParams = NULL;

    char *params = NULL;

    //int numRows;
    int lastId = 0;

    int i;
    unsigned long *lengths;

    j->solverName = NULL;
    j->resultFile = NULL;


    if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
        return dbError;
    }

    sprintf(queryExpJob, QUERY_EXPERIMENT_JOBS, experimentId);

    /* fetch the id's of the experiment jobs */
    if(mysql_query(conn, queryExpJob) != 0) {
        return dbError;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dbError;
    }

    i = mysql_num_rows(res);
    srand(clock());
    mysql_data_seek(res, rand() % i);

    if((row = mysql_fetch_row(res)) != NULL) {
        lastId = atoi(row[0]);
    }

    sprintf(queryJob, QUERY_JOB, lastId);
    sprintf(queryJobParams, QUERY_JOB_PARAMS, lastId);

    /* fetch job information */
    if(mysql_query(conn, queryJob) != 0) {
        mysql_free_result(res);
        return dbError;
    }

    mysql_free_result(res);
    if((res = mysql_store_result(conn)) == NULL) {
        return dbError;
    }


    if((row = mysql_fetch_row(res)) != NULL) {
        lengths = mysql_fetch_lengths(res);

        j->id = atoi(row[0]);

        j->solverName = (char *)malloc(lengths[1]*sizeof(char));
        strncpy(j->solverName, row[1], lengths[1]);

        j->instanceName = (char *)malloc(lengths[2]*sizeof(char));
        strncpy(j->instanceName, row[2], lengths[2]);
    }

    /* fetch params information */
    if(mysql_query(conn, queryJobParams) != 0) {
        mysql_free_result(res);
        return dbError;
    }

    mysql_free_result(res);
    if((res = mysql_store_result(conn)) == NULL) {
        return dbError;
    }

    
    params = (char *)calloc(1,sizeof(char));

    while((row = mysql_fetch_row(res)) != NULL) {
        params = strcat(params, row[1]);

        if(row[3] == NULL) {
            params = strcat(params, row[2]);
        } else {
            params = strcat(params, row[3]);
        }
    }

    strcpy(j->params, params);

    mysql_free_result(res);
    mysql_close(conn);
    return success;
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
    //MYSQL_RES *res;
    //MYSQL_ROW row;

    //char *updateJob;
    char *queryJob = NULL;

    sprintf(queryJob, UPDATE_JOB, j->resultFileName, j->status, j->seed, j->time, j->statusCode);

    if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
        return dbError;
    }

    if(mysql_query(conn, queryJob) != 0) {
        return dbError;
    }

    mysql_commit(conn);

    mysql_close(conn);
    return success;
}

//Try to fetch the solver named solverName from the database
status dbFetchSolver(const char* solverName, solver* s) {
    MYSQL *conn = NULL;
    MYSQL_RES *res;
    MYSQL_ROW row;

    char *querySolver = NULL;
    char *queryJob = NULL;
    unsigned long *lengths;

    sprintf(queryJob, QUERY_SOLVER, solverName);

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

    sprintf(queryInstance, QUERY_INSTANCE, instanceName);

    if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
        return dbError;
    }


    /* fetch job information */
    if(mysql_query(conn, queryInstance) != 0) {
        return dbError;
    }

    if((res = mysql_store_result(conn)) == NULL) {
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
