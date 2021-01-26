package com.xbl.performance;

import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

public class StackUtils {


    public static List<String> generateStackStr() {
        StackTraceElement[] stackTraceElements = Looper.getMainLooper().getThread().getStackTrace();
        List<String> stackStrList = new ArrayList<>();
        for (int i = 0; i < stackTraceElements.length; i++) {
            stackStrList.add(stackToStr(stackTraceElements[i]));
        }
        return stackStrList;

    }

    public static String stackToStr(StackTraceElement element) {
        StringBuilder sb = new StringBuilder();
        sb.append(element.getClassName())
                .append(".")
                .append(element.getMethodName())
                .append("(")
                .append(element.getFileName())
                .append(":")
                .append(element.getLineNumber())
                .append(")");
        return sb.toString();
    }
}
