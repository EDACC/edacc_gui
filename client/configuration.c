#include "configuration.h"
#include "global.h"

status read_config() {
    char *lineptr = NULL;
    size_t len = 0;
    int read;
    FILE *conf;

    char *key;
    char *value;
    key = (char *)malloc(64*sizeof(char));
    value = (char *)malloc(64*sizeof(char));


    if((conf = fopen("./config", "r")) == NULL) {
        printf("could not read config file\n");
        free(key);
        free(value);
        return sysError;
    }

    while((read = getline(&lineptr, &len, conf)) != -1) {
        sscanf(lineptr, "%[a-z] = %[^=\n]\n", key, value);

        if(strcmp(key, "host") == 0) {
            strcpy(host, value);
        } else if(strcmp(key, "username") == 0) {
            strcpy(username, value);
        } else if(strcmp(key, "password") == 0) {
            strcpy(password, value);
        } else if(strcmp(key, "database") == 0) {
            strcpy(database, value);
        } else if(strcmp(key, "experiment") == 0) {
            experimentId = atoi(value);
        } else if(strcmp(key, "gridqueue") == 0) {
            gridQueueId = atoi(value);
        } else {
            printf("unknownn option %s\n", key);
            return sysError;
        }
        
        free(lineptr);
        lineptr = NULL;
    }

    fclose(conf);

    free(key);
    free(value);
    return success;
}
