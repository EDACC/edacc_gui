#include <string.h>
#include <time.h>
#include <stdio.h>
#include "database.h"
#include "global.h"
#include "log.h"
#include "safeio.h"

status dbFetchExperimentData(experiment* e) {
	MYSQL *conn;
	MYSQL_RES *res;
	MYSQL_ROW row;
	int queryLength;



	char *queryExperimentInfo = NULL;
	char *queryGridInfo = NULL;
	e->id = experimentId;
	e->name = NULL;

	logComment(1, "establishing a MySQL-connection...");
	conn = mysql_init(NULL);
	if (conn==NULL){
		LOGERROR(AT,"could not init mysql library: Out of memory!\n");
		return dbError;
	}
	int tries=0;
	for (tries=0;tries<connectAttempts;tries++){
		if (mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)== NULL) {
			LOGERROR(AT,"could not establish a mysql connection!\n error message: %s",mysql_error(conn));
			sleep(waitForDB);
		}
		else break;
	}
	if (tries==connectAttempts){
		mysql_close(conn);
		return dbError;
	}

	logComment(1, "done\n");

	queryLength = sprintfAlloc(&queryExperimentInfo, QUERY_EXPERIMENT_INFO,
			experimentId);

	/* fetch experiment information */
	if (mysql_query(conn, queryExperimentInfo) != 0) {
		LOGERROR(AT, "DB error message: %s", mysql_error(conn));
		return dbError;
	}

	if ((res = mysql_store_result(conn)) == NULL) {
		LOGERROR(AT, "DB error message: %s", mysql_error(conn));
		return dbError;
	}

	if ((row = mysql_fetch_row(res)) != NULL) {
		e->CPUTimeLimit = atoi(row[0]);
		e->wallClockTimeLimit = atoi(row[1]);
		e->memoryLimit = atoi(row[2]);
		e->stackSizeLimit = atoi(row[3]);
		e->outPutSizeLimit = atoi(row[4]);
		e->name = malloc(256 * sizeof(char));
		strncpy(e->name, row[5], 256);
	} else {
		LOGERROR(AT, "NO experiment data available for experiment with ID: %d",
				experimentId);
		return dbError;
	}

	logComment(2, "Experiment settings \n------------------------------\n");
	logComment(2, "%-30s : %s \n", "name of experiment", e->name);
	logComment(2, "%-30s : %d (sec) \n", "CPU time-out for jobs",
			e->CPUTimeLimit);
	logComment(2, "%-30s : %d (sec) \n", "wall clock time out for jobs",
			e->wallClockTimeLimit);
	logComment(2, "%-30s : %d (MB) \n", "max memory  for jobs", e->memoryLimit);
	logComment(2, "%-30s : %d (MB) \n", "max stack size for jobs",
			e->stackSizeLimit);
	logComment(2, "%-30s : %d (MB) \n", "max size output files for jobs",
			e->outPutSizeLimit);
	mysql_free_result(res);

	/* fetch grid information */

	queryLength
	= sprintfAlloc(&queryGridInfo, QUERY_GRID_SETTINGS, gridQueueId);

	if (mysql_query(conn, queryGridInfo) != 0) {
		mysql_free_result(res);
		LOGERROR(AT, "DB error message: %s", mysql_error(conn));
		return dbError;
	}

	if ((res = mysql_store_result(conn)) == NULL) {
		LOGERROR(AT, "DB error message: %s", mysql_error(conn));
		return dbError;
	}

	if ((row = mysql_fetch_row(res)) != NULL) {
		e->numCPUs = atoi(row[0]);
	}

	logComment(2, "%-30s : %d\n", "numCpus per Node", e->numCPUs);
	logComment(2, "------------------------------\n");

	mysql_free_result(res);
	mysql_close(conn);
	mysql_library_end();

	free(queryExperimentInfo);
	free(queryGridInfo);
	return success;
}

void freeExperimentData(experiment *e) {
	free(e->name);
}

