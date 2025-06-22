package etf.ri.rma.newsfeedapp.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import etf.ri.rma.newsfeedapp.data.SavedNewsDAO

import etf.ri.rma.newsfeedapp.model.NewsItem

class NewsRepository(
    private val context: Context,
    internal val newsDao: NewsDAO,
    private val imaggaDao: ImagaDAO,
    internal val savedNewsDao: SavedNewsDAO
) {

    private fun hasNetwork(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(nw) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    suspend fun getTopStories(category: String): List<NewsItem> {
        return if (hasNetwork()) {
            val apiList = newsDao.getTopStoriesByCategory(category)
            apiList.forEach { item ->

                if (savedNewsDao.saveNews(item)) {

                }

                val tags = imaggaDao.getTags(item.imageUrl.orEmpty())

                val entity = savedNewsDao.findNewsByUuid(item.uuid)
                if (entity != null) {
                    savedNewsDao.addTags(tags, entity.id)
                }
            }
            apiList
        } else {

            savedNewsDao.getNewsWithCategory(category)
        }
    }


    suspend fun getSimilarNews(item: NewsItem): List<NewsItem> {
        return if (!hasNetwork()) {

            val dbTags = savedNewsDao.getTags(savedNewsDao.findNewsByUuid(item.uuid)!!.id)
            savedNewsDao.getSimilarNews(dbTags.take(2))
        } else {
            newsDao.getSimilarStories(item.uuid).take(2)
        }
    }


    suspend fun getTagsForNews(item: NewsItem): List<String> {
        val entity = savedNewsDao.findNewsByUuid(item.uuid)
            ?: return emptyList()
        val inDb = savedNewsDao.getTags(entity.id)
        if (inDb.isNotEmpty()) return inDb

        return if (hasNetwork()) {
            val remote = imaggaDao.getTags(item.imageUrl.orEmpty())
            savedNewsDao.addTags(remote, entity.id)
            remote
        } else {
            emptyList()
        }
    }


    suspend fun getAllSavedNews(): List<NewsItem> =
        savedNewsDao.allNews()
}
