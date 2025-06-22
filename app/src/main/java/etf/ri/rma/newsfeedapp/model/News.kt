package etf.ri.rma.newsfeedapp.model

data class News(
    val id: Int = 0,
    val uuid: String,
    val title: String,
    val snippet: String,
    val imageUrl: String?,
    val category: String,
    val isFeatured: Boolean,
    val source: String,
    val publishedDate: String
) {
    var tags: List<Tag> = emptyList()
}

data class Tag(
    val id: Int = 0,
    val value: String
)
fun News.toEntity(): NewsEntity =
    NewsEntity(
        uuid = this.uuid,
        title = this.title,
        snippet = this.snippet.orEmpty(),
        imageUrl = this.imageUrl,
        category = this.category,
        isFeatured = this.isFeatured,
        source = this.source,
        publishedDate = this.publishedDate
    )

/**
 * Mapira Room relaciju NewsWithTags u domenski model News.
 */
fun NewsWithTags.toNews(): News =
    News(
        uuid = news.uuid,
        title = news.title,
        snippet = news.snippet.ifEmpty { null }.toString(),
        category = news.category,
        imageUrl = news.imageUrl.orEmpty(),
        isFeatured = news.isFeatured,
        publishedDate = news.publishedDate,
        source = news.source,

    )
