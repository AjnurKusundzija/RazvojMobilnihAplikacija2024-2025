package etf.ri.rma.newsfeedapp.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.model.NewsItem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn

@Composable
fun NewsList(newsList: List<NewsItem>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("news_list")
    ) {
        newsList.forEach { news ->
            item {
                NewsCard(news = news)
            }
        }
    }
}

@Composable
fun NewsCard(news: NewsItem) {
    if (!news.isFeatured) {
        StandardNewsCard(news)

    } else {
        FeaturedNewsCard(news)
    }
}