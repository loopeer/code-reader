package com.loopeer.codereader.event;

public class DownloadRepoStartEvent {
    public String reason;

    public DownloadRepoStartEvent(String reason) {
        this.reason = reason;
    }
}
