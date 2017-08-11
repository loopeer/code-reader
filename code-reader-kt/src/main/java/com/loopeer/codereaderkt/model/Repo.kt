package com.loopeer.codereaderkt.model

import android.text.TextUtils
import com.loopeer.directorychooser.FileNod


class Repo() : BaseModel() {
    var name: String ?= null
    var lastModify: Long = 0
    var absolutePath: String? = null
    var netDownloadUrl: String? = null
    var isFolder: Boolean = false
    var downloadId: Long = 0
    var factor: Float = 0.toFloat()
    var isUnzip: Boolean = false
init {

}
    constructor(name: String, absolutePath: String, netDownloadUrl: String, isFolder: Boolean, downloadId: Long) : this() {
        this.name = name
        this.absolutePath = absolutePath
        this.netDownloadUrl = netDownloadUrl
        this.isFolder = isFolder
        this.downloadId = downloadId
    }

    fun isDownloading(): Boolean {
        return downloadId > 0
    }

    fun parse(node: FileNod): Repo {
        val result = Repo()
        result.name = node.name
        result.absolutePath = node.absolutePath
        result.isFolder = node.isFolder
        return result
    }

    fun toDirectoryNode(): DirectoryNode {
        val node = DirectoryNode()
        node.name = name!!
        node.absolutePath = absolutePath
        node.isDirectory = isFolder
        return node
    }

    fun isNetRepo(): Boolean {
        return !TextUtils.isEmpty(netDownloadUrl)
    }

    fun isLocalRepo(): Boolean {
        return !TextUtils.isEmpty(absolutePath)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val repo = o as Repo?

        if (if (id != null) id != repo!!.id else repo!!.id != null) return false
        if (if (name != null) name != repo.name else repo.name != null) return false
        if (if (absolutePath != null) absolutePath != repo.absolutePath else repo.absolutePath != null)
            return false
        return if (netDownloadUrl != null) netDownloadUrl == repo.netDownloadUrl else repo.netDownloadUrl == null

    }

    override fun hashCode(): Int {
        var result = if (name != null) name!!.hashCode() else 0
        result = 31 * result + if (id != null) id!!.hashCode() else 0
        result = 31 * result + if (absolutePath != null) absolutePath!!.hashCode() else 0
        result = 31 * result + if (netDownloadUrl != null) netDownloadUrl!!.hashCode() else 0
        return result
    }

    override fun toString(): String {
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
                '}'
    }
}