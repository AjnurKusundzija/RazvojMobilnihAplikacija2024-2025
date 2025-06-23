package etf.ri.rma.newsfeedapp.entites

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Tags",
    indices = [Index(value = ["value"], unique = true)]
)

data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: String
)