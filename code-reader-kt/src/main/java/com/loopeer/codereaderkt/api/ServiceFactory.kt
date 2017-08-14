package com.loopeer.codereaderkt.api

import com.loopeer.codereaderkt.api.service.GithubService


object ServiceFactory {

    val githubService: GithubService
        get() = ServiceUtils.apiService.retrofit.create(GithubService::class.java)


}