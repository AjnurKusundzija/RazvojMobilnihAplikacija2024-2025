package etf.ri.rma.newsfeedapp.data



import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import etf.ri.rma.newsfeedapp.model.NewsEntity
import etf.ri.rma.newsfeedapp.model.NewsTagCrossRef
import etf.ri.rma.newsfeedapp.model.TagEntity


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

        fun getDatabase(context: Context): NewsDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news-db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
