package com.xbl.performance;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StackInfo {
    private String type;
    private Object data;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public StackInfo(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public void printStack() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n")
                .append("===========================================\n")
                .append("type = ")
                .append(type).append('\n')
                .append("time = ")
                .append(dateFormat.format(new Date())).append('\n');

        if (data instanceof List) {
            List<Object> datas = (List<Object>) data;
            sb.append("traceStack :\n");
            for (int i = 0; i < datas.size(); i++) {
                sb.append('\t').append(datas.get(i)).append('\n');
            }
        }
        Log.d(Config.PERFORMANCE_TAG, sb.toString());
    }


}
