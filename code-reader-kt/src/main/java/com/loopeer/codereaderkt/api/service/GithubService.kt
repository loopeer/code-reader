package com.loopeer.codereaderkt.api.service

import com.loopeer.codereaderkt.api.BaseListResponse
import com.loopeer.codereaderkt.model.Empty
import com.loopeer.codereaderkt.model.Repository
import com.loopeer.codereaderkt.model.Token
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import rx.Observable


interface GithubService {

    @Streaming
    @GET
    fun downloadRepo(@Url fileUrl: String): Observable<ResponseBody>

    @GET("/search/repositories")
    fun repositories(
            @Query("q") keyword: String,
            @Query("sort") sort: String,
            @Query("order") order: String,
            @Query("page") page: Int,
            @Query("per_page") pageSize: Int
    ): Observable<Response<BaseListResponse<Repository>>>

    // Api about token
    @POST("authorizations")
    fun createToken(@Body token: Token, @Header("Authorization") authorization: String): Observable<Response<Token>>

    @GET("authorizations")
    fun listToken(@Header("Authorization") authorization: String): Observable<Response<List<Token>>>

    @DELETE("authorizations/{id}")
    fun removeToken(@Header("Authorization") authorization: String, @Path("id") id: String): Observable<Response<Empty>>

}
