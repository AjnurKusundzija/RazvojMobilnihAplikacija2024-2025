package etf.ri.rma.newsfeedapp.data

import androidx.room.*
import etf.ri.rma.newsfeedapp.data.local.relation.NewsWithTags
import etf.ri.rma.newsfeedapp.model.NewsEntity

import etf.ri.rma.newsfeedapp.model.NewsTagCrossRef
import etf.ri.rma.newsfeedapp.model.TagEntity

@Dao
interface SavedNewsDAO {

    @Transaction
    suspend fun saveNews(news: NewsEntity): Boolean {
        val existing = getNewsByUuid(news.uuid)
        if (existing != null) return false
        insertNews(news)
        return true
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(news: NewsEntity): Long

    @Query("SELECT * FROM News WHERE uuid = :uuid LIMIT 1")
    suspend fun getNewsByUuid(uuid: String): NewsEntity?

    @Transaction
    @Query("SELECT * FROM News")
    suspend fun allNewsWithTags(): List<NewsWithTags>

    @Transaction
    @Query("SELECT * FROM News WHERE category = :category")
    suspend fun getNewsWithTagsByCategory(category: String): List<NewsWithTags>

    // Dodaje tagove na vijest, vraća broj novih tagova
    @Transaction
    suspend fun addTags(tags: List<String>, newsId: String): Int {
        var newTagsCount = 0
        for (tag in tags) {
            val tagId = getTagIdByValue(tag) ?: run {
                val newId = insertTag(TagEntity(value = tag)).toInt()
                newTagsCount++
                newId
            }
            insertNewsTagCrossRef(NewsTagCrossRef(newsId = newsId, tagsId = tagId))
        }
        return newTagsCount
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Query("SELECT id FROM Tags WHERE value = :value LIMIT 1")
    suspend fun getTagIdByValue(value: String): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewsTagCrossRef(crossRef: NewsTagCrossRef)

    @Transaction
    @Query("SELECT * FROM News WHERE id = :newsId")
    suspend fun getNewsWithTagsById(newsId: Int): NewsWithTags?

    @Query("""
        SELECT t.value FROM Tags t
        INNER JOIN NewsTags nt ON nt.tagsId = t.id
        WHERE nt.newsId = :newsId
    """)
    suspend fun getTags(newsId: Int): List<String>

    @Transaction
    @Query("""
        SELECT DISTINCT n.* FROM News n
        INNER JOIN NewsTags nt ON nt.newsId = n.id
        INNER JOIN Tags t ON t.id = nt.tagsId
        WHERE t.value IN (:tags)
        ORDER BY n.publishedDate DESC
    """)
    suspend fun getSimilarNews(tags: List<String>): List<NewsWithTags>
}