/*int dbGetJob(job* j, status* s) {
	MYSQL *conn = NULL;
	MYSQL_RES *res = NULL;
	MYSQL_ROW row;
	int jobID;
	int freeJobs=1; //indicates whether there are uncomputed jobs
	int gotAJob=0;
	int i;
	char *queryRandomJob = NULL;
	char *queryLockJob = NULL;

	conn = mysql_init(NULL);
	if (!mysql_real_connect(conn, host, username, password, database, port,
			NULL, 0)) {
		LOGERROR(AT, "could not establish mysql connection!\n");
 *s = dbError;
		mysql_close(conn);
		return 1;
	}
	//Autocommit wird hier ausgeschaltet
	if (mysql_autocommit(conn,0)!=0)
		LOGERROR(AT, "db error: Could not switch autocommit OFF");

	while ((freeJobs)&&(!gotAJob)){

		sprintfAlloc(&queryRandomJob, QUERY_RANDOM_JOB,experimentId);
		if (mysql_query(conn, queryRandomJob) != 0) {
			LOGERROR(AT, "db query error, message: %s\n", mysql_error(conn));
 *s = dbError;
			mysql_close(conn);
			free(queryJob);
			return 1;
		}
		free(queryJob);	queryJob=NULL;

		if ((res = mysql_store_result(conn)) == NULL) {
			LOGERROR(AT, "store result error");
 *s = dbError;
			mysql_close(conn);
			return 1;
		}
		i = mysql_num_rows(res);
		if (i == 0) { //keine jobs mehr vorhanden!
			mysql_free_result(res);
 *s = success;
			return 1;
		}

		if ((row = mysql_fetch_row(res)) != NULL) {
			jobID = atoi(row[0]);
		}

		sprintfAlloc(&queryLockJob, QUERY_LOCK_JOB,jobID);

		if (mysql_query(conn, queryJob) != 0) {
			LOGERROR(AT, "could not lock Job %d: %s\n",jobID, mysql_error(conn));
			return dbError;
		}
		if ((long)mysql_affected_rows(&mysql)==1){
			gotAJob=1;
			succes=1;
		}
		mysql_commit(conn);
	}
	mysql_close(conn);
	free(queryJob);
}*/

