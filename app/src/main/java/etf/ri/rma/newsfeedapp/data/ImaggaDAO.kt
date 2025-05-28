package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.exceptions.InvalidImageURLException



import etf.ri.rma.newsfeedapp.dto.TagsResponse

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImaggaDAO {
    private val tagsCache = mutableMapOf<String, List<String>>() // imageUrl → tagovi


    private val urlRegex = Regex("^https?://.*\\.(jpg|jpeg|png|bmp|gif)(\\?.*)?\$", RegexOption.IGNORE_CASE)

    /**
     * Vraća tagove za sliku sa proslijeđenim URL-om.
     * Ako URL nije validan baca InvalidImageURLException.
     * Ako je tagove već dohvaćena, vraća ih iz cache-a.
     */
    suspend fun getTags(imageUrl: String): List<String> = withContext(Dispatchers.IO) {

        if (!urlRegex.matches(imageUrl)) {
            throw InvalidImageURLException()
        }


        tagsCache[imageUrl]?.let { return@withContext it }


        val response: TagsResponse = RetrofitInstance.imaggaApi.getTags(imageUrl)


        val tagList = response.result?.tags?.map { it.tag?.en }


        tagsCache[imageUrl] = tagList as List<String>

        return@withContext tagList
    }
}





