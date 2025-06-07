package etf.ri.rma.newsfeedapp.model


data class NewsItem(
    val id: Int,
    val uuid: String,
    val title: String,
    val snippet: String?,
    val imageUrl: String?,
    val publishedDate: String?,
    val source: String?,
    val category: String,
    val isFeatured: Boolean,
    val imageTags: List<String>
) {
    companion object {
        fun fromEntity(nwt: NewsWithTags): NewsItem {
            val e = nwt.news
            return NewsItem(
                id            = e.id,
                uuid          = e.uuid,
                title         = e.title,
                snippet       = e.snippet,
                imageUrl      = e.imageUrl,
                publishedDate = e.publishedDate,
                source        = e.source,
                category      = e.category,
                isFeatured    = e.isFeatured,
                imageTags     = nwt.tags.map { it.value }
            )
        }
    }
}


