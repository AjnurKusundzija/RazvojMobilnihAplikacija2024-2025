package etf.ri.rma.newsfeedapp.data.network



import etf.ri.rma.newsfeedapp.data.NewsRepository
import etf.ri.rma.newsfeedapp.data.RetrofitInstance
import etf.ri.rma.newsfeedapp.data.network.api.ImagaApiService
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import etf.ri.rma.newsfeedapp.dto.TagsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImagaDAO(private val repository: NewsRepository,private var apiService: ImagaApiService = RetrofitInstance.imaggaApi) {

    private val tagovi_cache = mutableMapOf<String, List<String>>()





    fun setApiService(service: ImagaApiService) {
       apiService = service
    }

    private val format_regex = Regex("^https?://.*\\.(jpg|jpeg|png|bmp|gif)(\\?.*)?\$", RegexOption.IGNORE_CASE)


    suspend fun getTags(imageUrl: String, newsId: String): List<String> = withContext(Dispatchers.IO) {
        if (!format_regex.matches(imageUrl)) {
            throw InvalidImageURLException()
        }

        tagovi_cache[imageUrl]?.let { return@withContext it }


        val response: TagsResponse = apiService.getTags(imageUrl)
        val lista_tagova = response.result?.tags?.mapNotNull { it.tag?.en } ?: emptyList()

        repository.addTags(lista_tagova, newsId)
        tagovi_cache[imageUrl] = lista_tagova
        return@withContext lista_tagova
    }


}
