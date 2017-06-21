package com.aaphilip.tools.autowifilogin.util;

import android.util.Log;

import com.aaphilip.tools.autowifilogin.BuildConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {
    private File mLogFile;

    public Logger() {
        initialiseFileAndDir();
    }

    public void initialiseFileAndDir() {
        if(!BuildConfig.DEBUG_LOGS_ENABLED) {
            return;
        }
        mLogFile = new File(FileUtils.getAppDir().getAbsolutePath() + "/" +  Constants.LOG_FILE);
        if (mLogFile.exists() && mLogFile.length()/1024 > Constants.MAX_LOG_SIZE_KB) {
            if (!mLogFile.delete()) {
                Log.e("Logger", "Failed to delete large log file");
            }
        }
    }

    public void addToLog(Date date, String msg) {
        if(!BuildConfig.DEBUG_LOGS_ENABLED) {
            return;
        }
        try {
            FileWriter fileWriter = new FileWriter(mLogFile, true);
            PrintWriter pw = new PrintWriter(fileWriter);
            pw.println(new SimpleDateFormat("dd:MMM:yyyy:HH:mm:ss", Locale.ENGLISH).format(date)
                    + "," + msg);
            pw.close();
        } catch (IOException e) {
            Log.e("Logger", "Failed to write to log", e);
        }
    }
}
