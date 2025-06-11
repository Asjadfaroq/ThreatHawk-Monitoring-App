package com.java.threathawk.classes;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import com.java.threathawk.R;

import needle.BackgroundThreadExecutor;
import needle.Needle;
import needle.UiRelatedTask;

public class UDP_Client {
    public String Message;
    public String ip;
    public Integer port;

    public UDP_Client(String ip, Integer port){
        this.ip = ip;
        this.port = port;
    }

    @SuppressLint("NewApi")
    public void Send() {
        BackgroundThreadExecutor nee = Needle.onBackgroundThread();
        Needle.onBackgroundThread().withThreadPoolSize(10).execute(new Runnable() {
            @Override
            public void run() {
                DatagramSocket ds = null;
                try {
                    ds = new DatagramSocket();
                    DatagramPacket dp;
                    InetAddress local = InetAddress.getByName(ip);
                    dp = new DatagramPacket(Message.getBytes(), Message.length(), local, port);
                    ds.setBroadcast(true);
                    ds.send(dp);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null) {
                        ds.close();
                    }
                }
            }
        });

    }
    public void Stop(){
//        async_cient.cancel(true);
    }
}