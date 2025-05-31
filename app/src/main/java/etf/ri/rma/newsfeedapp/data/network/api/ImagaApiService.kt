package etf.ri.rma.newsfeedapp.data.network.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import etf.ri.rma.newsfeedapp.dto.TagsResponse

interface ImagaApiService {
    @GET("v2/tags")
    @Headers(
        "Authorization: Basic YWNjXzM0YWM1Njc4MDk5ZjM3Nzo3YTU1MWY4OWUxNDMwMTlmZjdmYjQ2NjEwNzA1N2U1MA=="
    )
    suspend fun getTags(
        @Query("image_url") imageUrl: String
    ): TagsResponse
}