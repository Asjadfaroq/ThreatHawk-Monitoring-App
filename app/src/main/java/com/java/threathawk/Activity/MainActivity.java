package com.java.threathawk.Activity;

import static android.telephony.SmsManager.RESULT_CANCELLED;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.java.threathawk.R;
import com.java.threathawk.Services.LoggerService;
import com.java.threathawk.classes.Logger;
import com.java.threathawk.classes.RemoteLogger;
import com.java.threathawk.classes.Settings;
import com.java.threathawk.classes.UDP_Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class MainActivity extends AppCompatActivity {


    TextView ip,port;

    Button send,scan, register;
    Settings s = new Settings();
    AutoCompleteTextView type,level;
    Intent serviceIntent;
    String Tag = "Android-32";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String[] info = contents.split("&&", 0);
                if(info.length == 3){
                    ip.setText(info[0]);
                    port.setText(info[1]);
                    Tag = info[2];
                }
            }
            if(resultCode == RESULT_CANCELLED){
               Toast.makeText(this,"Nothing scanned",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If granted
                }
            }
           case -1:
               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
               alertDialogBuilder.setTitle("Change Permissions in Settings");
               alertDialogBuilder
                       .setMessage("Allow Manually for Notifications permission in settings. \n\n Select \n Permission -> Notifications -> Allow")
                       .setCancelable(false)
                       .setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                               Uri uri = Uri.fromParts("package", getPackageName(), null);
                               intent.setData(uri);
                               startActivityForResult(intent, 1000);
                           }
                       });

               AlertDialog alertDialog = alertDialogBuilder.create();
               alertDialog.show();
        }

    }

    public void AskForPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                1);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AskForPermission();
        scan = findViewById(R.id.scan);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

                    startActivityForResult(intent, 0);
                } catch (Exception e) {
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                    startActivity(marketIntent);
                }
            }
        });

        serviceIntent = new Intent(MainActivity.this, LoggerService.class);
        type = findViewById(R.id.type);
        level = findViewById(R.id.loglevel);
        ArrayAdapter<Logger.TYPES> adapter = new ArrayAdapter<Logger.TYPES>(MainActivity.this, android.R.layout.simple_spinner_item, Logger.TYPES.values());
        type.setAdapter(adapter);

        ArrayAdapter<Logger.LogLevel> adapter2 = new ArrayAdapter<Logger.LogLevel>(MainActivity.this, android.R.layout.simple_spinner_item, Logger.LogLevel.values());
        level.setAdapter(adapter2);


        send = findViewById(R.id.send);
        ip = findViewById(R.id.ip);
        port = findViewById(R.id.port);
        s = Settings.GetSettings(this);
        ip.setText(s.ip);
        port.setText(s.port.toString());
        type.setText(adapter.getItem(adapter.getPosition(s.type)).toString(),true);
        level.setText(adapter2.getItem(adapter2.getPosition(s.level)).toString(),true);
        Tag = s.tag;
        CheckServiceStatus();

        send.setText((s.isOn)? "Stop" : "Start");

        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start the second activity
                Intent intent = new Intent(MainActivity.this, sideDrawer.class);
                startActivity(intent);
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Tag == null){
                    Toast.makeText(MainActivity.this,"Agent is disconnected From the server. Rescan your agent",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!s.isOn){
                    s.isOn = true;
                    s.port = Integer.parseInt(port.getText().toString());
                    s.ip = ip.getText().toString();
                    s.type = Logger.TYPES.valueOf(type.getText().toString());
                    s.tag = Tag;
                    startLogger(s);
                    send.setText("Stop");
                }else{
                     stopService(serviceIntent);
                     send.setText("Start");
                     s.isOn = false;
                }
                CheckServiceStatus();
                Settings.SaveSetting(MainActivity.this,s);

            }
        });
        if(Tag == null){
            Toast.makeText(this,"Agent is disconnected From the server. Rescan your agent",Toast.LENGTH_LONG).show();
            return;
        }
        if(s.isOn) {
            startLogger(s);
        }
    }
    public void startLogger(Settings s){
        send.setText("Please Wait ...");
        send.setEnabled(false);
        serviceIntent.putExtra("ip",s.ip);
        serviceIntent.putExtra("port",s.port);
        serviceIntent.putExtra("type",s.type);
        serviceIntent.putExtra("type",s.type);
        serviceIntent.putExtra("tag",Tag);
        serviceIntent.putExtra("level", s.level);
        ContextCompat.startForegroundService(MainActivity.this, serviceIntent);
        send.setText("Stop");
        send.setEnabled(true);
    }
    public void CheckServiceStatus(){
        ip.setEnabled(!s.isOn);
        port.setEnabled(!s.isOn);
        type.setEnabled(!s.isOn);
        level.setEnabled(!s.isOn);

    }
    public void CheckTagExist(){

    }
}