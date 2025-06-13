package etf.ri.rma.newsfeedapp.data


import etf.ri.rma.newsfeedapp.model.NewsEntity
import etf.ri.rma.newsfeedapp.model.NewsItem

class NewsRepository(private val db: NewsDatabase) {

    // 1. Dodaje vijest u bazu ako ne postoji uuid
    suspend fun saveNews(newsItem: NewsItem): Boolean {
        val newsEntity = NewsEntity(
            id = 0, // autoincrement
            uuid = newsItem.uuid,
            title = newsItem.title,
            snippet = newsItem.snippet.toString(),
            imageUrl = newsItem.imageUrl,
            category = newsItem.category,
            isFeatured = newsItem.isFeatured,
            source = newsItem.source.toString(),
            publishedDate = newsItem.publishedDate.toString()
        )
        return db.savedNewsDAO().saveNews(newsEntity)
    }

    // 2. Vraća listu svih vijesti sa tagovima (imageTags popunjeni)
    suspend fun allNews(): List<NewsItem> {
        return db.savedNewsDAO().allNewsWithTags().map { it.toNewsItem() }
    }

    // 3. Vraća sve vijesti po kategoriji (sa tagovima)
    suspend fun getNewsWithCategory(category: String): List<NewsItem> {
        return db.savedNewsDAO().getNewsWithTagsByCategory(category).map { it.toNewsItem() }
    }

    // 4. Dodaje tagove na vijest, vraća broj novih tagova
    suspend fun addTags(tags: List<String>, newsId: String): Int {
        return db.savedNewsDAO().addTags(tags, newsId)
    }

    // 5. Vraća sve tagove za vijest kao stringove
    suspend fun getTags(newsId: Int): List<String> {
        return db.savedNewsDAO().getTags(newsId)
    }

    // 6. Vraća slične vijesti po tagovima (max 2 taga koristiš ako je offline)
    suspend fun getSimilarNews(tags: List<String>): List<NewsItem> {
        val newsWithTagsList = db.savedNewsDAO().getSimilarNews(tags.take(2))
        return newsWithTagsList.map { it.toNewsItem() }
    }
}
