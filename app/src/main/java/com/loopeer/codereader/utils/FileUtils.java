package com.loopeer.codereader.utils;

import com.loopeer.codereader.model.DirectoryNode;

import java.io.File;
import java.util.ArrayList;

public class FileUtils {

    public static DirectoryNode getFileDirectory(File file) {
        if (file == null) return null;
        DirectoryNode directoryNode = new DirectoryNode();
        directoryNode.name = file.getName();
        if (file.isDirectory()) {
            directoryNode.pathNodes = new ArrayList<>();
            for (File childFile : file.listFiles()) {
                DirectoryNode childNode = getFileDirectory(childFile);
                directoryNode.pathNodes.add(childNode);
            }
        }
        return directoryNode;
    }
}
