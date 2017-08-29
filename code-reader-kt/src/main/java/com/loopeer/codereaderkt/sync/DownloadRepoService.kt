package com.loopeer.codereaderkt.sync

import android.app.DownloadManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
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
import java.io.FileInputStream


class DownloadRepoService : Service() {

    companion object {
        val DOWNLOAD_COMPLETE = 0
        val DOWNLOAD_REPO = 1
        val DOWNLOAD_PROGRESS = 2
        val DOWNLOAD_REMOVE_DOWNLOAD = 3
    }


    private val TAG = "DownloadRepoService"

    private val DOWNLOAD_CONTENT_URI = Uri.parse("content://downloads/my_downloads")
    private val MEDIA_TYPE_ZIP = "application/zip"
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
        val inn = intent
        val type = inn.getIntExtra(Navigator.EXTRA_DOWNLOAD_SERVICE_TYPE, 0)
        val repo = inn.getSerializableExtra(Navigator.EXTRA_REPO) as Repo?
        val id: Long = inn.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
        when (type) {
            DOWNLOAD_COMPLETE -> {
                doRepoDownloadComplete(id, mDownloadingRepos[id]?.absolutePath)
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

    private fun doRepoDownloadComplete(id: Long, location: String?) {
        CoReaderDbHelper.getInstance(CodeReaderApplication.appContext)
            .updateRepoUnzipProgress(id, 1f, true)
        RxBus.instance?.send(DownloadProgressEvent(id, true))

        Observable.create(Observable.OnSubscribe<Void> { subscriber ->
            var cursor: Cursor? = null
            try {
                val manager = this@DownloadRepoService.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val baseQuery = DownloadManager.Query()
                    .setFilterById(id)
                cursor = manager.query(baseQuery)
                val statusColumnId = cursor!!.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                val localFilenameColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_FILENAME)
                val descName = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_DESCRIPTION)
                val fileUriId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)
                if (cursor.moveToNext()) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        val fileUri = cursor.getString(fileUriId)
                        val status = cursor.getLong(statusColumnId)
                        if(status == DownloadManager.STATUS_SUCCESSFUL.toLong()) {
                            unZipByUri(id, Uri.parse(fileUri), location)
                        }
                    } else {
                        val status = cursor.getLong(statusColumnId)
                        val path = cursor.getString(localFilenameColumnId)
                        val name = cursor.getString(descName)
                        if (status == DownloadManager.STATUS_SUCCESSFUL.toLong()) {
                            val zipFile = File(path)
                            val fileCache = FileCache().getInstance()
                            fileCache.deleteFilesByDirectory(File(fileCache.getCacheDir()!!.getPath() + File.separator + name))
                            val decomp = Unzip(FileInputStream(zipFile.path), fileCache.getCacheDir()!!.getPath() + File.separator + name, applicationContext)
                            decomp.DecompressZip()
                            if (zipFile.exists()) zipFile.delete()
                            CoReaderDbHelper.getInstance(CodeReaderApplication.appContext)
                                .updateRepoUnzipProgress(id, 1f, false)
                            CoReaderDbHelper.getInstance(
                                CodeReaderApplication.appContext).resetRepoDownloadId(mDownloadingRepos[id]!!.downloadId)
                            RxBus.instance?.send(DownloadProgressEvent(id, false))
                            RxBus.instance?.send(DownloadRepoMessageEvent(
                                getString(R.string.repo_download_complete, mDownloadingRepos[id]!!.name)))
                        }
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
            .doOnCompleted { this.checkTaskEmptyToFinish() }
            .subscribe()


    }

    private fun unZipByUri(id: Long, fileUri: Uri?, location: String?) {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(fileUri, "r")
        val fileDescriptor = parcelFileDescriptor.fileDescriptor
        val fileInputStream = FileInputStream(fileDescriptor)
        val zipFile = File(fileUri?.path)
        val fileCache = FileCache().getInstance()
        fileCache.deleteFilesByDirectory(File(location))
        val decomp = Unzip(fileInputStream, location, applicationContext)
        decomp.DecompressZip()
        if (zipFile.exists())
            zipFile.delete()
        CoReaderDbHelper.getInstance(CodeReaderApplication.appContext)
            .updateRepoUnzipProgress(id, 1f, false)
        CoReaderDbHelper.getInstance(
            CodeReaderApplication.appContext).resetRepoDownloadId(mDownloadingRepos[id]!!.downloadId)
        RxBus.instance?.send(DownloadProgressEvent(id, false))
        RxBus.instance?.send(DownloadRepoMessageEvent(
            getString(R.string.repo_download_complete, mDownloadingRepos[id]!!.name)))
    }


    private fun checkTaskEmptyToFinish() {
        if (mDownloadingRepos.isEmpty()) {
            stopSelf()
        }
    }

    private fun downloadFile(repo: Repo) {
        val dataFetcher = RemoteRepoFetchers(this, repo.netDownloadUrl, repo.name)
        val downloadId: Long = dataFetcher.download()
        if (downloadId <= 0) {
            CoReaderDbHelper.getInstance(getApplicationContext()).deleteRepo(repo.id!!.toLong());
            return;
        }
        repo.downloadId = downloadId
        mDownloadingRepos.put(downloadId, repo)
        CoReaderDbHelper.getInstance(applicationContext).updateRepoDownloadId(downloadId, repo.id)
        RxBus.instance?.send(DownloadRepoMessageEvent(getString(R.string.repo_download_start, repo.name)))
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
                    RxBus.instance?.send(DownloadRepoMessageEvent(
                            getString(R.string.repo_download_fail, repo.name)))
                    RxBus.instance?.send(DownloadFailDeleteEvent(repo))
                    CoReaderDbHelper.getInstance(context).deleteRepo(java.lang.Long.parseLong(repo.id))
                } else if (status != DownloadManager.STATUS_SUCCESSFUL) {
                    val dl_progress = 1f * bytes_downloaded / bytes_total
                    repo.factor = dl_progress
                } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    repo.factor = 1f
                }
                if (repo.factor < 0) repo.factor = 0f
                if (repo.factor >= 0) {
                    CoReaderDbHelper.getInstance(CodeReaderApplication.appContext)
                        .updateRepoDownloadProgress(repo.downloadId, repo.factor)
                    Log.d("DownloadrepoServiceLog", "" + repo.factor)
                    RxBus.instance?.send(DownloadProgressEvent(repo.id!!,
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