int dbFetchJob(job* j, status* s) {
	MYSQL *conn = NULL;
	MYSQL_RES *res = NULL;
	MYSQL_ROW row;

	char *queryJob = NULL;
	char *queryJobParams = NULL;
	//char *queryJobParams = NULL;

	int queryLength;

	int jobID;

	int gotAJob=0;

	char *queryRandomJob = NULL;
	char *queryLockJob = NULL;

	char *params = NULL;
	char *temp = NULL;

	int i;
	unsigned long *lengths = NULL;

	j->startTime = NULL;

	j->solverOutputFN = NULL;
	j->launcherOutputFN = NULL;
	j->watcherOutputFN = NULL;
	j->verifierOutputFN = NULL;

	j->solverOutput = NULL;
	j->launcherOutput = NULL;
	j->watcherOutput = NULL;
	j->verifierOutput = NULL;

	j->solverName = NULL;
	j->solverVersion = NULL;
	j->instanceName = NULL;
	j->binaryName = NULL;



	conn = mysql_init(NULL);
	if (conn==NULL){
		LOGERROR(AT,"could not init mysql library: Out of memory!\n");
		return dbError;
	}
	int tries=0;
	for (tries=0;tries<connectAttempts;tries++){
		if (mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)== NULL) {
			LOGERROR(AT,"could not establish a mysql connection!\n error message: %s",mysql_error(conn));
			sleep(waitForDB);
		}
		else break;
	}
	if (tries==connectAttempts){
		mysql_close(conn);
		*s = dbError;
		mysql_close(conn);
		return 1;
	}


	//	conn = mysql_init(NULL);
	//	if (!mysql_real_connect(conn, host, username, password, database, port,
	//			NULL, 0)) {
	//		LOGERROR(AT, "could not establish mysql connection!\n");
	//		*s = dbError;
	//		mysql_close(conn);
	//		return 1;
	//	}

	//Autocommit wird hier ausgeschaltet
	if (mysql_autocommit(conn,0)!=0)
		LOGERROR(AT, "db error: Could not switch autocommit OFF");

	while (!gotAJob){
		sprintfAlloc(&queryRandomJob, QUERY_RANDOM_JOB,experimentId);
		if (mysql_query(conn, queryRandomJob) != 0) {
			LOGERROR(AT, "db query error, message: %s\n", mysql_error(conn));
			*s = dbError;
			mysql_close(conn);
			free(queryJob);
			return 1;
		}

		if ((res = mysql_store_result(conn)) == NULL) {
			LOGERROR(AT, "store result error");
			*s = dbError;
			mysql_close(conn);
			return 1;
		}
		i = mysql_num_rows(res);
		if (i == 0) { //keine jobs mehr vorhanden!
			mysql_free_result(res);
			mysql_commit(conn);
			if (mysql_autocommit(conn,1)!=0)
				LOGERROR(AT, "db error: Could not switch autocommit ON");

			*s = success;
			return 1;
		}

		if ((row = mysql_fetch_row(res)) != NULL) {
			jobID = atoi(row[0]);
		}

		sprintfAlloc(&queryLockJob, QUERY_LOCK_JOB,jobID);

		if (mysql_query(conn, queryLockJob) != 0) {
			LOGERROR(AT, "could not lock Job %d: %s\n",jobID, mysql_error(conn));
			return dbError;
		}
		if ((long)mysql_affected_rows(conn)==1){
			gotAJob=1;
			*s = success;
		}
		mysql_commit(conn);
	}
	//autocommit turn on!
	if (mysql_autocommit(conn,1)!=0)
		LOGERROR(AT, "db error: Could not switch autocommit ON");

	free(queryJob);	queryJob=NULL;

	queryLength = sprintfAlloc(&queryJob, QUERY_JOB, jobID);

	/* fetch job information */
	if (mysql_query(conn, queryJob) != 0) {
		LOGERROR(AT, "db query error, message: %s\n", mysql_error(conn));
		mysql_free_result(res);
		*s = dbError;
		return 1;
	}

	mysql_free_result(res);
	if ((res = mysql_store_result(conn)) == NULL) {
		LOGERROR(AT, "store result error");
		*s = dbError;
		return 1;
	}

	if ((row = mysql_fetch_row(res)) != NULL) {
		lengths = mysql_fetch_lengths(res);

		j->id = atoi(row[0]);

		j->solverName = (char *) calloc(lengths[1] + 1, sizeof(char));
		strncpy(j->solverName, row[1], lengths[1]);

		j->solverVersion = (char *) calloc(lengths[2] + 1, sizeof(char));
		strncpy(j->solverVersion, row[2], lengths[2]);

		//binaryName is <solverName>_v<solverVersion>
		j->binaryName = (char *) calloc(lengths[1] + lengths[2] + 3,
				sizeof(char)); //+3 because of _v
		strncpy(j->binaryName, row[1], lengths[1]);
		strncat(j->binaryName, "_v", 2);
		strncat(j->binaryName, row[2], lengths[2]);

		j->instanceName = (char *) calloc(lengths[3] + 1, sizeof(char));
		strncpy(j->instanceName, row[3], lengths[3]);

		j->seed = atoi(row[4]);

		j->solverOutputFN = (char *) calloc(lengths[5] + 1, sizeof(char));
		strncpy(j->solverOutputFN, row[5], lengths[5]);

		j->launcherOutputFN = (char *) calloc(lengths[6] + 1, sizeof(char));
		strncpy(j->launcherOutputFN, row[6], lengths[6]);

		j->watcherOutputFN = (char *) calloc(lengths[7] + 1, sizeof(char));
		strncpy(j->watcherOutputFN, row[7], lengths[7]);

		j->verifierOutputFN = (char *) calloc(lengths[8] + 1, sizeof(char));
		strncpy(j->verifierOutputFN, row[8], lengths[8]);
	}
	queryLength = sprintfAlloc(&queryJobParams, QUERY_JOB_PARAMS, j->id);
	/* fetch params information */
	if (mysql_query(conn, queryJobParams) != 0) {
		LOGERROR(AT, "query started:  %s\n", queryJobParams);
		LOGERROR(AT, "db query error, message: %s\n", mysql_error(conn));
		mysql_free_result(res);
		*s = dbError;
		return 1;
	}
	mysql_free_result(res);

	if ((res = mysql_store_result(conn)) == NULL) {
		*s = dbError;
		LOGERROR(AT, "No params found for job %i.\n", jobID);
		return 1;
	}

	//automatische Erkennung von seed und instance parameter: Werte werden automatisch eingesetzt.

	params = (char *) calloc(1024, sizeof(char));

	while ((row = mysql_fetch_row(res)) != NULL) {
		lengths = mysql_fetch_lengths(res);

		// every param starts with a prefix
		if (row[1] != NULL)
			params = strcat(params, row[1]);
		//params = strcat(params, row[0]);

//		if (row[0] != NULL)
//			printf("\n %s \n", row[0]);
		if (strcmp(row[0], "seed") == 0) { //seed parameter
			temp = (char *) calloc(32, sizeof(char));
			sprintf(temp, "%d", j->seed);
			params = strcat(params, temp);
			free(temp);
		} else if (strcmp(row[0], "instance") == 0) {//instance parameter
			//params = strcat(params, instancePath); //prepend path first
			params = strcat(params, prependInstancePath(j->instanceName)); //TODO: path noch hinzufügen
		} else
			// check if it's a param with value
			if (atoi(row[2]) == 1) {
				// delimiter between paramname and value
				params = strcat(params, " ");
				if (row[3] != NULL && (strcmp(row[3], "") != 0)) {
					params = strcat(params, row[3]);
				} else {
					LOGERROR(AT,
							"No value found for param %s for solver %s_v%s.\n",
							row[1], j->solverName, j->solverVersion);
					return 1;
				}
			}

		params = strcat(params, " ");

	}
	strcpy(j->params, params);
	free(params);
	mysql_free_result(res);
	mysql_close(conn);
	*s = success;
	free(queryJob);
	return 0;
}

