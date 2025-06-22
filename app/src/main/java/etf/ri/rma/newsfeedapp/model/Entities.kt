package etf.ri.rma.newsfeedapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Junction


@Entity(tableName = "News")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uuid: String,
    val title: String,
    val snippet: String,
    val imageUrl: String?,
    val category: String,
    val isFeatured: Boolean,
    val source: String,
    val publishedDate: String
)

/**
 * Room entitet za tag.
 */
@Entity(tableName = "Tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: String
)

/**
 * Predstavlja many-to-many vezu između vijesti i tagova.
 */
@Entity(
    tableName = "NewsTags",
    primaryKeys = ["newsId", "tagsId"]
)
data class NewsTagCrossRef(
    val newsId: Int,
    val tagsId: Int
)

/**
 * Pomoćna klasa koja dohvaća vijest zajedno s pripadajućim tagovima.
 */
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
)

/**
 * Konverter iz domenskog modela NewsItem u Room entitet.
 */
fun NewsItem.toEntity(): NewsEntity =
    NewsEntity(
        uuid = this.uuid,
        title = this.title,
        snippet = this.snippet.orEmpty(),
        imageUrl = this.imageUrl,
        category = this.category,
        isFeatured = this.isFeatured,
        source = this.source.orEmpty(),
        publishedDate = this.publishedDate.orEmpty()
    )

/**
 * Konverter iz Room relacije (NewsWithTags) natrag u domenski model.
 */
fun NewsWithTags.toNewsItem(): NewsItem =
    NewsItem(
        uuid = news.uuid,
        title = news.title,
        snippet = news.snippet,
        imageUrl = news.imageUrl,
        publishedDate = news.publishedDate,
        source = news.source,
        category = news.category,
        isFeatured = news.isFeatured,
        imageTags = tags.map { it.value }.toCollection(ArrayList())
    )
