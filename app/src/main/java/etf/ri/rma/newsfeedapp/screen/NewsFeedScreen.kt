package etf.ri.rma.newsfeedapp.screen

import MessageCard
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import etf.ri.rma.newsfeedapp.data.ChipData
import etf.ri.rma.newsfeedapp.data.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.launch

@Composable
fun NewsFeedScreen(
    navController: NavHostController,
    kategorije: Set<String>,
    dateRange: Pair<Long, Long>?,
    nepozeljneRijeci: List<String>,
    onKategorijeUpdate: (Set<String>) -> Unit
) {
    val TAG = "NewsFeedScreen"
    val scope = rememberCoroutineScope()
    var newsList by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var aktivnaKategorija by remember { mutableStateOf("Sve") }
    //  — inicijalni dohvat svih priča —
    DisposableEffect(aktivnaKategorija) {
        Log.d(TAG, "DisposableEffect: reload for kategorija=$aktivnaKategorija")
        scope.launch {
            newsList = if (aktivnaKategorija == "Sve")
                NewsDAO.getAllStories()
            else
                NewsDAO.getTopStoriesByCategory(aktivnaKategorija)
            Log.d(TAG, "Reloaded newsList for kategorija=$aktivnaKategorija, count=${newsList.size}")
        }
        onDispose { }
    }


    // filterirana lista (datum + nepoželjne riječi, ali KATEGORIJE više ne filtriramo ovdje)
    val filtriraneVijesti = newsList.filter { news ->
        // ... tvoja postojeća logika za dateRange i nepoželjne riječi ...
        true
    }

    val chipovi = listOf(
        ChipData("Više filtera ...", "filter_chip_more", "Više filtera ..."),
        ChipData("Sve",            "filter_chip_all", "Sve"),
        ChipData("Politika",       "filter_chip_pol", "Politika"),
        ChipData("Sport",          "filter_chip_spo", "Sport"),
        ChipData("Nauka",          "filter_chip_sci", "Nauka"),
        ChipData("Tehnologija",    "filter_chip_tech","Tehnologija"),
        ChipData("Posao",          "filter_chip_biz", "Posao"),
        ChipData("Zdravlje",       "filter_chip_health","Zdravlje"),
        ChipData("Zabava",         "filter_chip_ent", "Zabava"),
        ChipData("Hrana",          "filter_chip_food","Hrana"),
        ChipData("Putovanja",      "filter_chip_travel","Putovanja")
    )


    val bojachipa   = if (isSystemInDarkTheme()) Color(0xFF312D2D) else Color(0xFF4C60B6)
    val bojapozadine= if (isSystemInDarkTheme()) Color(0xFF3E3838) else Color(0xFF9CAEEE)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bojapozadine
    ) {
        Column {
            Spacer(Modifier.height(18.dp))
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(chipovi) { chip ->
                    val selected = kategorije.contains(chip.kategorija)
                    FilterChip(
                        selected = selected,
                        onClick = {
                            if (chip.tag == "filter_chip_more") {
                                navController.navigate("filters")
                            } else {
                                aktivnaKategorija = chip.kategorija // <- OVDE!
                                onKategorijeUpdate(setOf(chip.kategorija))
                                scope.launch {
                                    newsList = if (chip.kategorija == "Sve")
                                        NewsDAO.getAllStories()
                                    else
                                        NewsDAO.getTopStoriesByCategory(chip.kategorija)
                                }
                            }
                        },
                        label = {
                            Text(chip.prikaz,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        },
                        leadingIcon = {
                            if (selected)
                                Icon(Icons.Filled.Check, contentDescription = null,
                                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black)
                        },
                        colors = FilterChipDefaults.filterChipColors(containerColor = bojachipa),
                        modifier = Modifier.testTag(chip.tag)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (filtriraneVijesti.isNotEmpty()) {
                Log.d(TAG, "Displaying filtered news: count = ${filtriraneVijesti.size}")
                NewsList(filtriraneVijesti, navController)
            } else {
                Log.d(TAG, "No news to display after filtering")
                MessageCard("Nema vijesti za prikazane filtere.")
            }
        }
    }
}
