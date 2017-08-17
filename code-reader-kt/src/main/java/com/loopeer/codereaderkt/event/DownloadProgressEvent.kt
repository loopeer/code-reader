package com.loopeer.codereaderkt.event


class DownloadProgressEvent {

    public lateinit var repoId: String
    public var downloadId: Long = 0
    public var factor: Float = 0.0f
    public var isUnzip: Boolean = false

    constructor(downloadId: Long, isUnzip: Boolean) {
        this.downloadId = downloadId
        this.factor = 1f
        this.isUnzip = isUnzip
    }

    constructor(repoId: String, downloadId: Long, factor: Float, isUnzip: Boolean) {
        this.repoId = repoId
        this.downloadId = downloadId
        this.factor = factor
        this.isUnzip = isUnzip
    }


}
