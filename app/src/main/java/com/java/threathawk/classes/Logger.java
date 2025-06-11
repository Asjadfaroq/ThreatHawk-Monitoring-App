package com.java.threathawk.classes;


import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Logger {

    public enum TYPES {
        EVENT,
        MAIN,
        ALL
    }


    public enum LogLevel{
        Debug("D"),
        Warning("W"),
        Info("I"),
        Error("E"),
        Fatal("F"),
        All("V");

        private String level;
        LogLevel(String level){
            this.level = level;
        }
        public String getLevel() {
            return this.level;
        }
        public static String getAllLevelsRegex(){
            return "( " + Debug.getLevel() + " | " + Warning.getLevel() + " | " + Info.getLevel() + " | "
                    + Error.getLevel() + " | " + Fatal.level + " )";
        }
    }
    public static String GetLevel(String s){
        String regex = LogLevel.getAllLevelsRegex();
        Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(s);
        if(matcher.find()){
            return  matcher.group(1);
        }
        return null;
    }
    private AsyncTask<Void, Void, Void> async_cient;
    ArrayList<String> buffer;
    Handler h = new Handler();
    Runnable r;
    public Integer log_capture_interval = 10000;
    File gpxfile = new File("/sdcard/test.txt");

    public void WriteToFile(BufferedReader log){
        String old;
        String line = "";
        try {
            FileWriter fileWritter = new FileWriter(gpxfile,true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            while ((line = log.readLine()) != null) {
                bufferWritter.append(line);
                bufferWritter.newLine();
            }
            System.out.println("write");
            bufferWritter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    public Logger(){
        buffer = new ArrayList<>();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  List<String> getLogs(Integer offset, Integer limit){
        offset = (offset > buffer.size()) ? buffer.size() : offset;
        limit = (limit > buffer.size()) ? buffer.size() : limit;
        List<String> temp = buffer.subList(offset,limit);
        return temp;
    }
    public void clearBuffer(Integer offset, Integer limit){
        offset = (offset > buffer.size()) ? buffer.size() : offset;
        limit = (limit > buffer.size()) ? buffer.size() : limit;
        List<String> temp = buffer.subList(offset,limit);
        buffer.removeAll(temp);
    }
    public void ClearLogcat() throws IOException {
       Runtime.getRuntime().exec("logcat -c");
    }
    public void Start(TYPES types,String Tag, LogLevel level) {
//        System.out.println(types + Tag + level);
        try {
            ClearLogcat();
        }catch (Exception ex){

        }
        async_cient = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                 r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Process process = Runtime.getRuntime().exec("logcat -d "+ types);
                            BufferedReader bufferedReader = new BufferedReader(
                                    new InputStreamReader(process.getInputStream()));
                            String line = "";
//                            WriteToFile(bufferedReader);
                            while ((line = bufferedReader.readLine()) != null) {
                                if(!LogLevel.All.equals(level)){
                                if(line.contains(level.getLevel())){
                                    String l = " " + level.getLevel() + " ";
                                    try {
                                        line = line.substring(0, line.indexOf(l)) + " " + Tag + " " + line.substring(line.indexOf(l));
                                        buffer.add(line);
                                    }catch (Exception e){

                                    }
                                }}else {
                                    String l = GetLevel(line);
                                    if (l != null) {
                                        line = line.substring(0, line.indexOf(l)) + " " + Tag + line.substring(line.indexOf(l));
                                        buffer.add(line);
                                    }
                                }
                            }

//                            ClearLogcat();
                            h.postDelayed(this,log_capture_interval);
                        } catch (IOException e) {
                            // Handle Exception
                            System.out.println(e.toString());
                        }
                    }
                };
                h.post(r);
                return null;
            }
        };
        if (Build.VERSION.SDK_INT >= 11)
            async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else async_cient.execute();
    }
    public void Stop(){
        async_cient.cancel(true);
        h.removeCallbacks(r);
    }
}
