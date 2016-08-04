package com.loopeer.codereader.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class StorageUtils {

  private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
  private static final String TAG = "StorageUtils";

  private StorageUtils() {
  }

  /**
   * Returns application cache directory. Cache directory will be created on SD card
   * <i>("/Android/data/[app_package_name]/cache")</i> if card is mounted and app has appropriate permission. Else -
   * Android defines cache directory on device's file system.
   *
   * @param context Application context
   * @return Cache {@link File directory}
   */
  public static File getCacheDirectory(Context context) {
    File appCacheDir = null;
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
      appCacheDir = getExternalCacheDir(context);
    }
    if (appCacheDir == null) {
      appCacheDir = context.getCacheDir();
    }
    if (appCacheDir == null) {
      Log.w(TAG,"Can't define system cache directory! The app should be re-installed.");
    }
    return appCacheDir;
  }



  public static File getExternalCacheDir(Context context) {
    File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
    File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
    if (!appCacheDir.exists()) {
      if (!appCacheDir.mkdirs()) {
        Log.w(TAG,"Unable to create external cache directory");
        return null;
      }
      try {
        new File(appCacheDir, ".nomedia").createNewFile();
      } catch (IOException e) {
        Log.i(TAG,"Can't create \".nomedia\" file in application external cache directory");
      }
    }
    return appCacheDir;
  }


  public static boolean hasExternalStoragePermission(Context context) {
    int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
    return perm == PackageManager.PERMISSION_GRANTED;
  }

}