/**
 * ===========================================================================
 * Program - cfg.c
 * Author - shashank
 * This is the program that loads Agent Property Config and
 * populates necessary data structures
 * ===========================================================================
 */

#include "ptype.h"

PRIVATE GLOBAL struct config sgCfg;

PRIVATE int cxParse(const char *config_file, struct config *pCfg);

PRIVATE void createDirIfNotExists(char *buffer);

/**
 * ===========================================================================
 * Function: loadCfg()
 * Description: This function locates the configuration file in HOST environment.
 * Since this is designed for Finacle, we are using Finacle ENV variables.
 * Normally Finacle has ROOT folder available in TBA_PROD_ROOT or B2K_PROD_ROOT.
 * For non-Finacle hosts, one can set CXPS_PROD_ROOT.
 * This method will check for configuration file's existence in order.
 *
 * The files are -
 * .../clari5/agent.properties  -> specifies property file
 * .../clari5/saf -> specifies folder for SAF messages
 * .../clari5/agent.log -> will log error message from this agent.
 *
 * This function is not thread-safe
 * @return
 * ===========================================================================
 */

PUBLIC struct config *loadCfg() {

    static char sCfgFile[8192] = "";
    static volatile int initialized = 0;
    static long last_time = 0;
    static char root[4096] = "/tmp";
    char buffer[8192] = "";

    struct stat st;

    if (!initialized) {
        char* location;
        if (((location = getenv("CXPS_PROD_ROOT")) != NULL) && (strlen(location) > 0)) {
            strcpy(root, strndup(location, 4095));
        } else if (((location = getenv("TBA_PROD_ROOT")) != NULL) && (strlen(location) > 0)) {
            strcpy(root, strndup(location, 4095));
        } else if (((location = getenv("B2K_PROD_ROOT")) != NULL) && (strlen(location) > 0)) {
            strcpy(root, strndup(location, 4095));
        } else {
            getProdRoot(root);
        }

        /* Multiple C-Client processes writing to their own <log-file>-<pid>.log */
        sprintf(sCfgFile, "%s%s", root, "/clari5/agent.properties");
        sprintf(sgCfg.logFile, "%s%s%d%s", root, "/clari5/logs/agent-", ((int) getpid()), ".log");
        sprintf(sgCfg.decideRecordFile, "%s%s%d%s", root, "/clari5/logs/decide-elapsed-", ((int) getpid()), ".log");
        sprintf(sgCfg.enqueueRecordFile, "%s%s%d%s", root, "/clari5/logs/enqueue-elapsed-", ((int) getpid()), ".log");
        sprintf(sgCfg.saf, "%s%s", root, "/clari5/saf");
        last_time = 0;
        initialized = 1;
    }

    /* If config file exists and last parsed time is not same then parse and set params */
    if (stat(sCfgFile, &st) == 0) {
        if (st.st_mtime != last_time) {
            last_time = st.st_mtime;
            cxParse(sCfgFile, &sgCfg);
        }
    } else {
        nbsLog("Error: Config File [%s] Not found. Applying default configuration", sCfgFile);

        strcpy(sgCfg.hostname, "server");
        strcpy(sgCfg.decideProtocol, "UDP");
        strcpy(sgCfg.enqueueProtocol, "UDP");
        strcpy(sgCfg.decideAuth, "finacle:cxps123");
        strcpy(sgCfg.decideDefResponse, "{\"advice\":\"ALLOW\"}");
        sgCfg.decideTimeout = 2000;
        sgCfg.timeoutEnqueue = 500;
        sgCfg.buckets = 1;
        sgCfg.timeSlice = 32767;
        sgCfg.debug = 'N';
        sgCfg.timeFieldsOverride = 'Y';
        sgCfg.numPortsEnqueue = 0;
        sgCfg.numPortsDecide = 0;
        sgCfg.numDecideUrls = 0;
        sgCfg.recordDecide = 'Y';
        sgCfg.recordEnqueue = 'Y';
        last_time = 0;        /* Also reset the time stamp */

        dbgLog(&sgCfg, "Default Configuration applied as: \nHost name: [%s]\nDecide Protocol: [%s]\nEnqueue Protocol: [%s]\nDecide Default Response: [%s]\nLog File: [%s]\n SAF Folder: [%s]\nSAF Time Slice: [%d]",
               sgCfg.hostname, sgCfg.decideProtocol, sgCfg.enqueueProtocol, sgCfg.decideDefResponse, sgCfg.logFile, sgCfg.saf, sgCfg.timeSlice);
    }

    sprintf(buffer, "%s%s", root, "/clari5");
    createDirIfNotExists(buffer);

    createDirIfNotExists(sgCfg.saf);

    sprintf(buffer, "%s%s", root, "/clari5/logs");
    createDirIfNotExists(buffer);

    return &sgCfg;
}