void freeJob(job *j) {//TODO: hier muss noch einiges befreit werden!
	if (j->solverOutputFN != NULL) {
		free(j->solverOutputFN);
	}

	if (j->solverName != NULL) {
		free(j->solverName);
	}

	if (j->instanceName != NULL) {
		free(j->instanceName);
	}
}

status dbUpdate(const job* j) {
	MYSQL *conn = NULL;
	char *queryJob = NULL;
	int queryLength;

	//	conn = mysql_init(NULL);
	//	if (!mysql_real_connect(conn, host, username, password, database, port,
	//			NULL, 0)) {
	//		LOGERROR(AT, "could not establilogCommentsh mysql connection!\n");
	//		return dbError;
	//	}

	conn = mysql_init(NULL);
	if (conn==NULL){
		LOGERROR(AT,"could not init mysql library: Out of memory!\n");
		return dbError;
	}
	int tries=0;
	for (tries=0;tries<connectAttempts;tries++){
		if (mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)== NULL) {
			LOGERROR(AT,"could not establish a mysql connection!\n error message: %s",mysql_error(conn));
			sleep(waitForDB);
		}
		else break;
	}
	if (tries==connectAttempts){
		mysql_close(conn);
		return dbError;
	}

	//TODO: SQL injection possibility with StartTime
	queryLength = sprintfAlloc(&queryJob, UPDATE_JOB, j->status, j->startTime,
			j->resultTime, j->computeQueue, j->id);

	if (mysql_query(conn, queryJob) != 0) {
		LOGERROR(AT, "db update query error, message: %s\n", mysql_error(conn));
		return dbError;
	}
	mysql_commit(conn);
	mysql_close(conn);
	free(queryJob);

	return success;
}

