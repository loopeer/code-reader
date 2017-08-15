package com.loopeer.codereaderkt.api;

import com.loopeer.codereaderkt.api.service.GithubService;
import com.loopeer.codereaderkt.api.service.GithubServicej;

public class ServiceFactory {

    public static GithubService getGithubService() {
        return ServiceUtils.getApiService().getRetrofit().create(GithubService.class);
    }


}
