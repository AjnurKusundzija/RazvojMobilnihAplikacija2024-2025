package etf.ri.rma.newsfeedapp.data.network



import etf.ri.rma.newsfeedapp.data.RetrofitInstance
import etf.ri.rma.newsfeedapp.data.network.api.ImagaApiService
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import etf.ri.rma.newsfeedapp.dto.TagsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImagaDAO {
    private val tagsCache = mutableMapOf<String, List<String>>()


    private var apiService: ImagaApiService = RetrofitInstance.imaggaApi


    fun setApiService(service: ImagaApiService) {
       this.apiService = service
    }

    private val urlRegex = Regex("^https?://.*\\.(jpg|jpeg|png|bmp|gif)(\\?.*)?\$", RegexOption.IGNORE_CASE)


    suspend fun getTags(imageUrl: String): List<String> = withContext(Dispatchers.IO) {
        if (!urlRegex.matches(imageUrl)) {
            throw InvalidImageURLException()
        }

        tagsCache[imageUrl]?.let { return@withContext it }


        val response: TagsResponse = apiService.getTags(imageUrl)
        val tagList = response.result?.tags?.mapNotNull { it.tag?.en } ?: emptyList()

        tagsCache[imageUrl] = tagList
        return@withContext tagList
    }


}