status dbUpdateResults(const job* j) {
	MYSQL *conn = NULL;
	char *queryJob = NULL;
	char *escapedString1 = NULL;
	char *escapedString2 = NULL;
	char *escapedString3 = NULL;
	char *escapedString4 = NULL;
	int queryLength, length;

	conn = mysql_init(NULL);
	if (conn==NULL){
		LOGERROR(AT,"could not init mysql library: Out of memory!\n");
		return dbError;
	}
	int tries=0;
	for (tries=0;tries<connectAttempts;tries++){
		if (mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)== NULL) {
			LOGERROR(AT,"could not establish a mysql connection!\n error message: %s",mysql_error(conn));
			sleep(waitForDB);
		}
		else break;
	}
	if (tries==connectAttempts){
		mysql_close(conn);
		return dbError;
	}

	length = strlen(j->solverOutput);
	escapedString1 = (char*) malloc(length * 2 + 1);
	mysql_real_escape_string(conn, escapedString1, j->solverOutput, length);

	length = strlen(j->launcherOutput);
	escapedString2 = (char*) malloc(length * 2 + 1);
	mysql_real_escape_string(conn, escapedString2, j->launcherOutput, length);

	length = strlen(j->watcherOutput);
	escapedString3 = (char*) malloc(length * 2 + 1);
	mysql_real_escape_string(conn, escapedString3, j->watcherOutput, length);

	length = strlen(j->verifierOutput);
	escapedString4 = (char*) malloc(length * 2 + 1);
	mysql_real_escape_string(conn, escapedString4, j->verifierOutput, length);

	queryLength = sprintfAlloc(&queryJob, UPDATE_JOB_RESULTS, j->status,
			j->startTime, j->resultTime, j->resultCode, escapedString1,
			escapedString2, escapedString3, escapedString4, j->solverExitCode,
			j->watcherExitCode, j->verifierExitCode, j->computeQueue, j->id);

	if (mysql_real_query(conn, queryJob, queryLength + 1) != 0) {
		LOGERROR(AT, "db update query error, message: %s\n", mysql_error(conn));
		LOGERROR(AT, "query launched: %s\n", queryJob);
		return dbError;
	}

	mysql_commit(conn);
	mysql_close(conn);
	free(escapedString1);
	free(escapedString2);
	free(escapedString3);
	free(escapedString4);

	free(queryJob);
	return success;
}

//Try to fetch the solver named solverName from the database
status dbFetchSolver(const char* solverName, const char* solverVersion,
		solver* s) {
	MYSQL *conn = NULL;
	MYSQL_RES *res = NULL;
	MYSQL_ROW row;

	char *querySolver = NULL;
	char *escapedSolverName = NULL;
	char *escapedSolverVersion = NULL;
	int queryLength;
	int length;

	unsigned long *lengths = NULL;

	conn = mysql_init(NULL);
	if (conn==NULL){
		LOGERROR(AT,"could not init mysql library: Out of memory!\n");
		return dbError;
	}
	int tries=0;
	for (tries=0;tries<connectAttempts;tries++){
		if (mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)== NULL) {
			LOGERROR(AT,"could not establish a mysql connection!\n error message: %s",mysql_error(conn));
			sleep(waitForDB);
		}
		else break;
	}
	if (tries==connectAttempts){
		mysql_close(conn);
		return dbError;
	}

	length = strlen(solverName);
	escapedSolverName = (char*) malloc(length * 2 + 1);
	mysql_real_escape_string(conn, escapedSolverName, solverName, length);

	length = strlen(solverVersion);
	escapedSolverVersion = (char*) malloc(length * 2 + 1);
	mysql_real_escape_string(conn, escapedSolverVersion, solverVersion, length);

	queryLength = sprintfAlloc(&querySolver, QUERY_SOLVER, escapedSolverName,
			escapedSolverVersion);

	/* fetch job information */
	if (mysql_query(conn, querySolver) != 0) {
		LOGERROR(AT, "DB error message: %s\n", mysql_error(conn));
		LOGERROR(AT, "Query lunched: %s\n", querySolver);
		return dbError;
	}

	if ((res = mysql_store_result(conn)) == NULL) {
		LOGERROR(AT, "DB error message: %s", mysql_error(conn));
		return dbError;
	}

	if ((row = mysql_fetch_row(res)) != NULL) {
		lengths = mysql_fetch_lengths(res);

		s->length = lengths[0];
		s->md5 = row[1];

		s->solver = (char *) malloc(lengths[0] * sizeof(char));
		memcpy(s->solver, row[0], lengths[0]);
	}

	mysql_close(conn);
	free(escapedSolverName);
	free(escapedSolverVersion);
	free(querySolver);
	return success;
}