void createDirIfNotExists(char *buffer) {
    struct stat st;
    if (stat(buffer, &st) != 0)
        mkdir(buffer, 0777);
    else
        chmod(buffer, 0777);
}

PUBLIC int getProdRoot(char* root) {
    char libPath[8192] = "";
    char* location;
    if (((location = getenv("LD_LIBRARY_PATH")) != NULL) && (strcmp(location, "(null)") != 0))
        sprintf(libPath, "%s%s", strndup(location, 8190), ":");

    int remaining = (8190 - strlen(libPath));
    if ((remaining > 0) && ((location = getenv("LIBPATH")) != NULL) && (strcmp(location, "(null)") != 0)) {
        strcat(libPath, strndup(location, remaining));
        strcat(libPath, ":");
    }

    remaining = (8190 - strlen(libPath));
    if ((remaining > 0) && ((location = getenv("SHLIB_PATH")) != NULL) && (strcmp(location, "(null)") != 0)) {
        strcat(libPath, strndup(location, remaining));
        strcat(libPath, ":");
    }

    char *path = strtok(libPath, ":");
    char filePath[8192] = "";
    bool fileExists = false;
    while (path != NULL) {
        strcpy(filePath, path);
        struct stat st;
        if ((strcmp(filePath, "(null)") != 0) && strlen(filePath) > 0) {
            int length = (int) strlen(filePath);
            if (filePath[length-1] != '/')
                strcat(filePath, "/fmsRoot.properties");
            else
                strcat(filePath, "fmsRoot.properties");

            /* If fmsRoot.properties exists in any of the LD Library Paths, extract CXPS_PROD_ROOT from the file */
            if (stat(filePath, &st) == 0) {
                fileExists = true;
                break;
            }
        }
        path = strtok(NULL, ":");
    }

    if (!fileExists) {
        strcpy(root, "/tmp");
        return 0;
    }

    FILE *fp = NULL;
    char line[1024] = "";
    char *name = NULL;
    char *value = NULL;
    int lineIndex, i, j, k;
    fp = fopen(filePath, "r");
    if (fp == NULL) {
        strcpy(root, "/tmp");
        return 0;
    }

    lineIndex = 0;
    while (fgets(line, LINESIZE, fp) != (char *) 0) {

        ++lineIndex;

        /* Skip spaces */
        for (i = 0; line[i] != '\0' && isspace(line[i]); i++);

        if (line[i] == '\0') {
            /* Empty Line */
            continue;
        }

        /* Skip comments */
        for (j = i; (line[j] != '\0') && (line[j] != '#'); j++);
        if (line[j] == '#') line[j] = '\0';

        /* Blank line */
        if (i == j) {
            /* Empty Comment Line */
            continue;
        }

        /* Find '=' */
        for (j = i + 1; (line[j] != '\0') && (line[j] != '='); j++);
        if (line[j] == '\0') {
            /* Error:No '=' found */
            fprintf(stderr, "Property file Error: No '=' found at Line: [%d]!", lineIndex);
            continue;
        }

        /* Trim RHS for name */
        for (k = j - 1; isspace(line[k]); k--);
        line[k + 1] = '\0';
        name = &line[i];

        /* Skip initial spaces from RHS value */
        for (i = j + 1; line[i] != '\0' && isspace(line[i]); i++);

        if (line[i] == '\0') {
            /* Nothing found after '='. Empty RHS. No Value for parameter. Might be left empty intentionally, error not thrown */
            continue;
        }

        /* Go to the end and start skipping space towards back */
        for (j = i; line[j] != '\0'; j++);
        for (--j; isspace(line[j]); j--);

        line[j + 1] = '\0';
        value = &line[i];

        if (strcasecmp(name, "CXPS_PROD_ROOT") == 0) {
            if (strlen(value) > 0 && strlen(value) < 8192)
                strcpy(root, value);
            else
                strcpy(root, "/tmp");
            break;
        }
    }

    if (fclose(fp) != 0)
        fprintf(stderr, "Error: Unable to close file - [%s]", strerror(errno));

    return 1;
}

