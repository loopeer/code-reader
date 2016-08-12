package com.loopeer.codereader.api;

import com.loopeer.codereader.api.service.GithubService;

public class ServiceFactory {

    public static GithubService getGithubService() {
        return ServiceUtils.getApiService().getRetrofit().create(GithubService.class);
    }

}