void freeSolver(solver* s) {
	if (s->solver != NULL) {
		free(s->solver);
	}
}

//Try to fetch the instance named instanceName from the database
status dbFetchInstance(const char* instanceName, instance* i) {
	MYSQL *conn = NULL;
	MYSQL_RES *res;
	MYSQL_ROW row;

	int queryLength;
	char *escapedInstanceName;
	int length;

	char *queryInstance = NULL;
	unsigned long *lengths;

	conn = mysql_init(NULL);
	if (conn==NULL){
		LOGERROR(AT,"could not init mysql library: Out of memory!\n");
		return dbError;
	}
	int tries=0;
	for (tries=0;tries<connectAttempts;tries++){
		if (mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)== NULL) {
			LOGERROR(AT,"could not establish a mysql connection!\n error message: %s",mysql_error(conn));
			sleep(waitForDB);
		}
		else break;
	}
	if (tries==connectAttempts){
		mysql_close(conn);
		return dbError;
	}

	length = strlen(instanceName);
	escapedInstanceName = (char*) malloc(length * 2 + 1);
	mysql_real_escape_string(conn, escapedInstanceName, instanceName, length);

	queryLength = sprintfAlloc(&queryInstance, QUERY_INSTANCE,
			escapedInstanceName);

	/* fetch instance  */
	if (mysql_query(conn, queryInstance) != 0) {//TODO: should it be real_query
		LOGERROR(AT, "Query error. Message: %s\n", mysql_error(conn));
		return dbError;
	}

	if ((res = mysql_store_result(conn)) == NULL) {
		LOGERROR(AT, "NULL result.\n");
		return dbError;
	}

	if ((row = mysql_fetch_row(res)) != NULL) {
		lengths = mysql_fetch_lengths(res);
		i->md5 = row[1];
		i->instance = (char *) calloc(lengths[0] + 1, sizeof(char));
		memcpy(i->instance, row[0], lengths[0]);
	}

	mysql_close(conn);
	free(escapedInstanceName);
	free(queryInstance);
	return success;
}

void freeInstance(instance *i) {
	if (i->instance != NULL) {
		free(i->instance);
	}
}

status setMySQLTime(job *j) {
	MYSQL *conn = NULL;
	MYSQL_RES *res;
	MYSQL_ROW row;
	char *queryTime = NULL;
	unsigned long *lengths;

	sprintfAlloc(&queryTime, QUERY_TIME);

	conn = mysql_init(NULL);
	if (conn==NULL){
		LOGERROR(AT,"could not init mysql library: Out of memory!\n");
		return dbError;
	}
	int tries=0;
	for (tries=0;tries<connectAttempts;tries++){
		if (mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)== NULL) {
			LOGERROR(AT,"could not establish a mysql connection!\n error message: %s",mysql_error(conn));
			sleep(waitForDB);
		}
		else break;
	}
	if (tries==connectAttempts){
		mysql_close(conn);
		return dbError;
	}

	if (mysql_query(conn, queryTime) != 0) {
		LOGERROR(AT, "DB error message: %s\n", mysql_error(conn));
		LOGERROR(AT, "Query lunched: %s\n", queryTime);
		return dbError;
	}

	if ((res = mysql_store_result(conn)) == NULL) {
		LOGERROR(AT, "DB error message: %s", mysql_error(conn));
		return dbError;
	}

	if ((row = mysql_fetch_row(res)) != NULL) {
		lengths = mysql_fetch_lengths(res);
		j->startTime = (char *) calloc(lengths[0] + 1, sizeof(char));
		memcpy(j->startTime, row[0], lengths[0]);
	}

	mysql_close(conn);
	return success;
}

