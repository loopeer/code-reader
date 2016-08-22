package com.loopeer.codereader.event;

import com.loopeer.codereader.model.Repo;

public class DownloadFailDeleteEvent {

    public Repo deleteRepo;

    public DownloadFailDeleteEvent(Repo deleteRepo) {
        this.deleteRepo = deleteRepo;
    }
}

