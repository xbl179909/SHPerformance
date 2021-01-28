package com.xbl.performance;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;


public class CpuMemoryManager {

    private static HandlerThread mHandlerThread = new HandlerThread("cpu_memory_thread");
    private static Handler mHandler;
    private static Handler mMainHandler = new Handler(Looper.getMainLooper());
    private static int MSG_TYPE_CPU = 1;
    private static RandomAccessFile mProcStatFile;
    private static RandomAccessFile mAppStatFile;
    private static ActivityManager mActivityManager;
    private static Long mLastCpuTime;
    private static Long mLastAppCpuTime;
    private static float cpuData;
    private static float memData;

    public static void start(Application application) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
        }
        if (mHandler == null) {
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == MSG_TYPE_CPU) {
                        makeCpuMemoryData();
                        mHandler.sendEmptyMessageDelayed(MSG_TYPE_CPU, Config.CPU_MEMORY_INTERVAL);
                    }
                }
            };
        }
        mHandler.sendEmptyMessageDelayed(MSG_TYPE_CPU, Config.CPU_MEMORY_INTERVAL);
    }

    private static void makeCpuMemoryData() {
        makeCpuData();
        memData = getMemoryData();
        printCpuMemory();
    }

    private static void printCpuMemory() {
        StringBuilder sb = new StringBuilder();
        sb.append("----cpu使用率= ")
                .append(cpuData)
                .append("%")
                .append("----memoryInfo = ")
                .append(memData)
                .append("M\n");
        Log.d(Config.CPU_MEMERY_TAG, sb.toString());

    }
    private static void makeCpuData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cpuData = getCpuDataO();
        } else {
            cpuData = getCPUData();
        }
    }

    private static float getMemoryData() {
        float mem = 0.0F;
        try {
            Debug.MemoryInfo memInfo = null;
            //28 为Android P
            if (Build.VERSION.SDK_INT > 28) {
                // 统计进程的内存信息 totalPss
                memInfo = new Debug.MemoryInfo();
                Debug.getMemoryInfo(memInfo);
            } else {
                //As of Android Q, for regular apps this method will only return information about the memory info for the processes running as the caller's uid;
                // no other process memory info is available and will be zero. Also of Android Q the sample rate allowed by this API is significantly limited, if called faster the limit you will receive the same data as the previous call.

                Debug.MemoryInfo[] memInfos = mActivityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
                if (memInfos != null && memInfos.length > 0) {
                    memInfo = memInfos[0];
                }
            }

            int totalPss = memInfo.getTotalPss();
            if (totalPss >= 0) {
                // Mem in MB
                mem = totalPss / 1024.0F;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mem;
    }

    private static float getCPUData() {
        long cpuTime;
        long appTime;
        float value = 0.0f;
        try {
            if (mProcStatFile == null || mAppStatFile == null) {
                mProcStatFile = new RandomAccessFile("/proc/stat", "r");
                mAppStatFile = new RandomAccessFile("/proc/" + android.os.Process.myPid() + "/stat", "r");
            } else {
                mProcStatFile.seek(0L);
                mAppStatFile.seek(0L);
            }
            String procStatString = mProcStatFile.readLine();
            String appStatString = mAppStatFile.readLine();
            String procStats[] = procStatString.split(" ");
            String appStats[] = appStatString.split(" ");
            cpuTime = Long.parseLong(procStats[2]) + Long.parseLong(procStats[3])
                    + Long.parseLong(procStats[4]) + Long.parseLong(procStats[5])
                    + Long.parseLong(procStats[6]) + Long.parseLong(procStats[7])
                    + Long.parseLong(procStats[8]);
            appTime = Long.parseLong(appStats[13]) + Long.parseLong(appStats[14]);
            if (mLastCpuTime == null && mLastAppCpuTime == null) {
                mLastCpuTime = cpuTime;
                mLastAppCpuTime = appTime;
                return value;
            }
            value = ((float) (appTime - mLastAppCpuTime) / (float) (cpuTime - mLastCpuTime)) * 100f;
            mLastCpuTime = cpuTime;
            mLastAppCpuTime = appTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private static float getCpuDataO() {
        java.lang.Process process = null;
        try {
            process = Runtime.getRuntime().exec("top -n 1");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int cpuIndex = -1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (TextUtils.isEmpty(line)) {
                    continue;
                }
                int tempIndex = getCPUIndex(line);
                if (tempIndex != -1) {
                    cpuIndex = tempIndex;
                    continue;
                }
                if (line.startsWith(String.valueOf(Process.myPid()))) {
                    if (cpuIndex == -1) {
                        continue;
                    }
                    String[] param = line.split("\\s+");
                    if (param.length <= cpuIndex) {
                        continue;
                    }
                    String cpu = param[cpuIndex];
                    if (cpu.endsWith("%")) {
                        cpu = cpu.substring(0, cpu.lastIndexOf("%"));
                    }
                    float rate = Float.parseFloat(cpu) / Runtime.getRuntime().availableProcessors();
                    return rate;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return 0;
    }

    private static int getCPUIndex(String line) {
        if (line.contains("CPU")) {
            String[] titles = line.split("\\s+");
            for (int i = 0; i < titles.length; i++) {
                if (titles[i].contains("CPU")) {
                    return i;
                }
            }
        }
        return -1;
    }
}
