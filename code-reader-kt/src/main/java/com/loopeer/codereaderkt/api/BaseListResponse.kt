package com.loopeer.codereaderkt.api

import com.google.gson.annotations.SerializedName


class BaseListResponse<T> {

    @SerializedName("total_count")
    var totalCount: Int = 0
    @SerializedName("incomplete_results")
    var incompleteResults: Boolean = false
    @SerializedName("items")
    var items: List<T>? = null

}