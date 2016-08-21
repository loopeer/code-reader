package com.loopeer.codereader.event;

import android.content.Context;

import com.loopeer.codereader.R;

public class DownloadRepoStartEvent {
    public int status;

    public DownloadRepoStartEvent(int status) {
        this.status = status;
    }

    public String getReason(Context context) {
        switch (status) {
            case 190:
                return context.getString(R.string.repo_download_start);
        }
        return null;
    }
}
