package com.loopeer.codereader.api.service;

import com.loopeer.codereader.api.BaseListResponse;
import com.loopeer.codereader.model.Empty;
import com.loopeer.codereader.model.Repository;
import com.loopeer.codereader.model.Token;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
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

    // Api about token
    @POST("authorizations")
    Observable<Response<Token>> createToken(@Body Token token, @Header("Authorization") String authorization);

    @GET("authorizations")
    Observable<Response<List<Token>>> listToken(@Header("Authorization") String authorization);

    @DELETE("authorizations/{id}")
    Observable<Response<Empty>> removeToken(@Header("Authorization") String authorization, @Path("id") String id);

}

