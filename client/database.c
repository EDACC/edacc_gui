#include "database.h"
#include "global.h"
#include "log.h"

/* Old queries */

#define QUERY_RESULT "" \
"SELECT " \
"       idJob, " \
"       run, " \
"       status, " \
"       seed, " \
"       resultFileName, " \
"       statusCode, " \
"       SolverConfig_idSolverConfig, " \
"       Instances_idInstance " \
"   FROM ExperimentResults " \
"   WHERE Experiment_idExperiment = %i "

#define QUERY_CONFIGURED_SOLVER "" \
"SELECT " \
"       count(Solver_IdSolver) " \
"   FROM SolverConfig " \
"   WHERE Experiment_idExperiment = %i " \
"   GROUP BY Solver_IdSolver "

#define QUERY_CONFIG "" \
"SELECT " \
"       sc.Solver_idSolver, " \
"       scp.value, " \
"       p.name, " \
"       p.prefix, " \
"       p.value, " \
"       p.order " \
"   FROM SolverConfig AS sc " \
"   LEFT JOIN SolverConfig_has_Parameters AS scp " \
"       ON sc.idSolverConfig = scp.SolverConfig_idSolverConfig " \
"   LEFT JOIN Parameters AS p " \
"       ON scp.Parameters_idParameter = p.idParameter " \
"   WHERE sc.Experiment_idExperiment = %i " \
"   ORDER BY sc.Solver_idSolver "

#define QUERY_SOLVER "" \
"SELECT " \
"       s.idSolver, " \
"       s.name, " \
"       s.binary " \
"   FROM SolverConfig AS sc " \
"   LEFT JOIN Solver AS s " \
"       ON sc.Solver_idSolver = s.idSolver " \
"   WHERE sc.Experiment_idExperiment = %i " \
"   GROUP BY s.idSolver "

#define QUERY_INSTANCE_OLD "" \
"SELECT " \
"       i.idInstance, " \
"       i.name, " \
"       i.instance " \
"   FROM Experiment_has_Instances AS er " \
"   LEFT JOIN Instances AS i " \
"       ON er.Instances_idInstance = i.idInstance " \
"   WHERE er.Experiment_idExperiment = %i "

/* END Old queries */






#define QUERY_EXPERIMENT_INFO "" \
"SELECT " \
"       timeOut " \
"   FROM Experiment " \
"   WHERE idExperiment = %i "

#define QUERY_GRID_SETTINGS "" \
"SELECT " \
"       numNodes " \
"   FROM gridSettings "


#define QUERY_INSTANCE "" \
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

solver **solvers = NULL;
config **configs = NULL;
result **results = NULL;
instance **instances = NULL;

status dbFetchExperimentData(experiment *e) {
    MYSQL *conn;
    MYSQL_RES *res;
    MYSQL_ROW row;

    char *queryExperimentInfo;
    char *queryGridInfo;
    char *queryResult;
    char *queryConfigSolver;
    char *queryConfig;
    char *querySolver;

    int numRows;
    int configuredSolver;
    int lastId;

    sprintf(queryExperimentInfo, QUERY_EXPERIMENT_INFO, experiment);
    sprintf(queryResult, QUERY_RESULT, experiment);
    sprintf(queryConfigSolver, QUERY_CONFIGURED_SOLVER, experiment);
    sprintf(queryConfig, QUERY_CONFIG, experiment);
    sprintf(querySolver, QUERY_SOLVER, experiment);
    sprintf(queryInstance, QUERY_INSTANCE, experiment);

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

    /* fetch instances */
    if(mysql_query(conn, queryInstance) != 0) {
        return dberror;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dberror;
    }

    e->numInstances = mysql_num_rows(res);

    if((instances = (instance *)e->numInstances*sizeof(instance))) == NULL) {
        return dberror;
    }

    while((row = mysql_fetch_row(res)) != NULL) {
        e->
    }



    /* fetch solver binaries */
    if(mysql_query(conn, querySolver) != 0) {
        return dberror;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dberror;
    }

    solvers = (solver *)malloc(mysql_num_rows(res)*sizeof(solver));

    while((row = mysql_fetch_row(res)) != NULL) {
        solvers[i] = {row[0], row[1], row[2]};
    }



    /* fetch the number of involved binaries in experiment */
    if(mysql_query(conn, queryConfiguredSolver) != 0) {
        return dberror;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dberror;
    }

    if ((row = mysql_fetch_row(res)) != NULL) {
        configuredSolver = row[0];
    } else {
        return dberror;
    }

    /* fetch configs */
    if(mysql_query(conn, queryConfig) != 0) {
        return dberror;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        return dberror;
    }

    configs = (config *)malloc(configuredSolver*sizeof(config));

    while((row = mysql_fetch_row(res)) != NULL) {
        results[i] = {row[0], row[1]};
    }



    /* fetch results */




    mysql_close(conn);
    return success;
}

int dbFetchJob(job* j, status* s) {
	static int i=0, num=7;

	if(i<num) {
		++i;
		*s=success;
		return 0;
	}

	*s=success;
	return 1;
}

status dbUpdate(const job* j) {
	return success;
}

//Try to fetch the solver named solverName from the database
status dbFetchSolver(const char* solverName, solver* s) {
	return success;
}

//Try to fetch the instance named instanceName from the database
status dbFetchInstance(const char* instanceName, instance* i) {
	return success;
}

solver *getSolver(int id, solvers **s) {
    int i;

    for(i=0, i<length(s), i++) {
        if (s[i].id = id) {
            return s[i];
        }
    }

    return NULL;
}

