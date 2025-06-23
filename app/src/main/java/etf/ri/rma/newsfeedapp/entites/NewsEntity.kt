package etf.ri.rma.newsfeedapp.entites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "News")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uuid: String,
    val title: String,
    val snippet: String?,
    val imageUrl: String?,
    val category: String,
    val isFeatured: Boolean,
    val source: String?,
    val publishedDate: String?
)

