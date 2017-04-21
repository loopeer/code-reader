package com.loopeer.codereader.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.loopeer.codereader.CodeReaderApplication;
import com.loopeer.codereader.model.DirectoryNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class


FileCache {
    private static String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static FileCache instance;
    private static String cachePath = Environment.getExternalStorageDirectory() + "/CodeReader/repo/";
    private File cacheDir;
    public final String cacheDirPath = "/repo/";

    private FileCache() {
        if (hasSDCard() && hasExternalStoragePermission(CodeReaderApplication.getAppContext())) {
            cacheDir = createFilePath(cachePath);
        } else {
            cacheDir = createFilePath(CodeReaderApplication.getAppContext().getCacheDir() + cacheDirPath);
        }
    }

    private File createFilePath(String filePath) {
        return createFilePath(new File(filePath));
    }

    private File createFilePath(File file) {
        if (!file.exists()) {
            file.mkdirs();// 按照文件夹路径创建文件夹
        }
        return file;
    }

    public static FileCache getInstance() {
        if (null == instance)
            instance = new FileCache();
        return instance;
    }

    public boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public DirectoryNode getFileDirectoryNode() {
        File file = new File(cacheDir, "CardStackView");
        if (file.listFiles() == null || file.listFiles().length == 0) return null;
        return getFileDirectory(file);
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public String getRepoAbsolutePath(String repoName) {
        return getCacheDir().getPath() + File.separator + repoName;
    }

    public static DirectoryNode getFileDirectory(File file) {
        if (file == null) return null;
        DirectoryNode directoryNode = new DirectoryNode();
        directoryNode.name = file.getName();
        directoryNode.absolutePath = file.getAbsolutePath();
        if (file.isDirectory()) {
            directoryNode.isDirectory = true;
            directoryNode.pathNodes = new ArrayList<>();
            for (File childFile : file.listFiles()) {
                if (childFile.getName().startsWith(".")) continue;
                DirectoryNode childNode = getFileDirectory(childFile);
                directoryNode.pathNodes.add(childNode);
            }
            if (!directoryNode.pathNodes.isEmpty()) {
                Collections.sort(directoryNode.pathNodes);
            }
        }
        return directoryNode;
    }

    public static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    public static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.list() != null) {
            for (File item : directory.listFiles()) {
                if (item.isDirectory()) {
                    deleteFilesByDirectory(item);
                } else {
                    item.delete();
                }
            }
        }
    }
}
