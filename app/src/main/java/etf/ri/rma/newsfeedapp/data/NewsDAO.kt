package etf.ri.rma.newsfeedapp.data

import android.util.Log

import etf.ri.rma.newsfeedapp.dto.NewsResponse
import etf.ri.rma.newsfeedapp.exceptions.InvalidUUIDException
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.util.CategoryMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object NewsDAO {

    private const val TAG = "NewsDAO"
    private const val API_KEY = "BEovF9RPKzTpn8zxxWOOqKcrQ1t6WsU7UetnJbod"
    private const val LANGUAGE = "en"
    private const val CACHE_DURATION_MS = 30_000L

    private val allStoriesCache = mutableListOf<NewsItem>()
    private val categoryTimestamps = mutableMapOf<String, Long>()
    private val categoryCache = mutableMapOf<String, List<NewsItem>>()
    private val similarStoriesCache = mutableMapOf<String, List<NewsItem>>()
    private val initialNewsData = NewsData.newsItems
    private var initialLoaded = false

    fun getAllStories(): List<NewsItem> {
        ensureInitialLoaded()
        Log.d(TAG, "getAllStories() returned ${allStoriesCache.size} items")
        return allStoriesCache.toList()
    }

    suspend fun getTopStoriesByCategory(category: String): List<NewsItem> = withContext(Dispatchers.IO) {
        ensureInitialLoaded()
        val now = System.currentTimeMillis()
        val displayCategory = category
        val apiCategory = CategoryMapper.toApiCategory(displayCategory)
        val lastFetched = categoryTimestamps[apiCategory] ?: 0

        Log.d(TAG, "getTopStoriesByCategory() called for category=$category (api=$apiCategory)")
        if (now - lastFetched < CACHE_DURATION_MS && categoryCache[apiCategory] != null) {
            Log.d(TAG, "Returning cached news for $apiCategory: ${categoryCache[apiCategory]?.size} items")
            return@withContext categoryCache[apiCategory]!!
        }

        Log.d(TAG, "API poziv za kategoriju: $apiCategory, language=$LANGUAGE, api_token=$API_KEY")
        val response: NewsResponse = try {
            RetrofitInstance.newsApi.getTopStoriesByCategory(apiCategory, LANGUAGE, API_KEY)
        } catch (e: Exception) {
            Log.e(TAG, "API ERROR (${e.javaClass.simpleName}) za $apiCategory: ${e.message}")
            throw e // Ili možeš vratiti praznu listu/poruku korisniku
        }
        Log.d(TAG, "API response: ${response.data?.size ?: 0} items received")

        // 1. Mapiraj podatke (bez imageTags još)
        val newsItems = response.data
            ?.take(3)
            ?.map { dto ->
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
        Log.d(TAG, "Mapped $apiCategory: ${newsItems.size} news items to NewsItem objects")

        // 2. Dohvati tagove iz Imagga za svaku vijest
        val newsWithTags = newsItems.map { news ->
            val tags = try {
                ImaggaDAO.getTags(news.imageUrl.toString()).also {
                    Log.d(TAG, "Imagga tags for ${news.title?.take(15)}...: $it")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Imagga ERROR for image ${news.imageUrl}: ${e.message}")
                emptyList<String>()
            }
            news.copy(imageTags = ArrayList(tags))
        }

        // KOREKTNA LOGIKA ZA FEATURED/STANDARD
        val previousItems = categoryCache[apiCategory] ?: emptyList()
        val featuredIds = newsWithTags.map { it.uuid }.toSet()
        val standard = previousItems.filter { it.uuid !in featuredIds }.map { it.copy(isFeatured = false) }
        val updatedCategoryList = newsWithTags + standard

        Log.d(
            TAG,
            "Updated category '$category' from ${previousItems.size} to ${updatedCategoryList.size} items (featured: ${newsWithTags.size}, standard: ${standard.size})"
        )

        categoryCache[apiCategory] = updatedCategoryList

        newsWithTags.forEach { item ->
            if (allStoriesCache.none { it.uuid == item.uuid }) {
                allStoriesCache.add(0, item)
                Log.d(TAG, "Added news '${item.title?.take(20)}...' to allStoriesCache")
            } else {
                Log.d(TAG, "News '${item.title?.take(20)}...' already in cache")
            }
        }

        categoryTimestamps[apiCategory] = now
        Log.d(TAG, "Returning ${updatedCategoryList.size} items for $apiCategory")
        return@withContext updatedCategoryList
    }

    suspend fun getSimilarStories(uuid: String): List<NewsItem> = withContext(Dispatchers.IO) {
        Log.d(TAG, "getSimilarStories() called for uuid='$uuid'")

        // 1. Validacija UUID-a
        if (!isValidUUID(uuid)) {
            Log.e(TAG, "Invalid UUID format: $uuid")
            throw InvalidUUIDException()
        }

        // 2. Provjera cache-a
        similarStoriesCache[uuid]?.let {
            Log.d(TAG, "Returning cached similar stories for '$uuid' (${it.size} items)")
            return@withContext it
        }

        // 3. Pronađi originalnu vijest u cache-u
        Log.d(TAG, "allStoriesCache currently holds UUIDs: ${allStoriesCache.map { it.uuid }}")
        val currentNews = allStoriesCache.find { it.uuid == uuid }
        if (currentNews == null) {
            Log.e(TAG, "No news found in allStoriesCache for uuid='$uuid'")
            throw InvalidUUIDException()
        }
        Log.d(TAG, "Found original news in cache: '${currentNews.title?.take(30)}...' (category=${currentNews.category})")

        // 4. Pozovi API
        val response: NewsResponse = try {
            Log.d(TAG, "Calling API for similar stories (uuid=$uuid)")
            RetrofitInstance.newsApi.getSimilarStories(uuid, API_KEY)
        } catch (e: Exception) {
            Log.e(TAG, "API ERROR similarStories for $uuid: ${e.message}")
            throw e
        }
        Log.d(TAG, "API response: ${response.data?.size ?: 0} similar items received")
        response.data?.forEach {
            Log.d(TAG, "Similar from API: uuid=${it.uuid}, title=${it.title?.take(40)}")
        }

        // 5. Mapiranje i filtracija
        val similar = response.data
            ?.filter {
                val inCategory = it.categories.firstOrNull() == currentNews.category
                Log.d(TAG, "Evaluating similar: uuid='${it.uuid}', title='${it.title?.take(25)}...', inCategory=$inCategory")
                it.uuid != uuid && inCategory
            }
            ?.take(2)
            ?.map { dto ->
                val news = NewsItem(
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
                // 6. Dohvati tagove slike za slične vijesti
                val tags = try {
                    Log.d(TAG, "Getting Imagga tags for image '${news.imageUrl}'")
                    ImaggaDAO.getTags(news.imageUrl.toString()).also {
                        Log.d(TAG, "Imagga tags for similar '${news.title?.take(15)}...': $it")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Imagga ERROR for similar image ${news.imageUrl}: ${e.message}")
                    emptyList<String>()
                }
                news.copy(imageTags = ArrayList(tags))
            } ?: emptyList()

        Log.d(TAG, "Returning ${similar.size} similar stories for uuid=$uuid")
        similar.forEachIndexed { idx, news ->
            Log.d(TAG, "Similar [$idx]: uuid='${news.uuid}', title='${news.title?.take(25)}...'")
        }

        // 7. Dodaj nove related vijesti u cache (ako ih nema već!)
        similar.forEach { simItem ->
            if (allStoriesCache.none { it.uuid == simItem.uuid }) {
                allStoriesCache.add(simItem)
                Log.d(TAG, "Added related news to cache: uuid='${simItem.uuid}', title='${simItem.title?.take(40)}'")
            } else {
                Log.d(TAG, "Related news already in cache: uuid='${simItem.uuid}'")
            }
        }
        Log.d(TAG, "allStoriesCache UUIDs after update: ${allStoriesCache.map { it.uuid }}")

        similarStoriesCache[uuid] = similar
        return@withContext similar
    }



    private fun ensureInitialLoaded() {
        if (!initialLoaded) {
            allStoriesCache.clear()
            allStoriesCache.addAll(initialNewsData)
            initialLoaded = true
            Log.d(TAG, "Initial load: allStoriesCache initialized with ${initialNewsData.size} items")
        }
    }

    private fun isValidUUID(uuid: String): Boolean {
        return try {
            UUID.fromString(uuid)
            true
        } catch (e: Exception) {
            false
        }
    }
}
