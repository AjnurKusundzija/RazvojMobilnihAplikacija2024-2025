package etf.ri.rma.newsfeedapp.entites

import androidx.room.Entity

@Entity(
    tableName = "NewsTags",
    primaryKeys = ["newsId", "tagsId"]
)
data class NewsTagCrossRef(
    val newsId: Int,
    val tagsId: Int
)