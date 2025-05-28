package etf.ri.rma.newsfeedapp.model

import etf.ri.rma.newsfeedapp.data.ImaggaDAO
import etf.ri.rma.newsfeedapp.dto.NewsItemDto


data class NewsItem(
    val uuid: String,
    val title: String?,
    val snippet: String?,
    val imageUrl: String?,
    val publishedDate: String?,
    val source: String?,
    var category: String = "",
    var isFeatured: Boolean = false,
    var imageTags: ArrayList<String>? = null
)
suspend fun NewsItemDto.toNewsItem(category: String): NewsItem {
    val tags = try { ImaggaDAO.getTags(this.image_url.orEmpty()) } catch (e: Exception) { emptyList() }
    return NewsItem(
        uuid = this.uuid.orEmpty(),
        title = this.title.orEmpty(),
        snippet = this.description.orEmpty(),
        imageUrl = this.image_url.orEmpty(),
        publishedDate = this.published_at.orEmpty(),
        source = this.source.orEmpty(),
        category = category,
        isFeatured = true,
        imageTags = ArrayList(tags)
    )
}
