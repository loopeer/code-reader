package com.loopeer.codereader;

public class DownloadProgressEvent {

    public long downloadId;
    public float factor;
    public boolean isUnzip;

    public DownloadProgressEvent() {
    }

    public DownloadProgressEvent(long downloadId, float factor) {
        this.downloadId = downloadId;
        this.factor = factor;
    }

    public DownloadProgressEvent(long downloadId, boolean isUnzip) {
        this.downloadId = downloadId;
        this.factor = 1.f;
        this.isUnzip = isUnzip;
    }

    public DownloadProgressEvent(long downloadId, float factor, boolean isUnzip) {
        this.downloadId = downloadId;
        this.factor = factor;
        this.isUnzip = isUnzip;
    }
}
