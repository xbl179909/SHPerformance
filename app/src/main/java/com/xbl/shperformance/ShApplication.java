package com.xbl.shperformance;

import android.app.Application;
import android.content.Context;

import com.xbl.performance.BlockManager;
import com.xbl.performance.FpsManager;
import com.xbl.performance.PerformanceManager;

public class ShApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        BlockManager.start();
//        FpsManager.start();
        PerformanceManager.Builder builder = new PerformanceManager.Builder(this)
                .CheckBlock(true)
                .checkFps(true)
                .checkCpuMem(true);
        PerformanceManager.init(builder);
    }
}
