package etf.ri.rma.newsfeedapp.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NewsWithTags(
    @Embedded val news: NewsEntity,

    @Relation(
        parentColumn  = "id",
        entityColumn  = "id",
        associateBy   = Junction(
            value         = NewsTagsCrossRef::class,
            parentColumn  = "newsId",
            entityColumn  = "tagsId"
        )
    )
    val tags: List<TagEntity>
)
