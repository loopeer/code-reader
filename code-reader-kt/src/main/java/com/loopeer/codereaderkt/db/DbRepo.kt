package com.loopeer.codereaderkt.db



abstract class DbRepo : DbRepoModel {
    companion object {

        val FACTORY = DbRepoModel.Factory({ _id, name, last_modify, absolute_path, net_url, is_folder, download_id, factor, is_unzip -> AutoValue_DbRepo(_id, name, last_modify, absolute_path, net_url, is_folder, download_id, factor, is_unzip) } as DbRepoModel.Creator<DbRepo>)

        val FOR_TEAM_MAPPER: RowMapper<DbRepo> = FACTORY.select_allMapper()
    }
}
