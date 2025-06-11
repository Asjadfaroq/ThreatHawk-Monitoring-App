package com.java.threathawk.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

public class Settings {

    public String ip;
    public Integer port;
    public Logger.TYPES type;
    public Boolean isOn;
    public String tag;
    public Logger.LogLevel level;

    public static void SaveSetting(Context ctx, Settings settings){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ip",settings.ip);
        editor.putInt("port",settings.port);
        editor.putString("type",settings.type.toString());
        editor.putString("level",settings.level.toString());
        editor.putBoolean("isOn",settings.isOn);
        editor.putString("tag",settings.tag);
        editor.apply();
        Toast.makeText(ctx, "Settings saved", Toast.LENGTH_SHORT).show();
    }
    public static Settings GetSettings(Context ctx){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Settings s = new Settings();
        s.ip = sharedPreferences.getString("ip", "");
        s.port = sharedPreferences.getInt("port",514);
        s.type = Logger.TYPES.valueOf(sharedPreferences.getString("type","ALL"));
        s.level = Logger.LogLevel.valueOf(sharedPreferences.getString("level", "All"));
        s.isOn = sharedPreferences.getBoolean("isOn",false);
        s.tag = sharedPreferences.getString("tag",null);
        return s;
    }

    public static void ClearStorage(Context ctx){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear();
    }

}
