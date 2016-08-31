package com.loopeer.codereader.api.service;

import com.loopeer.codereader.api.BaseListResponse;
import com.loopeer.codereader.model.Repository;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface GithubService {

    @Streaming
    @GET
    Observable<ResponseBody> downloadRepo(@Url String fileUrl);

    @GET("/search/repositories")
    Observable<Response<BaseListResponse<Repository>>> repositories(
            @Query("q") String keyword,
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("page") int page,
            @Query("per_page") int pageSize
    );

}

