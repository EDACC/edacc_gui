#include "configuration.h"

int read_config() {
    char *lineptr = NULL;
    size_t len = 0;
    int read;
    FILE *conf;

    char *key;
    char *value;
    key = (char *)malloc(64);
    value = (char *)malloc(64);

    if((conf = fopen("./config", "r")) == NULL) {
        printf("could not read config file\n");
        return 1;
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
            experiment = atoi(value);
        } else {
            printf("unknown option %s\n", key);
        }
    }

    fclose(conf);
    return 1;
}
