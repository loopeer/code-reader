package com.loopeer.codereaderkt.utils

import android.content.Context
import android.text.TextUtils
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.model.Repo
import java.io.File


object DownloadUrlParser {
    private val GITHUB_REPO_URL_BASE = "https://codeload.github.com/"
    private val ZIP_SUFFIX = ".zip"

    fun parseGithubUrlAndDownload(context: Context, url: String): Boolean {
        val downloadUrl = DownloadUrlParser.parseGithubDownloadUrl(url) ?: return false
        val repoName = DownloadUrlParser.getRepoName(url)
        val repo = Repo(repoName, FileCache().getInstance().getRepoAbsolutePath(repoName), downloadUrl, true, 0)
        Navigator().startDownloadNewRepoService(context, repo)
        return true
    }

    fun parseGithubDownloadUrl(url: String): String? {
        if (TextUtils.isEmpty(url)) return null
        val sb = StringBuilder()
        val strings = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (strings.size < 5) return null
        if (strings.size == 5) {
            sb.append(GITHUB_REPO_URL_BASE)
            sb.append(strings[3])
            sb.append("/")
            if (strings[4].contains("?")) {
                val lastName = strings[4].split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                sb.append(lastName[0])
                sb.append("/")
            } else {
                sb.append(strings[4])
                sb.append("/")
            }
            sb.append("zip/master")
            return sb.toString()
        }
        if (strings.size > 5) {
            sb.append(GITHUB_REPO_URL_BASE)
            sb.append(strings[3])
            sb.append("/")
            sb.append(strings[4])
            sb.append("/")
            sb.append("zip/master")
            return sb.toString()
        }
        return null
    }

    fun getRemoteRepoZipFileName(repoName: String): File =
            File(FileCache().getInstance().getCacheDir(), getRepoNameZip(repoName))

    private fun getRepoNameZip(name: String): String = name + ZIP_SUFFIX

    private fun getRepoName(url: String): String {
        val strings = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return strings[4].split("//.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
    }
}