package etf.ri.rma.newsfeedapp.model

import etf.ri.rma.newsfeedapp.entites.TagEntity


data class NewsItem(
    val uuid: String,
    val title: String,
    val snippet: String?,
    val imageUrl: String?,
    val publishedDate: String?,
    val source: String?,
    var category: String = "",
    var isFeatured: Boolean = false,
    var imageTags: ArrayList<TagEntity> = arrayListOf()
)


