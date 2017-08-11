package com.loopeer.codereaderkt.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.loopeer.codereaderkt.model.Repo
import java.util.ArrayList


class CoReaderDbHelper private constructor(context: Context) : SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DbRepoModel.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val version = oldVersion

        if (version != DATABASE_VERSION) {
            db.execSQL("DROP TABLE IF EXISTS " + DbRepoModel.TABLE_NAME)
            onCreate(db)
        }
    }

    fun deleteAllTable() {
        val db = writableDatabase
        db.delete(DbRepoModel.TABLE_NAME, null, null)
        db.close()
    }

    fun insertRepo(repo: Repo): Long {
        val same = readSameRepo(repo)
        if (same != null) return java.lang.Long.valueOf(same!!.id)!!
        if (repo.lastModify == 0) repo.lastModify = System.currentTimeMillis()
        val db = writableDatabase
        return db.insert(DbRepoModel.TABLE_NAME, null, DbRepo.FACTORY.marshal()
                .name(repo.name)
                .absolute_path(repo.absolutePath)
                .last_modify(repo.lastModify)
                .net_url(repo.netDownloadUrl)
                .is_folder(repo.isFolder)
                .download_id(repo.downloadId)
                .factor(repo.factor)
                .is_unzip(repo.isUnzip)
                .asContentValues())
    }

    private fun haveSameRepo(repo: Repo): Boolean {
        val repo1 = readSameRepo(repo)
        if (repo1 != null) return true
        return false
    }

    fun readSameRepo(repo: Repo): Repo? {
        val db = readableDatabase
        var result: Repo? = null
        val cursor = db.rawQuery(
                DbRepo.CHECK_SAME_REPO,
                arrayOf<String>(repo.name, repo.absolutePath))
        if (cursor.moveToFirst()) {
            result = parseRepo(cursor)
        }
        return result
    }

    private fun parseRepo(cursor: Cursor): Repo? {
        val dbRepo = DbRepo.FOR_TEAM_MAPPER.map(cursor)
        val repo = Repo()
        repo.id = dbRepo._id().toString()
        repo.name = dbRepo.name()
        repo.absolutePath = dbRepo.absolute_path()
        repo.netDownloadUrl = dbRepo.net_url()
        repo.isFolder = dbRepo.is_folder()!!
        repo.lastModify = dbRepo.last_modify()!!
        repo.downloadId = dbRepo.download_id()!!
        repo.factor = dbRepo.factor()!!
        repo.isUnzip = dbRepo.is_unzip()!!
        return repo
    }

    fun readRepos(): List<Repo> {
        val db = readableDatabase
        val repos = ArrayList<Repo>()
        val cursor = db.rawQuery(DbRepo.SELECT_ALL, null)
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val repo = parseRepo(cursor)
                if (repo != null) {
                    repos.add(repo)
                }
            }
        }
        return repos
    }

    fun updateRepoLastModify(primaryKey: Long, lastModify: Long) {
        val db = writableDatabase
        db.execSQL(DbRepoModel.UPDATE_LAST_MODIFY, arrayOf(lastModify.toString(), primaryKey.toString()))
    }

    fun updateRepoDownloadId(downloadId: Long, repoId: String) {
        val db = writableDatabase
        db.execSQL(DbRepoModel.UPDATE_DOWNLOAD_ID, arrayOf(downloadId.toString(), repoId.toString()))
    }

    fun resetRepoDownloadId(downloadId: Long) {
        val db = writableDatabase
        db.execSQL(DbRepoModel.RESET_DOWNLOAD_ID, arrayOf(downloadId.toString()))
    }

    fun updateRepoDownloadProgress(downloadId: Long, factor: Float) {
        val db = writableDatabase
        db.execSQL(DbRepoModel.UPDATE_DOWNLOAD_PROGRESS, arrayOf(factor.toString(), downloadId.toString()))
    }

    fun updateRepoUnzipProgress(downloadId: Long, factor: Float, isUnzip: Boolean) {
        val db = writableDatabase
        db.execSQL(DbRepoModel.UPDATE_UNZIP_PROGRESS, arrayOf(factor.toString(), (if (isUnzip) 1 else 0).toString(), downloadId.toString()))
    }

    fun deleteRepo(id: Long) {
        val db = writableDatabase
        db.execSQL(DbRepoModel.DELETE_REPO, arrayOf(id.toString()))
    }

    companion object {
        private val TAG = "CoReaderDbHelper"

        private val DATABASE_NAME = "coreader.db"
        private val DATABASE_VERSION = 1

        @Volatile private var sInstance: CoReaderDbHelper? = null

        fun getInstance(context: Context): CoReaderDbHelper {
            var inst = sInstance
            if (inst == null) {
                synchronized(CoReaderDbHelper::class.java) {
                    inst = sInstance
                    if (inst == null) {
                        inst = CoReaderDbHelper(context)
                        sInstance = inst
                    }
                }
            }
            return inst
        }
    }
}