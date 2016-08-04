package com.loopeer.codereader.utils;

import android.content.Context;

import com.loopeer.codereader.model.DirectoryNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FileUtils {

    public static DirectoryNode getFileDirectoryNode(Context context) {
        File file = new File(StorageUtils.getExternalCacheDir(context), "CardStackView");
        if (file.listFiles().length == 0) return null;
        return getFileDirectory(file);
    }

    public static DirectoryNode getFileDirectory(File file) {
        if (file == null) return null;
        DirectoryNode directoryNode = new DirectoryNode();
        directoryNode.name = file.getName();
        if (file.isDirectory()) {
            directoryNode.isDirectory = true;
            directoryNode.pathNodes = new ArrayList<>();
            for (File childFile : file.listFiles()) {
                if (childFile.getName().startsWith(".") || childFile.getName().startsWith("_")) continue;
                DirectoryNode childNode = getFileDirectory(childFile);
                directoryNode.pathNodes.add(childNode);
            }
            if (!directoryNode.pathNodes.isEmpty()) {
                Collections.sort(directoryNode.pathNodes);
            }
        }
        return directoryNode;
    }
}
