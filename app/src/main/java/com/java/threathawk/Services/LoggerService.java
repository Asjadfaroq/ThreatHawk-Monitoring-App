package com.java.threathawk.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.java.threathawk.R;
import com.java.threathawk.classes.Logger;
import com.java.threathawk.classes.RemoteLogger;
import com.java.threathawk.classes.Settings;

public class LoggerService extends Service {

    public static String CHANEL_ID = "14569";
    String ip ="";
    Integer port = 514;
    Logger.TYPES type = Logger.TYPES.ALL;
    Logger.LogLevel level= Logger.LogLevel.All;
    String Tag = "Android-";
    RemoteLogger remoteLogger;
    public LoggerService() {
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        createNotificationChannel();
        ip = intent.getStringExtra("ip");
        port = intent.getIntExtra("port",514);
        type = (Logger.TYPES) intent.getExtras().get("type");
        level = (Logger.LogLevel) intent.getExtras().get("level");
        Tag = intent.getExtras().get("tag").toString();

        remoteLogger = new RemoteLogger(ip,port);
        Intent notificationIntent = new Intent(this, RemoteLogger.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);

        Notification notification =
                new Notification.Builder(this,CHANEL_ID)
                        .setContentTitle("THREATHAWK")
                        .setContentText("Monitoring your android device")
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentIntent(pendingIntent)
                        .setTicker("Ticker")
                        .build();
        startForeground(1, notification);
        remoteLogger.ForwardLogs(type,level,Tag);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        throw new UnsupportedOperationException("Not yet implemented");
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    LoggerService.CHANEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Settings s = new Settings();
        s = Settings.GetSettings(this);
        if(s.isOn){
            createNotificationChannel();
        }
        super.onTaskRemoved(rootIntent);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
         remoteLogger.Stop();
    }
}