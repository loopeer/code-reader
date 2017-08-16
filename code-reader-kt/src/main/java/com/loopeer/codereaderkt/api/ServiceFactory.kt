package com.loopeer.codereaderkt.api

import com.loopeer.codereaderkt.api.service.GithubService


class ServiceFactory {

    @Synchronized fun getGithubService(): GithubService {
        return ServiceUtil().getApiService().retrofit.create(GithubService::class.java)
    }

    //这个注释符号代表java中的static相似
}