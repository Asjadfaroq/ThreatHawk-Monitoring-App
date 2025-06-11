package com.java.threathawk.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.List;

public class RemoteLogger{

    Logger logger;
    UDP_Client client;
    Handler h = new Handler();
    Runnable r;
    public Integer forward_interval = 1000 * 10 * 1;
    public RemoteLogger(String ServerIP, Integer Port){
        logger = new Logger();
        client = new UDP_Client(ServerIP,Port);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ForwardLogs(Logger.TYPES types,Logger.LogLevel level,String Tag){
        logger.Start(types,Tag,level);
        r = new Runnable() {
            @Override
            public void run() {
                List<String> logs = logger.getLogs(0,1000);
                for (String log: logs
                ) {
                    client.Message = log;
                    client.Send();
                }
                logger.clearBuffer(0,logs.size());
                h.postDelayed(this,forward_interval);
            }
        };
        h.postDelayed(r, forward_interval);

    }
    public void Stop(){
        h.removeCallbacks(r);
        client.Stop();
        logger.Stop();
    }
}
