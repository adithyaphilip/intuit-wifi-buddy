package com.aaphilip.tools.autowifilogin.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileUtils {
    public static File getAppDir() {
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File appDir = new File(rootPath + "/" + Constants.APP_DIR);
        if (!appDir.exists() && !appDir.mkdir()) {
            Log.e("FileUtils", "Failed to make app dir");
        }
        return appDir;
    }
}
