package etf.ri.rma.newsfeedapp.dto

data class TagsResponse(
    val result: TagsResult?,
    val status: TagsStatus?
)

data class TagsResult(
    val tags: List<TagItem>?
)

data class TagItem(
    val confidence: Double?,
    val tag: TagName?
)

data class TagName(
    val en: String?
)

data class TagsStatus(
    val text: String?,
    val type: String?
)
