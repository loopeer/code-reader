package com.loopeer.codereaderkt.api


class ServiceUtil {
    private var sApiService: ApiService ?= null
    fun getApiService(): ApiService {
        if (sApiService == null) {
            sApiService = ApiService()
        }
        return sApiService as ApiService
    }
}