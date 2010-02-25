#include <string.h>
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
"       numNodes " \
"   FROM gridSettings "

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
"       er.idJob " \
"   FROM ExperimentResults AS er " \
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
"UPDATE ExperimentResult SET " \
"       resultFileName = %s, " \
"       status = %i, " \
"       seed = %i, " \
"       time = %i, " \
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
    MYSQL *conn;
    MYSQL_RES *res;
    MYSQL_ROW row;

    char *queryExperimentInfo;
    char *queryGridInfo;
    char *querySolver;
    char *queryInstance;

    int numRows;
    int lastId;

    int i;
    unsigned long lengths;

    sprintf(queryExperimentInfo, QUERY_EXPERIMENT_INFO, experiment);
    sprintf(queryGridInfo, QUERY_GRID_SETTINGS, experiment);
    sprintf(querySolver, QUERY_SOLVERS, experiment);
    sprintf(queryInstance, QUERY_INSTANCES, experiment);

    if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
        return dberror;
    }


    /* fetch experiment information */
    if(mysql_query(conn, queryExperimentInfo) != 0) {
        return dberror;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dberror;
    }

    if((row = mysql_fetch_row(res)) != NULL) {
        e->timeOut = row[0];
    }


    /* fetch grid information */
    if(mysql_query(conn, queryGridInfo) != 0) {
        return dberror;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dberror;
    }

    if((row = mysql_fetch_row(res)) != NULL) {
        e->numNodes = row[0];
    }


    /* fetch instances */
    if(mysql_query(conn, queryInstance) != 0) {
        return dberror;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dberror;
    }

    e->numInstances = mysql_num_rows(res);

    i=0;
    while((row = mysql_fetch_row(res)) != NULL) {
        lengths = mysql_fetch_lengths(res);

        e->md5Instances[i] = row[3];

        /* Size + 1, to fit the lest nullbyte */
        e->instances[i] = (char *)calloc(lengths[2]+1,sizeof(char));
        memcpy(e->instances[i], row[2], lenhths[2]);

        e->instanceNames = (char *)malloc(lengths[1]*sizeof(char));
        strncpy(e->instanceNames[i], row[1], lengths[1]);

        i++;
    }


    /* fetch solver binaries */
    if(mysql_query(conn, querySolver) != 0) {
        return dberror;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dberror;
    }

    e->numSolvers = mysql_num_rows(res);

    i=0;
    while((row = mysql_fetch_row(res)) != NULL) {
        lengths = mysql_fetch_lengths(res);

        e->lengthSolver[i] = lengths[3];

        e->md5Solvers[i] = row[4];

        e->solvers[i] = (char *)malloc(lengths[3]*sizeof(char));
        memcpy(e->solvers[i], row[3], lengths[3]);

        e->solverNames[i] = (char *)malloc(lengths[2]*sizeof(char));
        strncpy(e->instanceNames[i], row[2], lengths[2]);

        i++;
    }

    mysql_close(conn);
    return success;
}

/* TODO: Implement the randomized job pick */
int dbFetchJob(job* j, status* s) {
    MYSQL *conn;
    MYSQL_RES *res;
    MYSQL_ROW row;

    char *queryJob;
    char *queryJobParams;

    char *params;

    int numRows;
    int lastId;

    int i;
    unsigned long lengths;

    sprintf(queryJob, QUERY_JOB, lastId);
    sprintf(queryJobParams, QUERY_JOB_PARAMS, lastId);

    if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
        return dberror;
    }


    /* fetch job information */
    if(mysql_query(conn, queryJob) != 0) {
        return dberror;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dberror;
    }

    if((row = mysql_fetch_row(res)) != NULL) {
        lengths = mysql_fetch_lengths(res);

        j->id = row[0];

        j->solverName = (char *)malloc(lengths[1]*sizeof(char));
        strncpy(j->solverName, row[1], lengths[1]);

        j->instanceName = (char *)malloc(lengths[2]*sizeof(char));
        strncpy(j->instanceName, row[2], lengths[2]);
    }

    /* fetch params information */
    if(mysql_query(conn, queryJobParams) != 0) {
        return dberror;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dberror;
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

    strcpy(j-params, params);

    mysql_close(conn);
    return success;
}

status dbUpdate(const job* j) {
    MYSQL *conn;
    MYSQL_RES *res;
    MYSQL_ROW row;

    char *updateJob;

    sprintf(queryJob, UPDATE_JOB, j->resultFileName, j->status, j->seed, j->time, j->statusCode);

    if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
        return dberror;
    }

    if(mysql_query(conn, queryJob) != 0) {
        return dberror;
    }

    mysql_commit(conn);

    mysql_close(conn);
    return success;
}

//Try to fetch the solver named solverName from the database
status dbFetchSolver(const char* solverName, solver* s) {
    MYSQL *conn;
    MYSQL_RES *res;
    MYSQL_ROW row;

    char *querySolver;
    unsigned long lengths;

    sprintf(queryJob, QUERY_SOLVER, solverName);

    if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
        return dberror;
    }


    /* fetch job information */
    if(mysql_query(conn, querySolver) != 0) {
        return dberror;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dberror;
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

//Try to fetch the instance named instanceName from the database
status dbFetchInstance(const char* instanceName, instance* i) {
    MYSQL *conn;
    MYSQL_RES *res;
    MYSQL_ROW row;

    char *queryInstance;
    unsigned long lengths;

    sprintf(queryJob, QUERY_INSTANCE, instanceName);

    if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
        return dberror;
    }


    /* fetch job information */
    if(mysql_query(conn, queryInstance) != 0) {
        return dberror;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dberror;
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
