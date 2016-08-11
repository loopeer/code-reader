package com.loopeer.codereader.coreader.db;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class DbRepo implements DbRepoModel {

    public static final Factory<DbRepo> FACTORY = new Factory<>(new Creator<DbRepo>() {

        @Override
        public DbRepo create(long _id, @Nullable String name, @Nullable Long last_modify
                , @Nullable String absolute_path, @Nullable String net_url
                , @Nullable Boolean is_folder, @Nullable Long download_id) {
            return new AutoValue_DbRepo(_id, name, last_modify, absolute_path, net_url
                    , is_folder, download_id);
        }

    });

    public static final RowMapper<DbRepo> FOR_TEAM_MAPPER = FACTORY.select_allMapper();
}
