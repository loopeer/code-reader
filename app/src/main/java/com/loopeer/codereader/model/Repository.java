package com.loopeer.codereader.model;

import com.google.gson.annotations.SerializedName;

public class Repository extends BaseModel {

    @SerializedName("name")
    public String name;
    @SerializedName("full_name")
    public String fullName;
    @SerializedName("owner")
    public Owner owner;
    @SerializedName("private")
    public boolean privateX;
    @SerializedName("html_url")
    public String htmlUrl;
    @SerializedName("description")
    public String description;
    @SerializedName("fork")
    public boolean fork;
    @SerializedName("url")
    public String url;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("updated_at")
    public String updatedAt;
    @SerializedName("pushed_at")
    public String pushedAt;
    @SerializedName("homepage")
    public String homepage;
    @SerializedName("size")
    public int size;
    @SerializedName("stargazers_count")
    public int stargazersCount;
    @SerializedName("watchers_count")
    public int watchersCount;
    @SerializedName("language")
    public String language;
    @SerializedName("forks_count")
    public int forksCount;
    @SerializedName("open_issues_count")
    public int openIssuesCount;
    @SerializedName("master_branch")
    public String masterBranch;
    @SerializedName("default_branch")
    public String defaultBranch;
    @SerializedName("score")
    public double score;

    public static class Owner {
        @SerializedName("login")
        public String login;
        @SerializedName("id")
        public int id;
        @SerializedName("avatar_url")
        public String avatarUrl;
        @SerializedName("gravatar_id")
        public String gravatarId;
        @SerializedName("url")
        public String url;
        @SerializedName("received_events_url")
        public String receivedEventsUrl;
        @SerializedName("type")
        public String type;
    }

}
