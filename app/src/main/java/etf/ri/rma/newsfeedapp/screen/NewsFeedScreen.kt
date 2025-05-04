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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import etf.ri.rma.newsfeedapp.data.ChipData
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.ui.theme.NewsFeedAppTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewsFeedScreen(
    navController: NavHostController,
    kategorije: Set<String>,
    dateRange: Pair<Long, Long>?,
    nepozeljneRijeci: List<String>,
    onKategorijeUpdate: (Set<String>) -> Unit
) {
    val sveVijesti = NewsData.getAllNews()

    val filtriraneVijesti = sveVijesti.filter { news ->
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val parsedDate = try {
            sdf.parse(news.publishedDate)
        } catch (e: Exception) {
            null
        }
        val newsDate = parsedDate?.time

        val katOk = kategorije.contains("Sve") || kategorije.contains(news.category)
        val datumOk =
            dateRange?.let { newsDate?.let { date -> date in it.first..it.second } } ?: true
        val rijeciOk = nepozeljneRijeci.none {
            news.title.contains(it, ignoreCase = true) || news.snippet.contains(it, ignoreCase = true)
        }


        katOk && datumOk && rijeciOk
    }
    val bojachipa = if (isSystemInDarkTheme()) {
        Color(0xFF312D2D)
    } else {
        Color(0xFF4C60B6)
    }
    val bojapozadine = if (isSystemInDarkTheme()) {
        Color(0xFF3E3838)
    } else {
        Color(0xFF9CAEEE)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bojapozadine
    ) {

        Column {
            Spacer(modifier = Modifier.height(18.dp).width(5.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val chipovi = listOf(
                    ChipData("Više filtera ...", "filter_chip_more", "Više filtera ..."),
                    ChipData("Sve", "filter_chip_all", "Sve"),
                    ChipData("Politika", "filter_chip_pol", "Politika"),
                    ChipData("Sport", "filter_chip_spo", "Sport"),
                    ChipData("Nauka/Tehnologija", "filter_chip_sci", "Nauka/Tehnologija"),
                    ChipData("Zdravlje", "filter_chip_none", "Zdravlje"),


                    )
                items(chipovi) { chip ->
                    val selected = when (chip.kategorija) {
                        "Sve" -> kategorije.contains("Sve")
                        else -> kategorije.contains(chip.kategorija)
                    }

                    FilterChip(
                        selected = selected,
                        onClick = {
                            if (chip.tag == "filter_chip_more") {
                                navController.navigate("filters")
                            } else {
                                val azuriran = when {
                                    chip.kategorija == "Sve" -> setOf("Sve")
                                    kategorije.contains(chip.kategorija) -> {
                                        val nova = kategorije - chip.kategorija
                                        if (nova.isEmpty()) setOf("Sve") else nova
                                    }

                                    else -> {
                                        val opc = kategorije - "Sve"
                                        val nova = opc + chip.kategorija
                                        if (nova.size == chipovi.count { it.kategorija != "Sve" && it.kategorija != "Više filtera ..." }) setOf(
                                            "Sve"
                                        )
                                        else nova
                                    }
                                }
                                onKategorijeUpdate(azuriran)
                            }
                        },
                        label = {
                            Text(
                                text = chip.prikaz,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        },
                        leadingIcon = {
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "selektovan",
                                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(containerColor = bojachipa),
                        modifier = Modifier.testTag(chip.tag)
                    )
                }
            }
            if (filtriraneVijesti.isNotEmpty()) {
                NewsList(filtriraneVijesti, navController)
            } else {
                MessageCard("Nema vijesti za prikazane filtere.")
            }


        }

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewNewsFeedScreen() {
    NewsFeedAppTheme {
        NewsFeedScreen(
            navController = rememberNavController(),
            kategorije = setOf("Sve"),
            dateRange = null,
            nepozeljneRijeci = emptyList(),
            onKategorijeUpdate = {}
        )
    }
}


