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
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class NewsDAO(
    var newsAPI: NewsApiService = RetrofitInstance.newsApi
) {

    companion object {
        private const val TAG = "NewsDAO"
        private const val API_KEY = "SV7uMJonNFELvX7IFgrK0BYwyPzW83lssDwJFgpb"
        private const val LANGUAGE = "en"
        private const val CACHE_DURATION_MS = 30_000L
    }
    //fun setApiService(service: NewsApiService) {
    //    newsAPI = service
    //}

    private val svevijesti_cache = mutableListOf<NewsItem>()


    private val categoryTimestamps = mutableMapOf<String, Long>()


    private val kategorije_cache = mutableMapOf<String, List<NewsItem>>()


    private val povezanevijesti_cache = mutableMapOf<String, List<NewsItem>>()


    private val pocetnevijesti = NewsData.newsItems
    private var JesuUcitanePocetne = false


    private fun UcitanePocetne() {
        if (!JesuUcitanePocetne) {
            svevijesti_cache.clear()

            svevijesti_cache.addAll(pocetnevijesti)
            JesuUcitanePocetne = true
        }
    }


    fun getAllStories(): List<NewsItem> {
        UcitanePocetne()
        return svevijesti_cache.toList()
    }

    private fun formatirajDatum_ISO_u_ddMMyyyy(isoString: String?): String? {
        if (isoString.isNullOrBlank()) return null
        return try {
            val instant = Instant.parse(isoString)
            val lokalni = instant.atZone(ZoneOffset.UTC).toLocalDate()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            lokalni.format(formatter)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getTopStoriesByCategory(category: String): List<NewsItem> = withContext(Dispatchers.IO) {
        UcitanePocetne()

        val now = System.currentTimeMillis()
        val apikategorija = CategoryMapper.toApiCategory(category)
        val lastFetchTime = categoryTimestamps[apikategorija] ?: 0L
        val isCacheValid = (now - lastFetchTime) < CACHE_DURATION_MS


        if (isCacheValid && kategorije_cache.containsKey(apikategorija)) {
            val cachedStories = kategorije_cache[apikategorija]!!.map { it.copy() }


            svevijesti_cache.forEach { it.isFeatured = false }


            val brandNewFromCache = cachedStories.filter { story ->
                svevijesti_cache.none { it.uuid == story.uuid }
            }
            brandNewFromCache.asReversed().forEach { story ->
                svevijesti_cache.add(0, story)
            }


            val olderSameCategory = svevijesti_cache.filter { item ->
                item.category == apikategorija && cachedStories.none { c -> c.uuid == item.uuid }
            }


            return@withContext cachedStories + olderSameCategory
        }


        try {

            svevijesti_cache.forEach { it.isFeatured = false }

            Log.d(TAG, "Dohvatanje za kategoriju=$apikategorija")
            val response: NewsResponse = newsAPI.getTopStoriesByCategory(
                category = apikategorija,
                language = LANGUAGE,
                apiKey = API_KEY
            )
            Log.d(TAG, "Response: $response")


            val dohvaceneapivijesti: List<NewsItem> = response.data?.map { dto ->
                val formatiraniDatum = formatirajDatum_ISO_u_ddMMyyyy(dto.published_at)
                NewsItem(
                    uuid = dto.uuid,
                    title = dto.title,
                    snippet = dto.description,
                    imageUrl = dto.image_url,
                    publishedDate = formatiraniDatum,
                    source = dto.source,

                    category = apikategorija,
                    isFeatured = true,
                    imageTags = arrayListOf()
                )
            } ?: emptyList()


            kategorije_cache[apikategorija] = dohvaceneapivijesti
            categoryTimestamps[apikategorija] = now


            val najnovije = dohvaceneapivijesti.filter { story ->
                svevijesti_cache.none { it.uuid == story.uuid }
            }
            najnovije.asReversed().forEach { story ->
                svevijesti_cache.add(0, story)
            }


            val istakategorija = svevijesti_cache.filter { item ->
                item.category == apikategorija && dohvaceneapivijesti.none { fs -> fs.uuid == item.uuid }
            }


            return@withContext dohvaceneapivijesti + istakategorija
        } catch (e: Exception) {
            Log.e(TAG, "API greska za kategoriju =$apikategorija: ${e.message}")


            if (kategorije_cache.containsKey(apikategorija)) {
                val novododanevijesti = kategorije_cache[apikategorija]!!.map { it.copy() }
                svevijesti_cache.forEach { it.isFeatured = false }
                val novododanekes = novododanevijesti.filter { story ->
                    svevijesti_cache.none { it.uuid == story.uuid }
                }
                novododanekes.asReversed().forEach { item ->
                    svevijesti_cache.add(0, item)
                }
                val olderSameCategory = svevijesti_cache.filter { item ->
                    item.category == apikategorija && novododanevijesti.none { fs -> fs.uuid == item.uuid }
                }
                return@withContext novododanevijesti + olderSameCategory
            }


            val lokalne = svevijesti_cache.filter { it.category == apikategorija || it.category == category }
            return@withContext lokalne
        }
    }

    suspend fun getSimilarStories(uuid: String, apiToken: String = API_KEY): List<NewsItem> = withContext(Dispatchers.IO) {
        if (!isValidUUID(uuid)) {
            throw InvalidUUIDException("Neispravan uuid: $uuid")
        }
        povezanevijesti_cache[uuid]?.let { return@withContext it }

        try {



            val response = newsAPI.getSimilarStories(
                uuid = uuid,
                apiToken = apiToken
            )
            val slicnevijesti: List<NewsItem> = response.data?.map { dto ->
                val formatiraniDatum = formatirajDatum_ISO_u_ddMMyyyy(dto.published_at)
                NewsItem(
                    uuid = dto.uuid,
                    title = dto.title,
                    snippet = dto.description,
                    imageUrl = dto.image_url,
                    publishedDate = formatiraniDatum,
                    source = dto.source,

                    category = dto.categories.firstOrNull() ?: "general",
                    isFeatured = true,
                    imageTags = arrayListOf()
                )
            } ?: emptyList()

            povezanevijesti_cache[uuid] = slicnevijesti


            val slicnenove = slicnevijesti.filter { item ->
                svevijesti_cache.none { it.uuid == item.uuid }
            }
            slicnenove.asReversed().forEach { item ->
                svevijesti_cache.add(0, item)
            }

            return@withContext slicnevijesti.take(2)
        } catch (e: Exception) {
            val vijest = svevijesti_cache.find { it.uuid == uuid }
            if (vijest != null) {
                val category = vijest.category
                return@withContext svevijesti_cache.filter {
                    it.uuid != uuid && it.category == category
                }.take(2)
            }
            return@withContext emptyList()
        }
    }

    private fun isValidUUID(uuidString: String): Boolean {
        val uuidformat = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        )
        return uuidformat.matcher(uuidString).matches()
    }
}