/**
 * ---------------------------------------------------------------------------
 * This function parses properties file
 * It expects following property attributes
 * HOSTNAME (mandatory)
 * DECIDE_URL_LIST (mandatory)
 * DECIDE_AUTH (mandatory)
 * DECIDE_TIMEOUT_IN_MS (defaults to 500 ms)
 * SAF (defaults to .../clari5/saf)
 * SAF_TIME_SLICE_IN_MIN (defaults to 5 mins)
 * UDP_PORT_LIST (absence will result in only saf message)
 * UDP_TIMEOUT_IN_MS (defaults to 250 ms)
 * DEBUG (defaults to N)
 * ---------------------------------------------------------------------------
 */
PRIVATE int cxParse(const char *config_file, struct config *pCfg) {

    FILE *fp = NULL;
    char line[1024] = "";
    char *name = NULL;
    char *value = NULL;
    int l, i, j, k;

    /* Initialize the Configuration Structure */
    pCfg->portsEnqueue[0] = -1;
    pCfg->portsDecide[0] = -1;
    strcpy(pCfg->hostname, "server");
    strcpy(pCfg->enqueueProtocol, "UDP");
    strcpy(pCfg->decideProtocol, "UDP");
    strcpy(pCfg->decideAuth, "finacle:cxps123");
    strcpy(pCfg->decideDefResponse, "{\"advice\":\"ALLOW\"}");
    pCfg->buckets = 1;
    pCfg->decideTimeout = 1000;
    pCfg->timeoutEnqueue = 500;
    pCfg->debug = 'N';
    pCfg->timeFieldsOverride = 'Y';
    pCfg->numPortsEnqueue = 0;
    pCfg->numPortsDecide = 0;
    pCfg->numDecideUrls = 0;
    pCfg->timeSlice = 300;
    pCfg->recordDecide = 'Y';
    pCfg->recordEnqueue = 'Y';

    if ((fp = fopen(config_file, "r")) == (FILE *) 0)
        return 0;

    l = 0;
    while (fgets(line, LINESIZE, fp) != (char *) 0) {

        ++l;

        /* Skip spaces */
        for (i = 0; line[i] != '\0' && isspace(line[i]); i++);

        if (line[i] == '\0') {
            /* Empty Line */
            continue;
        }

        /* Skip comments */
        for (j = i; (line[j] != '\0') && (line[j] != '#'); j++);
        if (line[j] == '#') line[j] = '\0';

        /* Blank line */
        if (i == j) {
            /* Empty Comment Line */
            continue;
        }

        /* Find '=' */
        for (j = i + 1; (line[j] != '\0') && (line[j] != '='); j++);
        if (line[j] == '\0') {
            /* Error:No '=' found */
            cxLog(pCfg->logFile, "Property file Error: No '=' found at Line: [%d]!", l);
            continue;
        }

        /* Trim RHS for name */
        for (k = j - 1; isspace(line[k]); k--);
        /* name = line[i to k] */
        line[k + 1] = '\0';
        name = &line[i];

        /* Skip initial spaces from RHS value */
        for (i = j + 1; line[i] != '\0' && isspace(line[i]); i++);

        if (line[i] == '\0') {
            /* Nothing found after '='. Empty RHS. No Value for parameter. Might be left empty intentionally, error not thrown */
            continue;
        }

        /* Go to the end and start skipping space towards back */
        for (j = i; line[j] != '\0'; j++);
        for (--j; isspace(line[j]); j--);

        line[j + 1] = '\0';
        value = &line[i];

        char ports[1000];
        char urls[1000];

        if (strcasecmp(name, "HOSTNAME") == 0) {
            if (strlen(value) > 255) {
                strcpy(pCfg->hostname, strndup(value, 254));
                cxLog(pCfg->logFile,
                      "Configured Host name [%s] exceeds permitted length of 255 characters. First 254 characters taken as host name [%s] is configured",
                      value, pCfg->hostname);
            } else {
                strcpy(pCfg->hostname, value);
            }

            if ((pCfg->hostname == ((char *) 0)) || (strcmp(pCfg->hostname, "") == 0)) {
                strcpy(pCfg->hostname, "server");
                cxLog(pCfg->logFile,"Configured Host name is empty or string copy failed. Default host name [%s] is configured", pCfg->hostname);
            }
        } else if (strcasecmp(name, "FMS_DECIDE_TRANSMISSION_PROTOCOL") == 0) {
            if (strcmp(value, "UDP") != 0 && strcmp(value, "TCP") != 0 && strcmp(value, "HTTP") != 0) {
                strcpy(pCfg->decideProtocol, "UDP");
                cxLog(pCfg->logFile,
                      "Configured FMS Decide Transmission Protocol [%s] should either be UDP / TCP / HTTP. Default protocol [%s] is configured",
                      value, pCfg->decideProtocol);
            } else {
                strcpy(pCfg->decideProtocol, value);
                if ((pCfg->decideProtocol == ((char *) 0)) || (strcmp(pCfg->decideProtocol, "") == 0)) {
                    strcpy(pCfg->decideProtocol, "UDP");
                    cxLog(pCfg->logFile, "Configured FMS Decide Transmission Protocol is empty or string copy failed. "
                            "Default protocol [%s] is configured", pCfg->decideProtocol);
                } else if ((strcmp(pCfg->decideProtocol, "UDP") != 0) && (strcmp(pCfg->decideProtocol, "TCP") != 0)) {
                    strcpy(pCfg->decideProtocol, "UDP");
                    cxLog(pCfg->logFile,
                          "Configured FMS Transmission Protocol can only either be UDP or TCP or HTTP but was configured to [%s]. Default protocol [%s] is configured",
                          value, pCfg->decideProtocol);
                }
            }
        } else if (strcasecmp(name, "FMS_ENQUEUE_TRANSMISSION_PROTOCOL") == 0) {
            if (strcmp(value, "UDP") != 0 && strcmp(value, "TCP") != 0 && strcmp(value, "HTTP") != 0) {
                strcpy(pCfg->enqueueProtocol, "UDP");
                cxLog(pCfg->logFile,
                      "Configured FMS Enqueue Transmission Protocol [%s] should either be UDP / TCP / HTTP. Default protocol [%s] is configured",
                      value, pCfg->enqueueProtocol);
            } else {
                strcpy(pCfg->enqueueProtocol, value);
                if ((pCfg->enqueueProtocol == ((char *) 0)) || (strcmp(pCfg->enqueueProtocol, "") == 0)) {
                    strcpy(pCfg->enqueueProtocol, "UDP");
                    cxLog(pCfg->logFile, "Configured FMS Enqueue Transmission Protocol is empty or string copy failed. "
                            "Default protocol [%s] is configured", pCfg->enqueueProtocol);
                } else if ((strcmp(pCfg->enqueueProtocol, "UDP") != 0) && (strcmp(pCfg->enqueueProtocol, "TCP") != 0)) {
                    strcpy(pCfg->enqueueProtocol, "UDP");
                    cxLog(pCfg->logFile,
                          "Configured FMS Transmission Protocol can only either be UDP or TCP or HTTP but was configured to [%s]. Default protocol [%s] is configured",
                          value, pCfg->enqueueProtocol);
                }
            }
        } else if (strcasecmp(name, "SAF_TIME_SLICE_IN_SEC") == 0) {
            pCfg->timeSlice = atoi(value);
            if (pCfg->timeSlice <= 0 || pCfg->timeSlice > 7200) {
                cxLog(pCfg->logFile, "Configured SAF time slice [%s] is invalid. Time slice must range between 1 - 120 minutes (inclusive)", value);
                if (pCfg->timeSlice <= 0) pCfg->timeSlice = 300;
                else pCfg->timeSlice = 7200;
                dbgLog(pCfg, "Re-setting SAF Time slice to [%d] seconds", pCfg->timeSlice);
            }
        } else if (strcasecmp(name, "SAF_BUCKET_SIZE") == 0) {
            pCfg->buckets = atoi(value);
            if (pCfg->buckets < 1 || pCfg->buckets > 100) {
                cxLog(pCfg->logFile, "Configured SAF Bucket size [%s] is invalid. Time slice must range between 1 - 100 buckets (inclusive)", value);
                if (pCfg->buckets < 1) pCfg->buckets = 1;
                else pCfg->buckets = 100;
                dbgLog(pCfg, "Re-setting SAF Bucket size to [%d] buckets", pCfg->buckets);
            }
        } else if (strcasecmp(name, "ENQUEUE_PORT_LIST") == 0) {
            strcpy(ports, value);
            char *port = strtok(ports, ",");
            while (port != NULL) {
                pCfg->portsEnqueue[pCfg->numPortsEnqueue] = atoi(port);
                if (pCfg->portsEnqueue[pCfg->numPortsEnqueue] < 0) {
                    cxLog(pCfg->logFile, "Invalid port [%d] defined in config for enqueue. Ignoring the port",
                          pCfg->portsEnqueue[pCfg->numPortsEnqueue]);
                }
                port = strtok(NULL, ",");
                if (pCfg->portsEnqueue[pCfg->numPortsEnqueue] > 0)
                    pCfg->numPortsEnqueue++;
            }
        } else if (strcasecmp(name, "ENQUEUE_TIMEOUT_IN_MS") == 0) {
            pCfg->timeoutEnqueue = atoi(value);
            if (pCfg->timeoutEnqueue <= 0)
                pCfg->timeoutEnqueue = 500;
            else if (pCfg->timeoutEnqueue > 20000)
                pCfg->timeoutEnqueue = 20000;
        } else if (strcasecmp(name, "DECIDE_PORT_LIST") == 0) {
            strcpy(ports, value);
            char *port = strtok(ports, ",");
            while (port != NULL) {
                pCfg->portsDecide[pCfg->numPortsDecide] = atoi(port);
                if (pCfg->portsDecide[pCfg->numPortsDecide] < 0) {
                    cxLog(pCfg->logFile, "Invalid port [%d] defined in config for decide. Ignoring the port",
                          pCfg->portsDecide[pCfg->numPortsDecide]);
                }
                port = strtok(NULL, ",");
                if (pCfg->portsDecide[pCfg->numPortsDecide] > 0)
                    pCfg->numPortsDecide++;
            }
        } else if (strcasecmp(name, "DECIDE_TIMEOUT_IN_MS") == 0) {
            pCfg->decideTimeout = atoi(value);
            if (pCfg->decideTimeout <= 0)
                pCfg->decideTimeout = 1000;
            else if (pCfg->decideTimeout > 20000)
                pCfg->decideTimeout = 20000;
        } else if (strcasecmp(name, "DECIDE_URL_LIST") == 0) {
            strcpy(urls, value);
            char *url = strtok(urls, ",");
            while (url != NULL) {
                pCfg->decideUrls[pCfg->numDecideUrls] = strdup(url);
                if (strlen(pCfg->decideUrls[pCfg->numDecideUrls]) > 1024) {
                    cxLog(pCfg->logFile, "Invalid url [%s] configured for decide. Ignoring the url", pCfg->decideUrls[pCfg->numDecideUrls]);
                } else
                    pCfg->numDecideUrls++;
                url = strtok(NULL, ",");
            }
        } else if (strcasecmp(name, "DECIDE_AUTH") == 0) {
            strcpy(pCfg->decideAuth, value);
        } else if (strcasecmp(name, "DECIDE_DEFAULT_RESPONSE") == 0) {
            strcpy(pCfg->decideDefResponse, value);
        } else if (strcasecmp(name, "DEBUG") == 0) {
            pCfg->debug = value[0];
        } else if (strcasecmp(name, "TIME_FIELDS_OVERRIDE") == 0) {
            pCfg->timeFieldsOverride = value[0];
        } else if (strcasecmp(name, "RECORD_DECIDE_ELAPSED") == 0) {
            pCfg->recordDecide = value[0];
        } else if (strcasecmp(name, "RECORD_ENQUEUE_ELAPSED") == 0) {
            pCfg->recordEnqueue = value[0];
        }
    }

    if (fclose(fp) != 0)
        fprintf(stderr, "Error: Unable to close file - [%s]", strerror(errno));

    /* Sanity checks */
    if (pCfg->numPortsEnqueue == 0)
        dbgLog(pCfg->logFile, "No [%s] for ENQUEUE!", pCfg->enqueueProtocol);
    if (pCfg->numPortsDecide == 0)
        dbgLog(pCfg->logFile, "No [%s] for DECIDE!", pCfg->decideProtocol);

    dbgLog(pCfg, "Configuration set successfully as: " +
    "\nHost name: [%s]\nDecide Protocol: [%s]\nEnqueue Protocol: [%s]" +
    "\nDecide Default Response: [%s]\nLog File: [%s]" +
    "\nSAF Folder: [%s]\nSAF Buckets: [%d]\nSAF Time Slice: [%d]" +
    "\nNumber of [%s] decide ports: [%d]\nNumber of [%s] enqueue ports: [%d]" +
    "\nDecide [%s] timeout: [%d]\nEnqueue [%s] timeout: [%d]",
           pCfg->hostname, pCfg->decideProtocol, pCfg->enqueueProtocol,
           pCfg->decideDefResponse, pCfg->logFile,
           pCfg->saf, pCfg->buckets, pCfg->timeSlice,
           pCfg->decideProtocol, pCfg->numPortsDecide, pCfg->enqueueProtocol, pCfg->numPortsEnqueue,
           pCfg->decideProtocol, pCfg->decideTimeout, pCfg->enqueueProtocol, pCfg->timeoutEnqueue);

    return 1;
}

