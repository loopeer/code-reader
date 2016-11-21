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

    public Repo(String name, String absolutePath, boolean isFolder) {
        this.name = name;
        this.absolutePath = absolutePath;
        this.isFolder = isFolder;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Repo repo = (Repo) o;

        if (id != null ? !id.equals(repo.id) : repo.id != null) return false;
        if (name != null ? !name.equals(repo.name) : repo.name != null) return false;
        if (absolutePath != null ? !absolutePath.equals(repo.absolutePath) : repo.absolutePath != null)
            return false;
        return netDownloadUrl != null ? netDownloadUrl.equals(repo.netDownloadUrl) : repo.netDownloadUrl == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (absolutePath != null ? absolutePath.hashCode() : 0);
        result = 31 * result + (netDownloadUrl != null ? netDownloadUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Repo{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                ", lastModify=" + lastModify +
                ", absolutePath='" + absolutePath + '\'' +
                ", netDownloadUrl='" + netDownloadUrl + '\'' +
                ", isFolder=" + isFolder +
                ", downloadId=" + downloadId +
                ", factor=" + factor +
                ", isUnzip=" + isUnzip +
                '}';
    }
}
