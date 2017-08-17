package com.loopeer.codereaderkt.sync

import android.app.DownloadManager
import android.app.Service
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import com.loopeer.codereaderkt.CodeReaderApplication
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.db.CoReaderDbHelper
import com.loopeer.codereaderkt.event.DownloadProgressEvent
import com.loopeer.codereaderkt.event.DownloadRepoMessageEvent
import com.loopeer.codereaderkt.model.Repo
import com.loopeer.codereaderkt.utils.RxBus
import rx.Subscription


class DownloadRepoService : Service() {

    companion object {
        open val DOWNLOAD_COMPLETE = 0
        open val DOWNLOAD_REPO = 1
        open val DOWNLOAD_PROGRESS = 2
        open val DOWNLOAD_REMOVE_DOWNLOAD = 3
    }


    private val TAG = "DownloadRepoService"

    val DOWNLOAD_CONTENT_URI = Uri.parse("content://downloads/my_downloads")//下载路径
    val MEDIA_TYPE_ZIP = "application/zip"
    private var mDownloadingRepos: HashMap<Long, Repo>? = null
    private var mProgressSubscription: Subscription? = null
    private var mDownloadChangeObserver: DownloadChangeObserver? = null


    override fun onCreate() {
        super.onCreate()
        mDownloadingRepos = HashMap()
        mDownloadChangeObserver = DownloadChangeObserver()
        contentResolver.registerContentObserver(DOWNLOAD_CONTENT_URI, true,
                mDownloadChangeObserver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        parseIntent(intent!!)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun parseIntent(intent: Intent) {
        var inn = intent //因为kotlin中in关键字的缘故，不能用‘in’做变量
        var type = inn.getIntExtra(Navigator.EXTRA_DOWNLOAD_SERVICE_TYPE, 0)//下载状态
        var repo: Repo = inn.getSerializableExtra(Navigator.EXTRA_REPO) as Repo
        var id: Long = inn.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
        when (type) {
            DOWNLOAD_COMPLETE -> {
                doRepoDownloadComplete(id)
            }
            DOWNLOAD_REPO -> {
                downloadFile(repo)
            }
            DOWNLOAD_PROGRESS -> {
                checkDownloadProgress()
            }
            DOWNLOAD_REMOVE_DOWNLOAD -> {
                removeDownloadingRepo(id)
            }
        }

    }

    private fun doRepoDownloadComplete(id: Long) {
        CoReaderDbHelper.getInstance(CodeReaderApplication().getAppContext())
                .updateRepoUnzipProgress(id, 1F, true)
        RxBus.getInstance().send(DownloadProgressEvent(id, true))
//                Observable.create((Observable.OnSubscribe<Void>))
/*        Observable.create({ subscriber ->
            var cursor: Cursor? = null
            try {
                val manager = this@DownloadRepoService.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val baseQuery = DownloadManager.Query()
                        .setFilterById(id)
                cursor = manager.query(baseQuery)
                val statusColumnId = cursor!!.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                val localFilenameColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_FILENAME)
                val descName = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_DESCRIPTION)
                if (cursor.moveToNext()) {
                    val status = cursor.getLong(statusColumnId)
                    val path = cursor.getString(localFilenameColumnId)
                    val name = cursor.getString(descName)
                    if (status == DownloadManager.STATUS_SUCCESSFUL.toLong()) {
                        val zipFile = File(path)
                        val fileCache = FileCache().getInstance()
                        fileCache.deleteFilesByDirectory(File(fileCache.getCacheDir()!!.getPath() + File.separator + name))
                        val decomp = Unzip(zipFile.path, fileCache.getCacheDir()!!.getPath() + File.separator + name, applicationContext)
                        decomp.DecompressZip()
                        if (zipFile.exists()) zipFile.delete()
                        CoReaderDbHelper.getInstance(CodeReaderApplication().getAppContext())
                                .updateRepoUnzipProgress(id, 1f, false)
                        CoReaderDbHelper.getInstance(
                                CodeReaderApplication().getAppContext()).resetRepoDownloadId(mDownloadingRepos!![id]!!.downloadId)
                        RxBus.getInstance().send(DownloadProgressEvent(id, false))
                        RxBus.getInstance().send(DownloadRepoMessageEvent(
                                getString(R.string.repo_download_complete, mDownloadingRepos!![id]!!.name)))
                    } else {
                    }
                }
                mDownloadingRepos!!.remove(id)
                subscriber.onCompleted()
            } catch (e: Exception) {
                subscriber.onError(e)
            } finally {
                cursor!!.close()
            }
        } as Observable.OnSubscribe<*>)
                .onErrorResumeNext(Observable.empty())
                .subscribeOn(Schedulers.io())
                .doOnError({})
                .doOnCompleted(this::checkTaskEmptyToFinish)
                .subscribe()*/;

    }

    private fun checkTaskEmptyToFinish() {
        if (mDownloadingRepos!!.isEmpty()) {
            stopSelf()
        }
    }

    fun downloadFile(repo: Repo) {
        var dataFetcher: RemoteRepoFetcher = RemoteRepoFetcher(this, repo.netDownloadUrl, repo.name)
        var downloadId: Long = dataFetcher.download()
        repo.downloadId = downloadId
        mDownloadingRepos!!.put(downloadId, repo)
        CoReaderDbHelper.getInstance(applicationContext).updateRepoDownloadId(downloadId, repo.id)
        RxBus.getInstance().send(DownloadRepoMessageEvent(getString(R.string.repo_download_start, repo.name)))
        checkDownloadProgress()
    }

    fun checkDownloadProgress() {
        if (mDownloadingRepos!!.isEmpty()) {
            val repos = CoReaderDbHelper.getInstance(this).readRepos()
        }
    }

    fun removeDownloadingRepo(id: Long) {

    }


    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    internal inner class DownloadChangeObserver : ContentObserver(Handler()) {

        override fun onChange(selfChange: Boolean) {
//       checkDownloadProgress()
        }
    }

}