package com.loopeer.codereader.model;

import android.text.TextUtils;

import com.loopeer.directorychooser.FileNod;

public class Repo extends BaseModel{

    public String name;
    public long lastModify;
    public String absolutePath;
    public String netUrl;
    public boolean isFolder;

    public boolean isLocal() {
        return !TextUtils.isEmpty(absolutePath);
    }

    public Repo() {
    }

    public static Repo parse(FileNod node) {
        Repo result = new Repo();
        result.name = node.name;
        result.absolutePath = node.absolutePath;
        result.isFolder = node.isFolder;
        return result;
    }

    public DirectoryNode toDirectoryNode() {
        DirectoryNode node = new DirectoryNode();
        node.name = name;
        node.absolutePath = absolutePath;
        node.isDirectory = isFolder;
        return node;
    }
}
