//package com.daicy.redis.bean;
//
//import java.util.List;
//
///**
// * @author: create by daichangya
// * @version: v1.0
// * @description: com.daicy.redis.bean
// * @date:11/6/20
// */
//public class RedisServer {
////    int port;
////    int fd;
////    dict **dict;
////    long long dirty;            /* changes to DB from the last save */
////    list *clients;
////    list *slaves;
////    char neterr[ANET_ERR_LEN];
////    aeEventLoop *el;
////    int cronloops;              /* number of times the cron function run */
////    list *objfreelist;          /* A list of freed objects to avoid malloc() */
////    time_t lastsave;            /* Unix time of last save succeeede */
////    /* Configuration */
////    int verbosity;
////    int glueoutputbuf;
////    int maxidletime;
////    int dbnum;
////    int daemonize;
////    int bgsaveinprogress;
////    struct saveparam *saveparams;
////    int saveparamslen;
////    char *logfile;
////    char *bindaddr;
////    char *dbfilename;
////    /* Replication related */
////    int isslave;
////    char *masterhost;
////    int masterport;
////    redisClient *master;
////    int replstate;
////    /* Sort parameters - qsort_r() is only available under BSD so we
////     * have to take this state global, in order to pass it to sortCompare() */
////    int sort_desc;
////    int sort_alpha;
////    int sort_bypattern;
//
//    private int port;
//    private int fd;
//    private Dict[] dict;
//    private long dirty;            /* changes to DB from the last save */
//    private List clients;
//    private List slaves;
//    private char neterr[ANET_ERR_LEN];
//    aeEventLoop *el;
//    private int cronloops;              /* number of times the cron function run */
//    private List objfreelist;          /* A list of freed objects to avoid malloc() */
//    time_t lastsave;            /* Unix time of last save succeeede */
//    /* Configuration */
//    private int verbosity;
//    private int glueoutputbuf;
//    private int maxidletime;
//    private int dbnum;
//    private int daemonize;
//    private int bgsaveinprogress;
//    struct saveparam *saveparams;
//    private int saveparamslen;
//    private char logfile;
//    private char bindaddr;
//    private char dbfilename;
//    /* Replication related */
//    private int isslave;
//    private char masterhost;
//    private int masterport;
//    redisClient master;
//    private int replstate;
//    /* Sort parameters - qsort_r() is only available under BSD so we
//     * have to take this state global, in order to pass it to sortCompare() */
//    private int sort_desc;
//    private int sort_alpha;
//    private int sort_bypattern;
//}
