package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.dto.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApiService {

    @GET("top")
    suspend fun getTopStoriesByCategory(
        @Query("categories") category: String,
        @Query("language") language: String,
        @Query("api_token") apiKey: String
    ): NewsResponse

    @GET("similar/{uuid}")
    suspend fun getSimilarStories(
        @Path("uuid") uuid: String,
        @Query("api_token") apiKey: String
    ): NewsResponse
}

