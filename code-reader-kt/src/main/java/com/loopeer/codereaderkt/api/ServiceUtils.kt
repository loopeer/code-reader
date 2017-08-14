package com.loopeer.codereaderkt.api


object ServiceUtils {
    private var sApiService: ApiService? = null
    val apiService: ApiService
        @Synchronized get() {
            if (sApiService == null) {
                sApiService = ApiService()
            }
            return sApiService!!
        }
}