package com.loopeer.codereaderkt.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.loopeer.codereaderkt.CodeReaderApplication;
import com.loopeer.codereaderkt.model.Repo;

import java.util.ArrayList;
import java.util.List;

public class CoReaderDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "CoReaderDbHelper";

    private static final String DATABASE_NAME = "coreader.db";
    private static final int DATABASE_VERSION = 1;

    private static volatile CoReaderDbHelper sInstance = null;

    private CoReaderDbHelper(Context context) {
        super(new CodeReaderApplication().getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);//这个位置总是出现空指针
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
        if (same != null) return Long.valueOf(same.getId());
        if (repo.getLastModify() == 0) repo.setLastModify(System.currentTimeMillis());
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(DbRepoModel.TABLE_NAME, null, DbRepo.FACTORY.marshal()
                .name(repo.getName())
                .absolute_path(repo.getAbsolutePath())
                .last_modify(repo.getLastModify())
                .net_url(repo.getNetDownloadUrl())
                .is_folder(repo.isFolder())
                .download_id(repo.getDownloadId())
                .factor(repo.getFactor())
                .is_unzip(repo.isUnzip())
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
                        repo.getName(),
                        repo.getAbsolutePath()
                });
        if (cursor.moveToFirst()) {
            result = parseRepo(cursor);
        }
        return result;
    }

    private Repo parseRepo(Cursor cursor) {
        DbRepo dbRepo = DbRepo.FOR_TEAM_MAPPER.map(cursor);
        Repo repo = new Repo();
        repo.setId(String.valueOf(dbRepo._id()));
        repo.setName(dbRepo.name());
        repo.setAbsolutePath(dbRepo.absolute_path());
        repo.setNetDownloadUrl(dbRepo.net_url());
        repo.setFolder(dbRepo.is_folder());
        repo.setLastModify(dbRepo.last_modify());
        repo.setDownloadId(dbRepo.download_id());
        repo.setFactor(dbRepo.factor());
        repo.setUnzip(dbRepo.is_unzip());
        return repo;
    }

    public List<Repo> readRepos() {
        SQLiteDatabase db = getReadableDatabase();//打开database引起的异常
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

    public void updateRepoDownloadId(long downloadId, String repoId) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DbRepoModel.UPDATE_DOWNLOAD_ID, new String[]{String.valueOf(downloadId), String.valueOf(repoId)});
    }

    public void resetRepoDownloadId(long downloadId) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DbRepoModel.RESET_DOWNLOAD_ID, new String[]{String.valueOf(downloadId)});
    }

    public void updateRepoDownloadProgress(long downloadId, float factor) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DbRepoModel.UPDATE_DOWNLOAD_PROGRESS
                , new String[]{String.valueOf(factor), String.valueOf(downloadId)});
    }

    public void updateRepoUnzipProgress(long downloadId, float factor, boolean isUnzip) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DbRepoModel.UPDATE_UNZIP_PROGRESS
                , new String[]{String.valueOf(factor), String.valueOf(isUnzip ? 1 : 0), String.valueOf(downloadId)});
    }

    public void deleteRepo(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DbRepoModel.DELETE_REPO
                , new String[]{String.valueOf(id)});
    }
}