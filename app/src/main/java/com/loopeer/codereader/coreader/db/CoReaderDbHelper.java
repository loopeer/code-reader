package com.loopeer.codereader.coreader.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.loopeer.codereader.model.Repo;

import java.util.ArrayList;
import java.util.List;

public class CoReaderDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "coreader.db";
    private static final int DATABASE_VERSION = 1;

    private static volatile CoReaderDbHelper sInstance = null;

    private CoReaderDbHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static CoReaderDbHelper getInstance(Context context) {
        CoReaderDbHelper inst = sInstance;
        if (inst == null) {
            synchronized (CoReaderDbHelper.class) {
                inst = sInstance;
                if (inst == null) {
                    inst = new CoReaderDbHelper(context);
                    sInstance = inst;
                }
            }
        }
        return inst;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbRepoModel.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version = oldVersion;

        if (version != DATABASE_VERSION) {
            db.execSQL("DROP TABLE IF EXISTS " + DbRepoModel.TABLE_NAME);
            onCreate(db);
        }
    }

    public void deleteAllTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DbRepoModel.TABLE_NAME, null, null);
        db.close();
    }

    public long insertRepo(Repo repo) {
        Repo same = readSameRepo(repo);
        if (same != null) return Long.valueOf(same.id);
        if (repo.lastModify == 0) repo.lastModify = System.currentTimeMillis();
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(DbRepoModel.TABLE_NAME, null, DbRepo.FACTORY.marshal()
                .name(repo.name)
                .absolute_path(repo.absolutePath)
                .last_modify(repo.lastModify)
                .net_url(repo.netUrl)
                .is_folder(repo.isFolder)
                .download_id(repo.downloadId)
                .factor(repo.factor)
                .asContentValues());
    }

    private boolean haveSameRepo(Repo repo) {
        Repo repo1 = readSameRepo(repo);
        if (repo1 != null) return true;
        return false;
    }

    public Repo readSameRepo(Repo repo) {
        SQLiteDatabase db = getReadableDatabase();
        Repo result = null;
        Cursor cursor = db.rawQuery(
                DbRepo.CHECK_SAME_REPO,
                new String[]{
                        repo.name,
                        repo.absolutePath
                });
        if (cursor.moveToFirst()) {
            result = parseRepo(cursor);
        }
        return result;
    }
    
    private Repo parseRepo(Cursor cursor) {
        DbRepo dbRepo = DbRepo.FOR_TEAM_MAPPER.map(cursor);
        Repo repo = new Repo();
        repo.id = String.valueOf(dbRepo._id());
        repo.name = dbRepo.name();
        repo.absolutePath = dbRepo.absolute_path();
        repo.netUrl = dbRepo.net_url();
        repo.isFolder = dbRepo.is_folder();
        repo.lastModify = dbRepo.last_modify();
        repo.downloadId = dbRepo.download_id();
        repo.factor = dbRepo.factor();
        return repo;
    }

    public List<Repo> readRepos() {
        SQLiteDatabase db = getReadableDatabase();
        List<Repo> repos = new ArrayList<>();
        Cursor cursor = db.rawQuery(DbRepo.SELECT_ALL, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Repo repo = parseRepo(cursor);
                if (repo != null) {
                    repos.add(repo);
                }
            }
        }
        return repos;
    }

    public void updateRepoLastModify(long primaryKey, long lastModify) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DbRepoModel.UPDATE_LAST_MODIFY, new String[]{String.valueOf(lastModify), String.valueOf(primaryKey)});
    }

    public void updateRepoDownloadId(long downloadId) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DbRepoModel.UPDATE_DOWNLOAD_ID, new String[]{String.valueOf(downloadId)});
    }

    public void updateRepoDownloadProgress(long downloadId, float factor) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DbRepoModel.UPDATE_DOWNLOAD_PROGRESS
                , new String[]{String.valueOf(factor), String.valueOf(downloadId)});
    }
}