package com.loopeer.codereader.model;

import android.text.TextUtils;

import com.loopeer.directorychooser.FileNod;

public class Repo extends BaseModel{

    public String name;
    public long lastModify;
    public String absolutePath;
    public String netDownloadUrl;
    public boolean isFolder;
    public long downloadId;
    public float factor;
    public boolean isUnzip;

    public Repo() {
    }

    public Repo(String name, String absolutePath, String netDownloadUrl, boolean isFolder, long downloadId) {
        this.name = name;
        this.absolutePath = absolutePath;
        this.netDownloadUrl = netDownloadUrl;
        this.isFolder = isFolder;
        this.downloadId = downloadId;
    }

    public boolean isDownloading() {
        return downloadId > 0;
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

    public boolean isNetRepo() {
        return !TextUtils.isEmpty(netDownloadUrl);
    }

    public boolean isLocalRepo() {
        return !TextUtils.isEmpty(absolutePath);
    }
}
