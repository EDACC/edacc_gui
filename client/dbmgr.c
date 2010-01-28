#include "./global.h"
#include "./dbmgr.h"

int fetchExdata() {
    MYSQL *conn;
    MYSQL_RES *res;
    MYSQL_ROW row;

    conn = mysql_init(NULL);

    if(!mysql_real_connect(conn, host, username, password, database, 0, NULL, 0)) {
        //fprintf(stderr, "%s\n", mysql_error(conn));
        return 0;
    }

    if(mysql_query(conn, "select * from test") != 0) {
        //puts("query error occured");
        return 0;
    }

    if((res = mysql_store_result(conn)) == NULL) {
        //puts("no result");
        return 0;
    }

    /* the data needs to be fetched here */
    while((row = mysql_fetch_row(res))) {
        printf("%s\n", row[1]);
    }

    mysql_close(conn);
    return 0;
}

int saveExdata() {

/*
select e.name, sc.idSolverConfig, s.name, scp.value, p.name, p.value 
from Experiment as e 
join SolverConfig as sc on sc.Experiment_idExperiment = e.idExperiment 
join Solver as s on sc.Solver_idSolver = s.idSolver 
join SolverConfig_has_Parameters as scp on scp.SolverConfig_idSolverConfig = sc.idSolverConfig 
join Parameters as p on p.idParameter = scp.Parameters_idParameter
*/

    return 0;
}
