package etf.ri.rma.newsfeedapp.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.model.NewsItem

import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewsDetailsScreen(newsId: String, navController: NavController, onBack: () -> Unit) {
    val bojapozadine = if (isSystemInDarkTheme()) Color(0xFF3E3838) else Color(0xFF798DDC)




    val naslovboja = if (isSystemInDarkTheme()) Color(0xFF797272) else Color(0xFF9191B6)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bojapozadine
    ) {
        val sveVijesti = NewsData.getAllNews()
        val vijest = sveVijesti.find { it.id == newsId } ?: return@Surface



        val format_dat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val datumTrenutne = format_dat.parse(vijest.publishedDate)?.time ?: 0L


        val ista_kategorija = sveVijesti.filter { it.category == vijest.category && it.id != vijest.id }


        val vijestiIsteKategorije: MutableList<NewsItem> = ista_kategorija.toMutableList()
        for (i in 0 until vijestiIsteKategorije.size - 1) {
            for (j in i + 1 until vijestiIsteKategorije.size) {
                val tp = format_dat.parse(vijestiIsteKategorije[i].publishedDate)?.time ?: 0L
                val tk = format_dat.parse(vijestiIsteKategorije[j].publishedDate)?.time ?: 0L
                val raz1 = kotlin.math.abs(datumTrenutne - tp)
                val raz2 = kotlin.math.abs(datumTrenutne - tk)

                if (raz2 < raz1 || (raz2 == raz1 && vijestiIsteKategorije[j].title < vijestiIsteKategorije[i].title)) {
                    val temp = vijestiIsteKategorije[i]
                    vijestiIsteKategorije[i] = vijestiIsteKategorije[j]
                    vijestiIsteKategorije[j] = temp
                }
            }
        }

        val povezana1 = vijestiIsteKategorije.getOrNull(0)
        val povezana2 = vijestiIsteKategorije.getOrNull(1)

        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = naslovboja)
            ) {
                Text(
                    text = "Detalji vijesti",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = vijest.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .testTag("details_title")
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(15.dp))


            Text(
                text = "Sažetak:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))


            Text(
                text = vijest.snippet,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .testTag("details_snippet")
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Kategorija: ${vijest.category}",
                modifier = Modifier
                    .testTag("details_category")
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Izvor: ${vijest.source}",
                modifier = Modifier
                    .testTag("details_source")
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Datum: ${vijest.publishedDate}",
                modifier = Modifier
                    .testTag("details_date")
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "Povezane vijesti iz iste kategorije:",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (povezana1 != null) {
                Text(
                    text = povezana1.title,
                    modifier = Modifier
                        .clickable {navController.popBackStack()
                            navController.navigate("details/${povezana1.id}") }
                        .testTag("related_news_title_1")
                        .padding(vertical = 4.dp)
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (povezana2 != null) {
                Text(
                    text = povezana2.title,
                    modifier = Modifier
                        .clickable { navController.popBackStack()
                            navController.navigate("details/${povezana2.id}") }
                        .testTag("related_news_title_2")
                        .padding(vertical = 4.dp)
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) Color(0xFF1A1717) else Color(0xFF455180),
                    contentColor = if (isSystemInDarkTheme()) Color(0xFFFFFFFF) else Color(0xFF000000)
                ),
                onClick = { navController.popBackStack("home", false) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .testTag("details_close_button")
            ) {
                Text("Zatvori detalje")
            }
        }
    }
}
