package etf.ri.rma.newsfeedapp.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import etf.ri.rma.newsfeedapp.data.SavedNewsDAO
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.util.CategoryMapper

class NewsRepository(
    private val context: Context,
    private val newsDao: NewsDAO,
    private val imaggaDao: ImagaDAO,
    private val savedNewsDao: SavedNewsDAO
) {


    private fun hasNetwork(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = cm.activeNetwork
        val caps = cm.getNetworkCapabilities(nw)
        val connected = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        return connected
    }


    suspend fun getTopStories(category: String): List<NewsItem> {
        val apiCategory = CategoryMapper.toApiCategory(category)


        return if (hasNetwork()) {

            try {
                val apiList = newsDao.getTopStoriesByCategory(apiCategory)

                apiList.forEach { item ->

                    val saved = savedNewsDao.saveNews(item)



                    try {
                        val tags = imaggaDao.getTags(item.imageUrl.orEmpty())

                        savedNewsDao.findNewsByUuid(item.uuid)?.let { entity ->
                            val added = savedNewsDao.addTags(tags, entity.id)

                        }
                    } catch (e: Exception) {

                    }
                }
                apiList
            } catch (e: Exception) {

                val local = savedNewsDao.getNewsWithCategory(apiCategory)
                local
            }
        } else {

            val local = savedNewsDao.getNewsWithCategory(apiCategory)

            local
        }
    }


    suspend fun getAllSavedNews(): List<NewsItem> {

        return savedNewsDao.allNews()
    }





    suspend fun getSimilarNews(item: NewsItem): List<NewsItem> {

        val apiCategory = CategoryMapper.toApiCategory(item.category)
        return if (!hasNetwork()) {

            savedNewsDao.findNewsByUuid(item.uuid)?.id?.let { id ->
                val dbTags = savedNewsDao.getTags(id)

                val result = savedNewsDao.getSimilarNews(dbTags.take(2))

                result
            } ?: run {

                emptyList()
            }
        } else {

            try {
                val apiSimilar = newsDao.getSimilarStories(item.uuid, apiCategory).take(2)

                apiSimilar
            } catch (e: Exception) {

                savedNewsDao.findNewsByUuid(item.uuid)?.id?.let { id ->
                    val dbTags = savedNewsDao.getTags(id)
                    savedNewsDao.getSimilarNews(dbTags.take(2))
                } ?: emptyList()
            }
        }
    }


    suspend fun getTagsForNews(item: NewsItem): List<String> {

        val entity = savedNewsDao.findNewsByUuid(item.uuid)
        if (entity == null) {

            return emptyList()
        }
        val inDb = savedNewsDao.getTags(entity.id)
        if (inDb.isNotEmpty()) {

            return inDb
        }
        if (!hasNetwork()) {

            return emptyList()
        }

        return try {
            val remote = imaggaDao.getTags(item.imageUrl.orEmpty())
            val added = savedNewsDao.addTags(remote, entity.id)

            remote
        } catch (e: Exception) {

            emptyList()
        }
    }
}
