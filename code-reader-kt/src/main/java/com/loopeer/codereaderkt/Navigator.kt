package com.loopeer.codereaderkt

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.widget.Toast
import com.loopeer.codereaderkt.db.CoReaderDbHelper
import com.loopeer.codereaderkt.model.Repo
import com.loopeer.codereaderkt.sync.DownloadRepoService
import com.loopeer.codereaderkt.ui.activity.*


open class Navigator {
    companion object {
        //静态变量这么写！！！
        val EXTRA_REPO = "extra_repo"
        val EXTRA_ID = "extra_id"
        val EXTRA_DOWNLOAD_SERVICE_TYPE = "extra_download_service_type"
        val EXTRA_DIRETORY_ROOT = "extra_diretory_root"
        val EXTRA_DIRETORY_ROOT_NODE_INSTANCE = "extra_diretory_root_node_instance"
        val EXTRA_DIRETORY_SELECTING = "extra_diretory_selecting"
        val EXTRA_WEB_URL = "extra_web_url"
        val EXTRA_HTML_STRING = "extra_html_string"
    }


    public fun startMainActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(intent)
    }

    public fun startLoginActivity(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
    }

    public fun startSettingActivity(context: Context) {
        val intent = Intent(context, SettingActivity::class.java)
        context.startActivity(intent)
    }

    public fun startAddRepoActivity(context: Context) {
        val intent = Intent(context, AddRepoActivity::class.java)
        context.startActivity(intent)
    }

    public fun startAboutActivity(context: Context) {
        val intent = Intent(context, AboutActivity::class.java)
        context.startActivity(intent)
    }

    public fun startWebActivity(context: Context, url: String) {
        val intent = Intent(context, SimpleWebActivity::class.java)
        intent.putExtra(EXTRA_WEB_URL, url)
        context.startActivity(intent)
    }

    fun startComposeEmail(context: Context, addresses: Array<String>, subject: String, content: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(content))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, R.string.about_email_app_not_have, Toast.LENGTH_SHORT).show()
        }
    }

    fun startDownloadRepoService(context: Context, repo: Repo) {
        val intent = Intent(context, DownloadRepoService::class.java)
        intent.putExtra(EXTRA_REPO, repo)
        intent.putExtra(EXTRA_DOWNLOAD_SERVICE_TYPE, DownloadRepoService.DOWNLOAD_REPO)
        context.startService(intent)
    }

    fun startDownloadRepoService(context: Context, type: Int) {
        val intent = Intent(context, DownloadRepoService::class.java)
        intent.putExtra(EXTRA_DOWNLOAD_SERVICE_TYPE, type)
        context.startService(intent)
    }

    fun startDownloadRepoServiceRemove(context: Context, downloadId: Long) {
        val intent = Intent(context, DownloadRepoService::class.java)
        intent.putExtra(EXTRA_DOWNLOAD_SERVICE_TYPE, DownloadRepoService.DOWNLOAD_REMOVE_DOWNLOAD)
        intent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, downloadId)
        context.startService(intent)
    }

    fun startDownloadNewRepoService(context: Context, repo: Repo) {
        val sameRepo = CoReaderDbHelper.getInstance(context).readSameRepo(repo)
        val repoId: Long
        if (sameRepo != null) {
            repoId = java.lang.Long.parseLong(sameRepo.id)
        } else {
            repoId = CoReaderDbHelper.getInstance(context).insertRepo(repo)
        }
        repo.id = repoId.toString()
        Navigator().startDownloadRepoService(context, repo)
    }

}