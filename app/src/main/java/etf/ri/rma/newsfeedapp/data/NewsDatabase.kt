package etf.ri.rma.newsfeedapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import etf.ri.rma.newsfeedapp.model.News
import etf.ri.rma.newsfeedapp.model.NewsEntity

import etf.ri.rma.newsfeedapp.model.NewsItem
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

    /**
     * Dodaje vijest u bazu ako ne postoji isti uuid.
     */
    suspend fun saveNews(news: NewsItem): Boolean =
        savedNewsDAO().saveNews(news)

    /**
     * Vraća sve vijesti iz baze, uključujući popunjene imageTags.
     */
    suspend fun allNews(): List<News> =
        savedNewsDAO().allNews()

    /**
     * Vraća vijesti filtrirane po kategoriji.
     */
    suspend fun getNewsWithCategory(category: String): List<News> =
        savedNewsDAO().getNewsWithCategory(category)

    /**
     * Dodaje tagove vijesti i vraća broj NOVO ubačenih tagova.
     */
    suspend fun addTags(tags: List<String>, newsId: Int): Int =
        savedNewsDAO().addTags(tags, newsId)

    /**
     * Vraća listu svih tagova za zadani newsId.
     */
    suspend fun getTags(newsId: Int): List<String> =
        savedNewsDAO().getTags(newsId)

    /**
     * Vraća vijesti koje sadrže bar jedan od proslijeđenih tagova,
     * sortirano od najnovije prema najstarijoj.
     * Koristi samo prve dvije vrijednosti iz liste, ako ih ima više.
     */
    suspend fun getSimilarNews(tags: List<String>): List<News> =
        savedNewsDAO().getSimilarNews(tags)

    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        /**
         * Singleton instanca baze.
         */
        fun getInstance(context: Context): NewsDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news-db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