/**
 * ===========================================================================
 * This method returns one of the DECIDE URL (randomly).
 * @param cfg
 * @return
 * ===========================================================================
 */
PUBLIC const char *getDecideUrl(const struct config *pCfg) {
    if (pCfg->numDecideUrls == 1)
        return pCfg->decideUrls[0];
    else if (pCfg->numDecideUrls > 1)
        return pCfg->decideUrls[rand() % pCfg->numDecideUrls];
    else
        return (char *) 0;
}

PUBLIC int getBucketIndex(const struct config *pCfg) {
    if (pCfg->buckets == 1)
        return 1;
    else if (pCfg->buckets > 1)
        return ((rand() % pCfg->buckets) + 1);
    else {
        cxLog(pCfg->logFile, "No bucket defined or invalid SAF bucket size");
        return -1;
    }
}

/**
 * ===========================================================================
 * This method returns one of the UDP ports (randomly)
 * @param cfg
 * @return
 * ===========================================================================
 */

PUBLIC int getPortEnqueue(const struct config *pCfg) {
    if (pCfg->numPortsEnqueue == 1)
        return pCfg->portsEnqueue[0];
    else if (pCfg->numPortsEnqueue > 1)
        return pCfg->portsEnqueue[rand() % pCfg->numPortsEnqueue];
    else {
        cxLog(pCfg->logFile, "No port defined or invalid UDP Enqueue port. Disabling UDP");
        return -1;
    }
}

PUBLIC int getPortDecide(const struct config *pCfg) {
    if (pCfg->numPortsDecide == 1)
        return pCfg->portsDecide[0];
    else if (pCfg->numPortsDecide > 1)
        return pCfg->portsDecide[rand() % pCfg->numPortsDecide];
    else {
        cxLog(pCfg->logFile, "No port defined or invalid UDP Decide port. Disabling UDP");
        return -1;
    }
}


