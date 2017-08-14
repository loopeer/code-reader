package com.loopeer.codereaderkt.model

import com.google.gson.annotations.SerializedName


class Repository : BaseModel() {

    @SerializedName("name")
    var name: String? = null
    @SerializedName("full_name")
    var fullName: String? = null
    @SerializedName("owner")
    var owner: Owner? = null
    @SerializedName("private")
    var privateX: Boolean = false
    @SerializedName("html_url")
    var htmlUrl: String? = null
    @SerializedName("description")
    var description: String? = null
    @SerializedName("fork")
    var fork: Boolean = false
    @SerializedName("url")
    var url: String? = null
    @SerializedName("created_at")
    var createdAt: String? = null
    @SerializedName("updated_at")
    var updatedAt: String? = null
    @SerializedName("pushed_at")
    var pushedAt: String? = null
    @SerializedName("homepage")
    var homepage: String? = null
    @SerializedName("size")
    var size: Int = 0
    @SerializedName("stargazers_count")
    var stargazersCount: Int = 0
    @SerializedName("watchers_count")
    var watchersCount: Int = 0
    @SerializedName("language")
    var language: String? = null
    @SerializedName("forks_count")
    var forksCount: Int = 0
    @SerializedName("open_issues_count")
    var openIssuesCount: Int = 0
    @SerializedName("master_branch")
    var masterBranch: String? = null
    @SerializedName("default_branch")
    var defaultBranch: String? = null
    @SerializedName("score")
    var score: Double = 0.toDouble()

    class Owner {
        @SerializedName("login")
        var login: String? = null
        @SerializedName("id")
        var id: Int = 0
        @SerializedName("avatar_url")
        var avatarUrl: String? = null
        @SerializedName("gravatar_id")
        var gravatarId: String? = null
        @SerializedName("url")
        var url: String? = null
        @SerializedName("received_events_url")
        var receivedEventsUrl: String? = null
        @SerializedName("type")
        var type: String? = null
    }

}