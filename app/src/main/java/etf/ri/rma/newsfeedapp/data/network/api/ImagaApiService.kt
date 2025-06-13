package etf.ri.rma.newsfeedapp.data.network.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import etf.ri.rma.newsfeedapp.dto.TagsResponse

interface ImagaApiService {
    @GET("v2/tags")
    @Headers(
        "Authorization: Basic YWNjXzJmNWU0YTIzY2JiODdkNDo0Y2Q0MmMwMzkxY2E3YzMxZmU4MWUzM2MxNzU0OWZlNg=="
    )
    suspend fun getTags(
        @Query("image_url") imageUrl: String
    ): TagsResponse
}