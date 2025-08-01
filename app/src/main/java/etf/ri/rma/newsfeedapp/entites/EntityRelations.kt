package etf.ri.rma.newsfeedapp.entites

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlin.collections.forEach

data class NewsWithTags(
    @Embedded val news: NewsEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NewsTagCrossRef::class,
            parentColumn = "newsId",
            entityColumn = "tagsId"
        )
    )
    val tags: List<TagEntity>
) {
    fun toNewsItem(): NewsItem {
        val tagovi = ArrayList<String>()
        tags.forEach { tagovi.add(it.value) }

        return NewsItem(
            uuid = news.uuid,
            title = news.title,
            snippet = news.snippet,
            imageUrl = news.imageUrl,
            category = news.category,
            isFeatured = news.isFeatured,
            source = news.source,
            publishedDate = news.publishedDate,
            imageTags = ArrayList(tags)
        )
    }
}
