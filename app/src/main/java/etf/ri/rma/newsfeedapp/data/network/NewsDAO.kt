package etf.ri.rma.newsfeedapp.data.network


import android.util.Log
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.data.RetrofitInstance
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidUUIDException
import etf.ri.rma.newsfeedapp.dto.NewsResponse
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.util.CategoryMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.util.regex.Pattern

class NewsDAO(
    var newsAPI: NewsApiService = RetrofitInstance.newsApi
) {
    companion object {
        private const val TAG = "NewsDAO"
        private const val API_KEY = "BEovF9RPKzTpn8zxxWOOqKcrQ1t6WsU7UetnJbod"
        private const val LANGUAGE = "en"
        private const val CACHE_DURATION_MS = 30_000L
    }

    private val allStoriesCache = mutableListOf<NewsItem>()
    private val categoryTimestamps = mutableMapOf<String, Long>()
    private val categoryCache = mutableMapOf<String, List<NewsItem>>()
    private val similarStoriesCache = mutableMapOf<String, List<NewsItem>>()
    private val initialNewsData = NewsData.newsItems
    private var initialDataLoaded = false

    fun setApiService(service: NewsApiService) {
        newsAPI = service
    }

    // INITIAL LOAD
    private fun ensureInitialDataLoaded() {
        if (!initialDataLoaded) {
            allStoriesCache.clear()
            allStoriesCache.addAll(initialNewsData)
            initialDataLoaded = true
        }
    }

    fun getAllStories(): List<NewsItem>  {
        ensureInitialDataLoaded()

        return  allStoriesCache
    }

    suspend fun getTopStoriesByCategory(category: String): List<NewsItem> = withContext(Dispatchers.IO) {
        ensureInitialDataLoaded()
        val now = System.currentTimeMillis()
        val apiCategory = CategoryMapper.toApiCategory(category)
        val lastFetchTime = categoryTimestamps[apiCategory] ?: 0L
        val isCacheValid = (now - lastFetchTime) < CACHE_DURATION_MS

        if (isCacheValid && categoryCache.containsKey(apiCategory)) {
            Log.d("NewsDAO", "Returning from cache for category: $apiCategory, count: ${categoryCache[apiCategory]?.size}")
            return@withContext categoryCache[apiCategory]!!.take(3)
        }

        try {
            Log.d("NewsDAO", "Calling getTopStoriesByCategory on API with params: categories=$apiCategory, language=$LANGUAGE, api_token=$API_KEY")
            val response: NewsResponse = newsAPI.getTopStoriesByCategory(
                category = apiCategory,
                language = LANGUAGE,
                apiKey = API_KEY
            )
            Log.d("NewsDAO", "API response received. Response: $response")
            val stories = response.data?.map { dto ->
                NewsItem(
                    uuid = dto.uuid,
                    title = dto.title,
                    snippet = dto.description,
                    imageUrl = dto.image_url,
                    publishedDate = dto.published_at,
                    source = dto.source,
                    category = dto.categories.firstOrNull() ?: apiCategory,
                    isFeatured = true,
                    imageTags = arrayListOf()
                )
            } ?: emptyList()

            categoryCache[apiCategory] = stories
            categoryTimestamps[apiCategory] = now

            // Ubaci u globalni cache nove
            stories.forEach { story ->
                if (allStoriesCache.none { it.uuid == story.uuid }) {
                    allStoriesCache.add(story)
                }
            }
            Log.d("NewsDAO", "Returning top stories for category=$apiCategory: ${stories.map { it.uuid }}")
            return@withContext stories.take(3)
        } catch (e: Exception) {
            Log.e("NewsDAO", "API ERROR for category=$apiCategory: ${e.message}")
            if (categoryCache.containsKey(apiCategory)) {
                return@withContext categoryCache[apiCategory]!!.take(3)
            }
            return@withContext allStoriesCache.filter {
                it.category == apiCategory || it.category == category
            }.take(3)
        }
    }




    suspend fun getSimilarStories(uuid: String, apiToken: String = API_KEY): List<NewsItem> = withContext(Dispatchers.IO) {
        if (!isValidUUID(uuid)) {
            throw InvalidUUIDException("Invalid UUID: $uuid")
        }
        similarStoriesCache[uuid]?.let { return@withContext it }
        try {
            val response = newsAPI.getSimilarStories(
                uuid = uuid,
                apiToken = apiToken  // OVDJE KORISTI PARAMETAR, NE fiksni API_KEY!
            )
            val similarStories = response.data?.map { dto ->
                NewsItem(
                    uuid = dto.uuid,
                    title = dto.title,
                    snippet = dto.description,
                    imageUrl = dto.image_url,
                    publishedDate = dto.published_at,
                    source = dto.source,
                    category = dto.categories.firstOrNull() ?: "general",
                    isFeatured = true,
                    imageTags = arrayListOf()
                )
            } ?: emptyList()

            similarStoriesCache[uuid] = similarStories
            // Dodaj u glavni cache nove related
            similarStories.forEach { item ->
                if (allStoriesCache.none { it.uuid == item.uuid }) {
                    allStoriesCache.add(item)
                }
            }
            return@withContext similarStories.take(2)
        } catch (e: Exception) {
            val story = allStoriesCache.find { it.uuid == uuid }
            if (story != null) {
                val category = story.category
                return@withContext allStoriesCache.filter {
                    it.uuid != uuid && it.category == category
                }.take(2)
            }
            return@withContext emptyList()
        }
    }


    private fun isValidUUID(uuidString: String): Boolean {
        val pattern = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        )
        return pattern.matcher(uuidString).matches()
    }
}
