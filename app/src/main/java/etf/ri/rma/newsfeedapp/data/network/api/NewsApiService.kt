package etf.ri.rma.newsfeedapp.data.network.api

import etf.ri.rma.newsfeedapp.dto.NewsResponse

import retrofit2.http.GET

import retrofit2.http.Query

interface NewsApiService {

        @GET("top")
        suspend fun getTopStoriesByCategory(
            @Query("categories") category: String? = null,
            @Query("language") language: String,
            @Query("api_token") apiKey: String
        ): NewsResponse



    @GET("news/similar")
    suspend fun getSimilarStories(
        @Query("uuid") uuid: String,
        @Query("api_token") apiToken: String
    ): NewsResponse





    @GET("all")
    suspend fun getHeadlinesBySourceAndCategory(
        @Query("domains") sourceId: String,
        @Query("categories") category: String,
        @Query("language") language: String,
        @Query("api_token") apiKey: String
    ): NewsResponse
}

