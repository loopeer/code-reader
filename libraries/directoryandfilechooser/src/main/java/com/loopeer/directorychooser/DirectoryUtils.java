package com.loopeer.directorychooser;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class DirectoryUtils {
    private static String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static FileNod getRootNode(Context context) {
        if (hasSDCard() && hasExternalStoragePermission(context)){
            return getFileDirectory(Environment.getExternalStorageDirectory());
        }
        return null;
    }

    public static FileNod getFileDirectory(File file) {
        if (file == null) return null;
        FileNod fileNod = new FileNod();
        fileNod.name = file.getName();
        fileNod.absolutePath = file.getAbsolutePath();
        if (file.isDirectory()) {
            fileNod.isFolder = true;
            fileNod.childNodes = new ArrayList<>();
            for (File childFile : file.listFiles()) {
                if (childFile.getName().startsWith(".") || childFile.getName().startsWith("_"))
                    continue;
                FileNod node = new FileNod();
                node.name = childFile.getName();
                if (childFile.isDirectory()) {
                    node.isFolder = true;
                }
                node.absolutePath = childFile.getAbsolutePath();
                fileNod.childNodes.add(node);
            }
            if (!fileNod.childNodes.isEmpty()) {
                Collections.sort(fileNod.childNodes, FileNod.NAME_COMPARATOR);
            }
        }
        return fileNod;
    }

    public static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
}