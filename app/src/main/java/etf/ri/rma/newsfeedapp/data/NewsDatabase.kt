package etf.ri.rma.newsfeedapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import etf.ri.rma.newsfeedapp.model.NewsEntity
import etf.ri.rma.newsfeedapp.model.NewsTagsCrossRef
import etf.ri.rma.newsfeedapp.model.TagEntity

@Database
(
    entities = [NewsEntity::class, TagEntity::class, NewsTagsCrossRef::class],
    version = 1,

)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun savedNewsDao(): SavedNewsDAO

    companion object {
        @Volatile private var INSTANCE: NewsDatabase? = null

        fun getInstance(context: Context): NewsDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news-db"
                ).build().also { INSTANCE = it }
            }
    }
}