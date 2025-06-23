package etf.ri.rma.newsfeedapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import etf.ri.rma.newsfeedapp.entites.NewsEntity
import etf.ri.rma.newsfeedapp.entites.NewsTagCrossRef
import etf.ri.rma.newsfeedapp.entites.TagEntity


@Database(
    entities = [
        NewsEntity::class,
        TagEntity::class,
        NewsTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun savedNewsDAO(): SavedNewsDAO

    companion object {
        @Volatile private var INSTANCE: NewsDatabase? = null

        fun getInstance(context: Context): NewsDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news-db"
                )

                    .build()
                    .also { INSTANCE = it }
            }
    }
}
