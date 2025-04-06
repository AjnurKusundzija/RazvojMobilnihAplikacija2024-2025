package etf.ri.rma.newsfeedapp.screen


import FeaturedNewsCard
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.model.NewsItem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid



@Composable
fun NewsList(newsList: List<NewsItem>) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("news_list")
    ) {


        items(newsList) { news ->
            NewsCard(news = news)
        }
    }
}

@Composable
fun NewsCard(news: NewsItem) {

    if (!news.isFeatured) {
        StandardNewsCard(
            title = news.title,
            snippet = news.snippet,
            imageUrl = news.imageUrl,
            source = news.source,
            publishedDate = news.publishedDate
        )
    } else {
        FeaturedNewsCard(
            title=news.title,
            snippet=news.snippet,
            imageUrl=news.imageUrl,
            source=news.source,
            publishedDate = news.publishedDate
        )
    }
}
