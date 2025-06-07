package etf.ri.rma.newsfeedapp.data.network

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import etf.ri.rma.newsfeedapp.model.NewsEntity
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.NewsTagsCrossRef
import etf.ri.rma.newsfeedapp.model.NewsWithTags
import etf.ri.rma.newsfeedapp.model.TagEntity
import retrofit2.http.Query

@Dao
interface SavedNewsDAO {

    // ——— 1) Spremi vijest (ako ne postoji isti uuid) ———
    @Query("SELECT * FROM News WHERE uuid = :uuid LIMIT 1")
    fun findByUuid(uuid: String): NewsEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNews(entity: NewsEntity): Long

    @Transaction
    fun saveNews(news: NewsItem): Boolean {
        // ako već postoji, vrati false
        if (findByUuid(news.uuid) != null) return false
        // mapiraj NewsItem -> NewsEntity i ubaci
        val rowId = insertNews(
            NewsEntity(
                uuid          = news.uuid,
                title         = news.title,
                snippet       = news.snippet,
                imageUrl      = news.imageUrl,
                publishedDate = news.publishedDate,
                source        = news.source,
                category      = news.category,
                isFeatured    = news.isFeatured
            )
        )
        return rowId != -1L
    }

    // ——— 2) Sve vijesti + tagovi ———
    @Transaction
    @Query("SELECT * FROM News ORDER BY publishedDate DESC")
    fun loadAll(): List<NewsWithTags>

    fun allNews(): List<NewsItem> =
        loadAll().map { NewsItem.fromEntity(it) }

    // ——— 3) Vijesti po kategoriji ———
    @Transaction
    @Query("SELECT * FROM News WHERE category = :category ORDER BY publishedDate DESC")
    fun loadByCategory(category: String): List<NewsWithTags>

    fun getNewsWithCategory(category: String): List<NewsItem> =
        loadByCategory(category).map { NewsItem.fromEntity(it) }

    // ——— 4) Rad s tagovima ———
    @Query("SELECT * FROM Tags WHERE value = :value LIMIT 1")
    fun findTag(value: String): TagEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTag(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCrossRef(ref: NewsTagsCrossRef)

    @Transaction
    fun addTags(tags: List<String>, newsId: Int): Int {
        var newCount = 0
        for (tagValue in tags) {
            // ako tag ne postoji, dodaj ga
            val tagId = findTag(tagValue)?.id ?:
            run {
                val newId = insertTag(TagEntity(value = tagValue))
                if (newId != -1L) newCount++
                newId.toInt()
            }
            // poveži vijest i tag (ignoriraj conflict)
            insertCrossRef(NewsTagsCrossRef(newsId = newsId, tagsId = tagId))
        }
        return newCount
    }

    // ——— 5) Lista tagova za vijest ———
    @Query("""
    SELECT t.value
      FROM Tags t
      JOIN NewsTags nt ON t.id = nt.tagsId
     WHERE nt.newsId = :newsId
  """)
    fun getTags(newsId: Int): List<String>

    // ——— 6) Slične vijesti ———
    @Transaction
    @Query("""
    SELECT DISTINCT n.*
      FROM News n
      JOIN NewsTags nt ON n.id = nt.newsId
      JOIN Tags t       ON t.id = nt.tagsId
     WHERE t.value IN (:tags)
     ORDER BY n.publishedDate DESC
  """)
    fun loadSimilar(tags: List<String>): List<NewsWithTags>

    fun getSimilarNews(tags: List<String>): List<NewsItem> =
        loadSimilar(tags).map { NewsItem.fromEntity(it) }
}
