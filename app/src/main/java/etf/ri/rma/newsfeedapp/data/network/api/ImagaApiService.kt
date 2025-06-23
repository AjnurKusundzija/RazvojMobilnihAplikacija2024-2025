package etf.ri.rma.newsfeedapp.data.network.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import etf.ri.rma.newsfeedapp.dto.TagsResponse

interface ImagaApiService {
    @GET("v2/tags")
    @Headers(
        "Authorization: Basic YWNjX2IwOWU5ZmUxMGYwOGJiMjowYjg1OTQ1ZGMwMTQ3NjA5YWI5OThlNmE1MTRhMmIwYg==\n"//dodan novi prije zadnjeg commita
    )
    suspend fun getTags(
        @Query("image_url") imageUrl: String
    ): TagsResponse
}