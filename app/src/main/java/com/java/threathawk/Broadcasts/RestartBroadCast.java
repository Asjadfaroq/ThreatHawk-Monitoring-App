package com.java.threathawk.Broadcasts;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.java.threathawk.Activity.MainActivity;
import com.java.threathawk.Services.LoggerService;
import com.java.threathawk.classes.Settings;

public class RestartBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("OnReceive","Inside on receive ");

        Settings s = new Settings();
        s = s.GetSettings(context);
        Log.d("OnReceive","Inside on receive ");
        if(s.isOn) {
            Intent serviceIntent = new Intent(context, LoggerService.class);
            serviceIntent.putExtra("ip",s.ip);
            serviceIntent.putExtra("port",s.port);
            serviceIntent.putExtra("type",s.type);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }
}
