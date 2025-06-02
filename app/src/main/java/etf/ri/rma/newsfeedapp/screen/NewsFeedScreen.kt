package etf.ri.rma.newsfeedapp.screen

import MessageCard
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
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.model.NewsItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewsFeedScreen(
    navController: NavHostController,
    newsDAO: NewsDAO,
    kategorije: Set<String>,
    dateRange: Pair<Long, Long>?,
    nepozeljneRijeci: List<String>,
    onKategorijeUpdate: (Set<String>) -> Unit
) {
    val scope = rememberCoroutineScope()


    var listaVijesti by remember { mutableStateOf<List<NewsItem>>(emptyList()) }


    val sdf = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }


    DisposableEffect(kategorije) {
        scope.launch {
            listaVijesti = when {

                kategorije.contains("Sve") -> {
                    newsDAO.getAllStories()
                }

                kategorije.size == 1 -> {
                    val jedinaKat = kategorije.first()
                    newsDAO.getTopStoriesByCategory(jedinaKat)
                }

                kategorije.size > 1 -> {

                    val prvaKat = kategorije.first()
                    newsDAO.getTopStoriesByCategory(prvaKat)
                }
                else -> {

                    newsDAO.getAllStories()
                }
            }
        }
        onDispose { /* nema potrebe za ništa */ }
    }


    val filtriraneVijesti = remember(listaVijesti, dateRange, nepozeljneRijeci) {
        listaVijesti.filter { news ->
            val passDate = dateRange?.let { (startMillis, endMillis) ->
                val pubDateStr = news.publishedDate
                if (pubDateStr.isNullOrBlank()) {
                    false
                } else {
                    try {
                        val parsedDate: Date = sdf.parse(pubDateStr)!!
                        val time = parsedDate.time
                        time in startMillis..endMillis
                    } catch (_: Exception) {
                        false
                    }
                }
            } != false

            if (!passDate) return@filter false

            val textToCheck = buildString {
                append(news.title)
                news.snippet?.let { append(" $it") }
            }.lowercase(Locale.getDefault())

            val containsUnwanted = nepozeljneRijeci.any { word ->
                val lw = word.lowercase(Locale.getDefault())
                textToCheck.contains(lw)
            }
            if (containsUnwanted) return@filter false

            true
        }
    }


    val chipovi = listOf(
        ChipData("Više filtera ...", "filter_chip_more", "Više filtera ..."),
        ChipData("Sve",            "filter_chip_all", "Sve"),
        ChipData("Politika",       "filter_chip_pol", "Politika"),
        ChipData("Sport",          "filter_chip_spo", "Sport"),
        ChipData("Nauka",          "filter_chip_sci", "Nauka"),
        ChipData("Tehnologija",    "filter_chip_tech", "Tehnologija"),
        ChipData("Posao",          "filter_chip_biz", "Posao"),
        ChipData("Zdravlje",       "filter_chip_health", "Zdravlje"),
        ChipData("Zabava",         "filter_chip_ent", "Zabava"),
        ChipData("Hrana",          "filter_chip_food", "Hrana"),
        ChipData("Putovanja",      "filter_chip_travel", "Putovanja")
    )

    val bojachipa    = if (isSystemInDarkTheme()) Color(0xFF312D2D) else Color(0xFF4C60B6)
    val bojapozadine = if (isSystemInDarkTheme()) Color(0xFF3E3838) else Color(0xFF9CAEEE)

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

                                onKategorijeUpdate(setOf(chip.kategorija))
                            }
                        },
                        label = {
                            Text(
                                chip.prikaz,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        },
                        leadingIcon = {
                            if (selected)
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                                )
                        },
                        colors = FilterChipDefaults.filterChipColors(containerColor = bojachipa),
                        modifier = Modifier.testTag(chip.tag)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (filtriraneVijesti.isNotEmpty()) {

                NewsList(filtriraneVijesti, navController)
            } else {
                MessageCard("Nema vijesti za prikazane filtere.")
            }
        }
    }
}
