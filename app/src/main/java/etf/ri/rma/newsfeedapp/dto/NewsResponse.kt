package etf.ri.rma.newsfeedapp.dto



data class NewsResponse(
    val meta: MetaData?,
    val data: List<NewsItemDto>?
)

data class MetaData(
    val found: Int?,
    val returned: Int?,
    val limit: Int?,
    val page: Int?
)
data class NewsItemDto(
    val uuid: String,
    val title: String,
    val description: String,
    val keywords: String,
    val snippet: String,
    val url: String,
    val image_url: String,
    val language: String,
    val published_at: String,
    val source: String,
    val categories: List<String>,
    val relevance_score: Double?, // može biti null!
    val locale: String
)