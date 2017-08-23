package com.loopeer.codereaderkt.sync

import android.app.DownloadManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.loopeer.codereaderkt.CodeReaderApplication
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.db.CoReaderDbHelper
import com.loopeer.codereaderkt.event.DownloadFailDeleteEvent
import com.loopeer.codereaderkt.event.DownloadProgressEvent
import com.loopeer.codereaderkt.event.DownloadRepoMessageEvent
import com.loopeer.codereaderkt.model.Repo
import com.loopeer.codereaderkt.utils.FileCache
import com.loopeer.codereaderkt.utils.RxBus
import com.loopeer.codereaderkt.utils.Unzip
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File


class DownloadRepoService : Service() {

    companion object {
        val DOWNLOAD_COMPLETE = 0
        val DOWNLOAD_REPO = 1
        val DOWNLOAD_PROGRESS = 2
        val DOWNLOAD_REMOVE_DOWNLOAD = 3
    }


    private val TAG = "DownloadRepoService"

    private val DOWNLOAD_CONTENT_URI = Uri.parse("content://downloads/my_downloads")//下载路径
    val MEDIA_TYPE_ZIP = "application/zip"
    private lateinit var mDownloadingRepos: HashMap<Long, Repo>
    private var mProgressSubscription: Subscription? = null
    private lateinit var mDownloadChangeObserver: DownloadChangeObserver


    override fun onCreate() {
        super.onCreate()
        mDownloadingRepos = HashMap()
        mDownloadChangeObserver = DownloadChangeObserver()
        contentResolver.registerContentObserver(DOWNLOAD_CONTENT_URI, true,
                mDownloadChangeObserver)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        parseIntent(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun parseIntent(intent: Intent) {
        val inn = intent //因为kotlin中in关键字的缘故，不能用‘in’做变量
        val type = inn.getIntExtra(Navigator.EXTRA_DOWNLOAD_SERVICE_TYPE, 0)//下载状态
        Log.d("DownloadRepoServiceLog ", " type: " + type)
        val repo = inn.getSerializableExtra(Navigator.EXTRA_REPO) as Repo?//这里会崩溃
        val id: Long = inn.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
        when (type) {
            DOWNLOAD_COMPLETE -> {
                doRepoDownloadComplete(id)
            }
            DOWNLOAD_REPO -> {
                downloadFile(repo!!)
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
        CoReaderDbHelper.getInstance(CodeReaderApplication.getAppContext())
                .updateRepoUnzipProgress(id, 1f, true)
        RxBus.getInstance().send(DownloadProgressEvent(id, true))
        //下载链接错误时会崩溃，原版本就有的问题

        Log.d("DownloadRepoServiceCompleteLog ", " id: "+id)
       Observable.create(Observable.OnSubscribe<Void>{ subscriber ->
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
                        CoReaderDbHelper.getInstance(CodeReaderApplication.getAppContext())
                                .updateRepoUnzipProgress(id, 1f, false)
                        CoReaderDbHelper.getInstance(
                                CodeReaderApplication.getAppContext()).resetRepoDownloadId(mDownloadingRepos[id]!!.downloadId)
                        RxBus.getInstance().send(DownloadProgressEvent(id, false))
                        RxBus.getInstance().send(DownloadRepoMessageEvent(
                                getString(R.string.repo_download_complete, mDownloadingRepos[id]!!.name)))
                    } else {
                    }
                }
                mDownloadingRepos.remove(id)
                subscriber.onCompleted()
            } catch (e: Exception) {
                subscriber.onError(e)
            } finally {
                cursor!!.close()
            }
        })
                .onErrorResumeNext(Observable.empty())
                .subscribeOn(Schedulers.io())
                .doOnError { e -> Log.d(TAG, e.toString()) }
                .doOnCompleted { this.checkTaskEmptyToFinish() }
                .subscribe()



    }

    private fun checkTaskEmptyToFinish() {
        if (mDownloadingRepos.isEmpty()) {
            stopSelf()
        }
    }

    private fun downloadFile(repo: Repo) {
        val dataFetcher: RemoteRepoFetcher = RemoteRepoFetcher(this, repo.netDownloadUrl, repo.name)
        val downloadId: Long = dataFetcher.download()
        repo.downloadId = downloadId
        mDownloadingRepos.put(downloadId, repo)
        CoReaderDbHelper.getInstance(applicationContext).updateRepoDownloadId(downloadId, repo.id)
        RxBus.getInstance().send(DownloadRepoMessageEvent(getString(R.string.repo_download_start, repo.name)))
        checkDownloadProgress()
    }

    private fun checkDownloadProgress() {
        if (mDownloadingRepos.isEmpty()) {
            val repos = CoReaderDbHelper.getInstance(this).readRepos()
            for (repo in repos) {
                if (repo.isDownloading()) {
                    mDownloadingRepos.put(repo.downloadId, repo)
                }
            }
        }
        if (mDownloadingRepos.isEmpty()) {
            stopSelf()
            return
        }
        if (mProgressSubscription != null && !mProgressSubscription?.isUnsubscribed!!) {
            mProgressSubscription?.unsubscribe()
        }
        mProgressSubscription = checkDownloadingProgress(this)
    }

    private fun clearDownloadProgressSubscription() {
        if (mProgressSubscription != null && !mProgressSubscription?.isUnsubscribed!!) {
            mProgressSubscription?.unsubscribe()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearDownloadProgressSubscription()
        contentResolver.unregisterContentObserver(mDownloadChangeObserver)
    }

    private fun checkDownloadingProgress(context: Context): Subscription {
        return Observable.create(Observable.OnSubscribe<List<Repo>> { subscriber ->
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val repos = ArrayList(mDownloadingRepos.values)
            for (repo in repos) {
                val q = DownloadManager.Query()
                q.setFilterById(repo.downloadId)

                val cursor = downloadManager.query(q)
                cursor.moveToFirst()
                val bytes_downloaded = cursor.getInt(cursor
                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytes_total = cursor.getInt(
                        cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                val mediaType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))
                val reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_FAILED
                        && MEDIA_TYPE_ZIP != mediaType
                        && reason == DownloadManager.ERROR_UNKNOWN) {
                    RxBus.getInstance()
                            .send(DownloadRepoMessageEvent(
                                    getString(R.string.repo_download_fail, repo.name)))
                    RxBus.getInstance()
                            .send(DownloadFailDeleteEvent(repo))
                    CoReaderDbHelper.getInstance(context).deleteRepo(java.lang.Long.parseLong(repo.id))
                } else if (status != DownloadManager.STATUS_SUCCESSFUL) {
                    val dl_progress = 1f * bytes_downloaded / bytes_total
                    repo.factor = dl_progress
                } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    repo.factor = 1f
                }
                if (repo.factor < 0) repo.factor = 0f
                if (repo.factor >= 0) {
                    CoReaderDbHelper.getInstance(CodeReaderApplication.getAppContext())
                            .updateRepoDownloadProgress(repo.downloadId, repo.factor)
                    RxBus.getInstance().send(DownloadProgressEvent(repo.id!!,
                            repo.downloadId, repo.factor, repo.isUnzip))
                }
                cursor.close()
            }
            subscriber.onCompleted()
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun removeDownloadingRepo(id: Long) {
        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val i = downloadManager.remove(id)
        if (i > 0) mDownloadingRepos.remove(id)
    }

    override fun onBind(p0: Intent?): IBinder? = null


    internal inner class DownloadChangeObserver : ContentObserver(Handler()) {

        override fun onChange(selfChange: Boolean) {
            checkDownloadProgress()
        }

    }
}