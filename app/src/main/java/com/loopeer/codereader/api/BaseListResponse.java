package com.loopeer.codereader.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseListResponse<T> {

    @SerializedName("total_count")
    public int totalCount;
    @SerializedName("incomplete_results")
    public boolean incompleteResults;
    @SerializedName("items")
    public List<T> items;

}
