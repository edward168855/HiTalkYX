package com.lpf.hitalkyx.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.lpf.hitalkyx.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sky90 on 2017/2/9.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = CrashHandler.class.getSimpleName();
    private static CrashHandler instance;
    private Context mContext;
    public CrashHandler.OnExceptionListener onExceptionListener;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Map<String, String> infos = new HashMap();
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public void setOnExceptionListener(CrashHandler.OnExceptionListener onExceptionListener) {
        this.onExceptionListener = onExceptionListener;
    }

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if(instance == null) {
            instance = new CrashHandler();
        }

        return instance;
    }

    public void init(Context context) {
        this.mContext = context;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        if(!this.handleException(ex) && this.mDefaultHandler != null) {
            this.mDefaultHandler.uncaughtException(thread, ex);
        } else if(this.onExceptionListener != null) {
            this.onExceptionListener.onException();
        } else {
            Process.killProcess(Process.myPid());
            System.exit(1);
        }

    }

    private boolean handleException(Throwable ex) {
        if(ex == null) {
            return false;
        } else {
            this.collectDeviceInfo(this.mContext);
            this.saveCrashInfo2File(ex);
            return true;
        }
    }

    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager fields = ctx.getPackageManager();
            PackageInfo field = fields.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if(field != null) {
                String versionName = field.versionName == null?"null":field.versionName;
                String versionCode = String.valueOf(field.versionCode);
                this.infos.put("versionName", versionName);
                this.infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException var9) {
            Log.e(TAG, "an error occured when collect package info", var9);
        }

        Field[] var10 = Build.class.getDeclaredFields();
        Field[] var6 = var10;
        int var13 = var10.length;

        for(int var12 = 0; var12 < var13; ++var12) {
            Field var11 = var6[var12];

            try {
                var11.setAccessible(true);
                this.infos.put(var11.getName(), var11.get((Object)null).toString());
                Log.d(TAG, var11.getName() + " : " + var11.get((Object)null));
            } catch (Exception var8) {
                Log.e(TAG, "an error occured when collect crash info", var8);
            }
        }

    }

    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        Iterator printWriter = this.infos.entrySet().iterator();

        String result;
        while(printWriter.hasNext()) {
            Map.Entry writer = (Map.Entry)printWriter.next();
            String cause = (String)writer.getKey();
            result = (String)writer.getValue();
            sb.append(cause + "=" + result + "\n");
        }

        StringWriter var15 = new StringWriter();
        PrintWriter var16 = new PrintWriter(var15);
        ex.printStackTrace(var16);

        for(Throwable var17 = ex.getCause(); var17 != null; var17 = var17.getCause()) {
            var17.printStackTrace(var16);
        }

        var16.close();
        result = var15.toString();
        sb.append(result);

        try {
            String e = this.formatter.format(new Date());
            String fileName = e + ".log";
            if(Environment.getExternalStorageState().equals("mounted")) {
                String path = this.mContext.getExternalFilesDir("crash") + File.separator;
                File dir = new File(path);
                if(!dir.isDirectory()) {
                    dir.mkdirs();
                }

                File[] allFiles = dir.listFiles();
                Arrays.sort(allFiles, new CrashHandler.FileComparator());

                for(int file = 0; file < allFiles.length - 9; ++file) {
                    FileUtil.delFile(allFiles[file], true);
                }

                File var18 = new File(path, fileName);
                var18.createNewFile();
                FileOutputStream fos = new FileOutputStream(var18);
                fos.write(sb.toString().getBytes());
                fos.close();
            }

            return fileName;
        } catch (Exception var14) {
            Log.e(TAG, "an error occured while writing file...", var14);
            return null;
        }
    }

    private String getFileNameWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.indexOf("."));
    }

    class FileComparator implements Comparator<File> {
        FileComparator() {
        }

        public int compare(File file1, File file2) {
            String createInfo1 = CrashHandler.this.getFileNameWithoutExtension(file1.getName());
            String createInfo2 = CrashHandler.this.getFileNameWithoutExtension(file2.getName());

            try {
                Date e = CrashHandler.this.formatter.parse(createInfo1);
                Date create2 = CrashHandler.this.formatter.parse(createInfo2);
                return e.before(create2)?-1:1;
            } catch (ParseException var7) {
                return 0;
            }
        }
    }

    public interface OnExceptionListener {
        void onException();
    }
}

