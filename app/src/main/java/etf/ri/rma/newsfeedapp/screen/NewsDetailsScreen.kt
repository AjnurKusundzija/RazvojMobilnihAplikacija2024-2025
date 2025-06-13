package etf.ri.rma.newsfeedapp.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import etf.ri.rma.newsfeedapp.R
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem

@Composable
fun NewsDetailsScreen(
    newsId: String,
    navController: NavController,
    newsDAO: NewsDAO,
    imagaDAO: ImagaDAO,
    onBack: () -> Unit
) {
    val background = if (isSystemInDarkTheme()) Color(0xFF3E3838) else Color(0xFF798DDC)
    val titleBg = if (isSystemInDarkTheme()) Color(0xFF797272) else Color(0xFF9191B6)


    val all by remember { mutableStateOf(newsDAO.getAllStories()) }
    val vijest = all.find { it.uuid == newsId }

    if (vijest == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Vijest nije pronađena.", color = Color.Red)
            Button(onClick = { onBack() }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Natrag")
            }
        }
        return
    }

    var tagovi by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingTags by remember { mutableStateOf(true) }
    var tagsError by remember { mutableStateOf(false) }

    var povezanevijesti by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var isLoadingSimilar by remember { mutableStateOf(true) }


    LaunchedEffect(newsId) {

        isLoadingTags = true
        tagsError = false
        val url = vijest.imageUrl.orEmpty()
        try {
            tagovi = if (url.isNotBlank()) imagaDAO.getTags(url,newsId) else emptyList()
        } catch (e: Exception) {
            tagsError = true
            tagovi = emptyList()
        }
        isLoadingTags = false


        isLoadingSimilar = true
        try {
            povezanevijesti = newsDAO.getSimilarStories(newsId, vijest.category)
        } catch (e: Exception) {
            povezanevijesti = emptyList()
        }
        isLoadingSimilar = false
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = titleBg)
            ) {
                Text(
                    text = "Detalji vijesti",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }


            Text(
                text = vijest.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface
            )


            val placeholder = painterResource(R.drawable.vijesti)
            if (vijest.imageUrl.isNullOrBlank()) {
                Image(
                    painter = placeholder,
                    contentDescription = "No image available",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = vijest.imageUrl,
                    placeholder = placeholder,
                    error = placeholder,
                    contentDescription = "News Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }


            Text(
                text = "Tagovi slike:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface
            )


            if (vijest.imageUrl.isNullOrBlank()) {
                Text(
                    "Nema slike → nema tagova za prikaz.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            } else when {
                isLoadingTags -> {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        StyledCircularIndicator()
                    }
                }
                tagsError -> {
                    Text(
                        "Tagovi nisu dostupni.",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
                tagovi.isNotEmpty() -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(tagovi.take(8)) { tag ->
                            AssistChip(onClick = {}, label = { Text(tag) })
                        }
                    }
                }
                else -> {
                    Text(
                        "Nema pronađenih tagova.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }


            Text("Sažetak:", style = MaterialTheme.typography.titleMedium)
            Text(vijest.snippet.orEmpty(), style = MaterialTheme.typography.bodyLarge)
            Text("Kategorija: ${vijest.category}")
            Text("Izvor: ${vijest.source.orEmpty()}")
            Text("Datum: ${vijest.publishedDate.orEmpty()}")


            Text(
                text = "Povezane vijesti iz iste kategorije:",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )


            when {
                isLoadingSimilar -> {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                povezanevijesti.isNotEmpty() -> {
                    Column {
                        povezanevijesti.forEach { related ->
                            Text(
                                text = related.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("details/${related.uuid}") {
                                            launchSingleTop = true
                                        }
                                    }
                                    .padding(8.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                else -> {
                    Text("Nema povezanih vijesti")
                }
            }


            Spacer(Modifier.height(30.dp))
            Button(
                onClick = { navController.popBackStack("home", false) },
                modifier = Modifier.testTag("details_close_button")
            ) {
                Text("Zatvori detalje")
            }
        }
    }
}
@Composable
fun StyledCircularIndicator() {

    CircularProgressIndicator(
        modifier = Modifier
            .size(48.dp),
        strokeWidth = 4.dp,
        color = MaterialTheme.colorScheme.primary
    )
}

