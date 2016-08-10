package com.loopeer.codereader.api.service;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface GithubService {

    @Streaming
    @GET
    Observable<ResponseBody> downloadRepo(@Url String fileUrl);

}

