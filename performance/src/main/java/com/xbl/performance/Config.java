package com.xbl.performance;

public class Config {
    //卡顿超时时间
    public static long BLOCK_WATCHER_INTERVAL = 1 * 1000;
    public static long FPS_WATCHER_INTERVAL = 1 * 1000;


    public static long CPU_MEMORY_INTERVAL = 500;

    public static int DEFAULT_MAX_FPS = 60;

    //堆栈抓取时间
    public static long DUMP_INTERVAL = 500;

    public static final String TYPE_BLOCK = "type_block";

    public static final String PERFORMANCE_TAG = "percormance_tag";
    public static final String CPU_MEMERY_TAG = "cpu_memory_tag";
}
