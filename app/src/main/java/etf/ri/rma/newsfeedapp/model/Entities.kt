package etf.ri.rma.newsfeedapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "News")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uuid: String,
    val title: String,
    val snippet: String?,
    val imageUrl: String?,
    val publishedDate: String?,
    val source: String?,
    val category: String,
    val isFeatured: Boolean = false
)

@Entity(tableName = "Tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val value: String
)
@Entity(tableName = "NewsTags", primaryKeys = ["newsId", "tagsId"])
data class NewsTagsCrossRef(
    val newsId: Int,
    val tagsId: Int
)