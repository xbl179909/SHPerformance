package com.xbl.performance;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Printer;

import java.util.List;

public class BlockManager {


    private static Handler mDumpStackHandler;
    private static DumpStackRunnable mDumpStackRunnable = new DumpStackRunnable();
    private static DumpStackThread mDumpStackThread = new DumpStackThread("dumpStackThread");
    public static void start() {
        mDumpStackThread.start();
        Looper.getMainLooper().setMessageLogging(new BlockPrinter());
    }


    /**
     *通过looper构造工作于子线程的handler
     */
    private static class DumpStackThread extends Thread{
        DumpStackThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            Looper.prepare();
            mDumpStackHandler = new Handler(Looper.myLooper());
            Looper.loop();
        }
    }

    /**
     * 系统暴露的打印器接口
     */
    private static class BlockPrinter implements Printer{

        @Override
        public void println(String s) {
            if (!TextUtils.isEmpty(s) && s.startsWith(">>>>>")) {
                startWatcher();
            } else if (!TextUtils.isEmpty(s) && s.startsWith("<<<<<")) {
                removeWatcher();
            }
        }
    }

    /**
     * 子线程工作的任务
     */
    private static class DumpStackRunnable implements Runnable{
        @Override
        public void run() {
            List<String> stackList = StackUtils.generateStackStr();
            StackInfo info = new StackInfo(Config.TYPE_BLOCK, stackList);
            info.printStack();
        }
    }

    private static void startWatcher(){
        if (mDumpStackHandler != null) {
            mDumpStackHandler.removeCallbacks(mDumpStackRunnable);
            mDumpStackHandler.postDelayed(mDumpStackRunnable, Config.BLOCK_WATCHER_INTERVAL);
        }
    }

    private static void removeWatcher() {
        if (mDumpStackHandler != null) {
            mDumpStackHandler.removeCallbacks(mDumpStackRunnable);

        }
    }
}
