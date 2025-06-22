package etf.ri.rma.newsfeedapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import etf.ri.rma.newsfeedapp.model.NewsEntity
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.model.NewsTagCrossRef
import etf.ri.rma.newsfeedapp.model.NewsWithTags
import etf.ri.rma.newsfeedapp.model.TagEntity
import etf.ri.rma.newsfeedapp.model.toEntity


@Dao
interface SavedNewsDAO {

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

    @Transaction
    suspend fun saveNews(news: NewsItem): Boolean {
        if (findNewsByUuid(news.uuid) != null) return false
        return insertNewsEntity(news.toEntity()) > 0L
    }

    @Transaction
    suspend fun allNews(): List<NewsItem> =
        loadAllNewsWithTags().map { it.toNewsItem() }

    @Transaction
    suspend fun getNewsWithCategory(category: String): List<NewsItem> =
        loadNewsWithTagsByCategory(category).map { it.toNewsItem() }


    @Transaction
    suspend fun addTags(tags: List<String>, newsId: Int): Int {
        var newlyInsertedCount = 0

        for (value in tags) {

            val insertedId = insertTag(TagEntity(value = value))


            val tagId = if (insertedId != -1L) {
                newlyInsertedCount++
                insertedId.toInt()
            } else {

                findTagIdByValue(value)
                    ?: throw IllegalStateException("Ne mogu naći tag `$value` nakon ubacivanja")
            }


            insertCrossRef(NewsTagCrossRef(newsId = newsId, tagsId = tagId))
        }

        return newlyInsertedCount
    }


    @Transaction
    suspend fun getTags(newsId: Int): List<String> =
        loadAllNewsWithTags()
            .firstOrNull { it.news.id == newsId }
            ?.tags
            ?.map { it.value }
            ?: emptyList()

    @Transaction
    suspend fun getSimilarNews(tags: List<String>): List<NewsItem> {
        val subset = if (tags.size > 2) tags.take(2) else tags
        return loadSimilarNewsWithTags(subset).map { it.toNewsItem() }
    }
}
