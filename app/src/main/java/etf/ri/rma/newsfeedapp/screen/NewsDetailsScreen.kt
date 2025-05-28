package etf.ri.rma.newsfeedapp.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import etf.ri.rma.newsfeedapp.R
import etf.ri.rma.newsfeedapp.data.ImaggaDAO
import etf.ri.rma.newsfeedapp.data.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem

private const val TAG = "NewsDetailsScreen"

@Composable
fun NewsDetailsScreen(newsId: String, navController: NavController, onBack: () -> Unit) {
    Log.d(TAG, "Displaying NewsDetailsScreen for newsId=$newsId")

    val background = if (isSystemInDarkTheme()) Color(0xFF3E3838) else Color(0xFF798DDC)
    val titleBg    = if (isSystemInDarkTheme()) Color(0xFF797272) else Color(0xFF9191B6)

    // 1) Dohvat svih vijesti
    val all = NewsDAO.getAllStories().also {
        Log.d(TAG, "getAllStories returned ${it.size} items: ${it.map { it.uuid }}")
    }
    val vijest = all.find { it.uuid == newsId }
    if (vijest == null) {
        Log.e(TAG, "NewsItem not found for id=$newsId. Cached UUIDs: ${all.map { it.uuid }}")
        return
    }

    Log.d(TAG, "Found item: ${vijest.title}")

    // State
    var imageTags        by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingTags    by remember { mutableStateOf(true) }
    var tagsError        by remember { mutableStateOf(false) }

    var relatedNews      by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var isLoadingSimilar by remember { mutableStateOf(true) }

    // 2) U background: tagovi i slične vijesti
    LaunchedEffect(newsId) {
        Log.d(TAG, "LaunchedEffect start for newsId=$newsId")

        // --- Tags ---
        isLoadingTags = true; tagsError = false
        val url = vijest.imageUrl.orEmpty()
        Log.d(TAG, "Fetching image tags for URL='$url'")
        try {
            imageTags = if (url.isNotBlank()) ImaggaDAO.getTags(url) else emptyList()
            Log.d(TAG, "Tags fetched (${imageTags.size}): $imageTags")
        } catch (e: Exception) {
            tagsError = true
            imageTags = emptyList()
            Log.e(TAG, "Error fetching tags for URL='$url'", e)
        }
        isLoadingTags = false

        // --- Similar stories ---
        isLoadingSimilar = true
        Log.d(TAG, "Fetching similar stories for uuid='$newsId'")
        try {
            relatedNews = NewsDAO.getSimilarStories(newsId)
            Log.d(TAG, "Similar stories fetched (${relatedNews.size})")
        } catch (e: Exception) {
            relatedNews = emptyList()
            Log.e(TAG, "Error fetching similar stories for '$newsId'", e)
        }
        isLoadingSimilar = false

        Log.d(TAG, "LaunchedEffect end for newsId=$newsId")
    }

    Surface(modifier = Modifier.fillMaxSize(), color = background) {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header card...
            item {
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
            }

            // Title
            item {
                Text(
                    text = vijest.title.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Image
            item {
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
                    Log.d(TAG, "No image URL, showing placeholder")
                } else {
                    AsyncImage(
                        model = vijest.imageUrl,
                        placeholder = placeholder,
                        error       = placeholder,
                        contentDescription = "News Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        onSuccess = { Log.d(TAG, "Image loaded: ${vijest.imageUrl}") },
                        onError   = { Log.e(TAG, "Image load failed: ${vijest.imageUrl}") }
                    )
                }
            }

            // Tags header
            item {
                Text(
                    text = "Tagovi slike:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Tags content
            item {
                if (vijest.imageUrl.isNullOrBlank()) {
                    Text(
                        "Nema slike → nema tagova za prikaz.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    Log.d(TAG, "Skipping tags UI since no image URL")
                } else when {
                    isLoadingTags -> {
                        Log.d(TAG, "Showing tags loading spinner")
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    tagsError -> {
                        Log.d(TAG, "Showing tags error message")
                        Text(
                            "Tagovi nisu dostupni.",
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                    imageTags.isNotEmpty() -> {
                        Log.d(TAG, "Displaying ${imageTags.size} tags")
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(imageTags.take(8)) { tag ->
                                AssistChip(onClick = {}, label = { Text(tag) })
                            }
                        }
                    }
                    else -> {
                        Log.d(TAG, "No tags found")
                        Text(
                            "Nema pronađenih tagova.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            // Snippet, category, source, date...
            item {
                Text("Sažetak:", style = MaterialTheme.typography.titleMedium)
                Text(vijest.snippet.toString(), style = MaterialTheme.typography.bodyLarge)
                Text("Kategorija: ${vijest.category}")
                Text("Izvor: ${vijest.source}")
                Text("Datum: ${vijest.publishedDate}")
            }

            // Similar header
            item {
                Text(
                    text = "Povezane vijesti iz iste kategorije:",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }

            // Similar content
            item {
                when {
                    isLoadingSimilar -> {
                        Log.d(TAG, "Showing similar loading spinner")
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    relatedNews.isNotEmpty() -> {
                        Log.d(TAG, "Displaying ${relatedNews.size} similar items")
                        relatedNews.forEach { related ->
                            Text(
                                text = related.title.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        Log.d(TAG, "Related item clicked: ${related.uuid}")
                                        navController.navigate("details/${related.uuid}") {
                                            launchSingleTop = true
                                        }
                                    }
                                    .padding(8.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    else -> {
                        Log.d(TAG, "No similar items found")
                        Text("Nema povezanih vijesti")
                    }
                }
            }

            // Close button
            item {
                Spacer(Modifier.height(30.dp))
                Button(
                    onClick = {
                        Log.d(TAG, "Closing NewsDetailsScreen")
                        navController.popBackStack("home", false)
                    },
                    modifier = Modifier.testTag("details_close_button")
                ) {
                    Text("Zatvori detalje")
                }
            }
        }
    }
}
