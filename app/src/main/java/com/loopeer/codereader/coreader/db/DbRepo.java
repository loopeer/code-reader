package com.loopeer.codereader.coreader.db;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class DbRepo implements DbRepoModel {

    public static final Factory<DbRepo> FACTORY =
            new Factory<>((Creator<DbRepo>) (_id, name, last_modify, absolute_path, net_url, is_folder, download_id, factor)
                    -> new AutoValue_DbRepo(_id, name, last_modify, absolute_path, net_url, is_folder, download_id, factor));

    public static final RowMapper<DbRepo> FOR_TEAM_MAPPER = FACTORY.select_allMapper();
}
