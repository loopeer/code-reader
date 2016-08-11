package com.loopeer.codereader;

public class DownloadProgressEvent {

    public long downloadId;
    public float factor;

    public DownloadProgressEvent() {
    }

    public DownloadProgressEvent(long downloadId, float factor) {
        this.downloadId = downloadId;
        this.factor = factor;
    }
}
