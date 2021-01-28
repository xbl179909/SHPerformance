package com.xbl.performance;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Choreographer;

public class FpsManager {

    private static FrameWatcherRunnable frameWatcherRunnable = new FrameWatcherRunnable();
    private static Handler handler = new Handler();

    public static void start() {
        handler.post(frameWatcherRunnable);
        Choreographer.getInstance().postFrameCallback(frameWatcherRunnable);
    }


    private static class FrameWatcherRunnable implements Runnable, Choreographer.FrameCallback{

        long time = -1;
        int count = 0;
        @Override
        public void doFrame(long l) {
            count++;
            Choreographer.getInstance().postFrameCallback(this);
        }

        @Override
        public void run() {
            long curTime = SystemClock.elapsedRealtime();
            if (time != -1) {
                int fps = (int) (1000f * count / (curTime - time));
                if (fps > Config.DEFAULT_MAX_FPS) {
                    fps = Config.DEFAULT_MAX_FPS;
                }
                Log.d(Config.PERFORMANCE_TAG, String.format("Cur FPS is %S", fps));
            }
            time = curTime;
            count = 0;
            handler.postDelayed(this, Config.FPS_WATCHER_INTERVAL);
        }
    }
}
