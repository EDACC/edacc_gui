#include "configuration.h"
#include "log.h"
#include "global.h"

status read_config() {
    char *lineptr = NULL;
    size_t len = 0;
    int read;
    FILE *conf;
    char *key=NULL;
    char *value=NULL;
    char *end=NULL;
    int valueLen, keyLen;

    if((conf = fopen("./config", "r")) == NULL) {
        logError("could not read config file\n");
        return sysError;
    }

    while((read = getline(&lineptr, &len, conf)) != -1) {
        //Terminate both the key and value substrings with '\0' within lineptr
        //and set the key resp. value pointer to the beginning of the corresponding sunstring
        key=lineptr;
        for(value=lineptr; *value!=' '; ++value) {
            if(*value=='\0') {
                logError("config file malformed\n");
                fclose(conf);
                free(lineptr);
                return sysError;
            }
        }
        *value='\0';
        for(++value; *value=='=' || *value==' '; ++value) {
            if(*value=='\0') {
                logError("config file malformed\n");
                fclose(conf);
                free(lineptr);
                return sysError;
            }
        }
        for(end=value; *end!='\0' && *end!='\n'; ++end);
        *end='\0';
        valueLen=strlen(value);
        keyLen=strlen(key);
        if(valueLen==0 || keyLen==0) {
            free(lineptr);
            lineptr=NULL;
            continue;
        }

        if(strcmp(key, "host") == 0) {
            host=malloc(valueLen+1);
            if(host==NULL) {
                logError("Out of memory\n");
                fclose(conf);
                free(lineptr);
                return sysError;
            }
            strcpy(host, value);
        } else if(strcmp(key, "username") == 0) {
            username=malloc(valueLen+1);
            if(username==NULL) {
                logError("Out of memory\n");
                fclose(conf);
                free(lineptr);
                return sysError;
            }
            strcpy(username, value);
        } else if(strcmp(key, "password") == 0) {
            password=malloc(valueLen+1);
            if(password==NULL) {
                logError("Out of memory\n");
                fclose(conf);
                free(lineptr);
                return sysError;
            }
            strcpy(password, value);
        } else if(strcmp(key, "database") == 0) {
            database=malloc(valueLen+1);
            if(database==NULL) {
                logError("Out of memory\n");
                fclose(conf);
                free(lineptr);
                return sysError;
            }
            strcpy(database, value);
        } else if(strcmp(key, "experiment") == 0) {
            experimentId = atoi(value);
        } else if(strcmp(key, "gridqueue") == 0) {
            gridQueueId = atoi(value);
        } else {
            printf("unknownn option %s\n", key);
            fclose(conf);
            free(lineptr);
            return sysError;
        }
        
        free(lineptr);
        lineptr = NULL;
    }

    fclose(conf);
    return success;
}

