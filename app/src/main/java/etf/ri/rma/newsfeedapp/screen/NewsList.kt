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
import androidx.navigation.NavHostController

@Composable
fun NewsList(newsList: List<NewsItem>,navController: NavHostController) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("news_list")
    ) {
        newsList.forEach { news ->
            item {
                NewsCard(news = news, onClick = {
                    navController.navigate("details/${news.uuid}")
                })
            }
        }

    }
}

@Composable
fun NewsCard(news: NewsItem, onClick: () -> Unit) {
    if (!news.isFeatured) {
        StandardNewsCard(news, onClick)
    } else {
        FeaturedNewsCard(news, onClick)
    }
}

