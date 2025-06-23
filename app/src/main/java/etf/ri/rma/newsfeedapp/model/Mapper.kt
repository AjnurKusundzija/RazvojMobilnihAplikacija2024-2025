
package etf.ri.rma.newsfeedapp.model

import etf.ri.rma.newsfeedapp.entites.NewsEntity

fun NewsItem.toEntity(): NewsEntity = NewsEntity(
    uuid = uuid,
    title = title,
    snippet = snippet,
    imageUrl = imageUrl,
    category = category,
    isFeatured = isFeatured,
    source = source,
    publishedDate = publishedDate
)

