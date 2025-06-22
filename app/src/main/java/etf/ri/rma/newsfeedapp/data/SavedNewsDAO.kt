package etf.ri.rma.newsfeedapp.data


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import etf.ri.rma.newsfeedapp.model.News
import etf.ri.rma.newsfeedapp.model.NewsEntity
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.NewsTagCrossRef
import etf.ri.rma.newsfeedapp.model.NewsWithTags
import etf.ri.rma.newsfeedapp.model.TagEntity
import etf.ri.rma.newsfeedapp.model.toEntity
import etf.ri.rma.newsfeedapp.model.toNews


@Dao
interface SavedNewsDAO {

    // --- POMOĆNE ---
    @Query("SELECT * FROM News WHERE uuid = :uuid LIMIT 1")
    suspend fun findNewsByUuid(uuid: String): NewsEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewsEntity(news: NewsEntity): Long

    @Query("SELECT * FROM Tags")
    suspend fun getAllTags(): List<TagEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Query("SELECT id FROM Tags WHERE value = :value LIMIT 1")
    suspend fun findTagIdByValue(value: String): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(ref: NewsTagCrossRef)

    @Transaction
    @Query("SELECT * FROM News")
    suspend fun loadAllNewsWithTags(): List<NewsWithTags>

    @Transaction
    @Query("SELECT * FROM News WHERE category = :category")
    suspend fun loadNewsWithTagsByCategory(category: String): List<NewsWithTags>

    @Transaction
    @Query("""
      SELECT * FROM News
      WHERE id IN (
        SELECT DISTINCT newsId
          FROM NewsTags
          JOIN Tags ON Tags.id = NewsTags.tagsId
          WHERE Tags.value IN (:tags)
      )
      ORDER BY publishedDate DESC
    """)
    suspend fun loadSimilarNewsWithTags(tags: List<String>): List<NewsWithTags>

    // --- DOMENSKE METODE ---

    /** Dodaje ako nema isti uuid */
    @Transaction
    suspend fun saveNews(news: NewsItem): Boolean {
        if (findNewsByUuid(news.uuid) != null) return false
        return insertNewsEntity(news. toEntity()) > 0L
    }

    /** Vraća News sa popunjenim .tags */
    @Transaction
    suspend fun allNews(): List<News> =
        loadAllNewsWithTags().map { it.toNews() }

    /** Vijesti po kategoriji */
    @Transaction
    suspend fun getNewsWithCategory(category: String): List<News> =
        loadNewsWithTagsByCategory(category).map { it.toNews() }

    /**
     * Dodaje tagove vijesti; vraća broj NOVO ubačenih tagova u tabelu Tags.
     */
    @Transaction
    suspend fun addTags(tags: List<String>, newsId: Int): Int {
        var count = 0
        for (v in tags) {
            val inserted = insertTag(TagEntity(value = v))
            if (inserted > 0) {
                count++
                insertCrossRef(NewsTagCrossRef(newsId, inserted.toInt()))
            } else {
                findTagIdByValue(v)?.let { insertCrossRef(NewsTagCrossRef(newsId, it)) }
            }
        }
        return count
    }

    /** Samo stringovi tagova */
    @Transaction
    suspend fun getTags(newsId: Int): List<String> =
        loadAllNewsWithTags()
            .firstOrNull { it.news.id == newsId }
            ?.tags
            ?.map { it.value }
            ?: emptyList()

    /** Slične vijesti po prva dva taga */
    @Transaction
    suspend fun getSimilarNews(tags: List<String>): List<News> {
        val subset = if (tags.size > 2) tags.take(2) else tags
        return loadSimilarNewsWithTags(subset).map { it.toNews() }
    }
}