package com.xbl.performance;

import android.app.Application;

public class PerformanceManager {

    public static class Builder{
        private boolean checkBlock;
        private boolean checkFps;
        private boolean cupMemory;
        private long checkBlockInterval = Config.BLOCK_WATCHER_INTERVAL;
        private long checkFpsInterval = Config.FPS_WATCHER_INTERVAL;
        private Application application;

        public Builder(Application app){
            application = app;
        }

        public Builder CheckBlock(boolean checkBlock) {
            this.checkBlock = checkBlock;
            return this;
        }

        public Builder CheckBlock(boolean checkBlock,long checkBlockInterval) {
            this.checkBlock = checkBlock;
            this.checkBlockInterval = checkBlockInterval;
            return this;
        }


        public Builder checkFps(boolean checkFps) {
            this.checkFps = checkFps;
            return this;
        }

        public Builder checkFps(boolean checkFps, long checkFpsInterval) {
            this.checkFps = checkFps;
            this.checkFpsInterval = checkFpsInterval;
            return this;
        }

        public Builder checkCpuMem(boolean checkCpuMemory) {
            this.cupMemory = checkCpuMemory;
            return this;
        }
    }

    public static void init(Builder builder) {
        if (builder != null) {
            if (builder.checkBlock) {
                Config.BLOCK_WATCHER_INTERVAL = builder.checkBlockInterval;
                BlockManager.start();
            }

            if (builder.checkFps) {
                Config.FPS_WATCHER_INTERVAL = builder.checkFpsInterval;
                FpsManager.start();
            }
            if (builder.cupMemory) {
                CpuMemoryManager.start(builder.application);
            }
        }
    }
}
