

package etf.ri.rma.newsfeedapp.screen
import MessageCard
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.data.ChipData
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.ui.theme.NewsFeedAppTheme


@Composable
fun FilterChipComponent(
    isSelected: Boolean,
    onClick: () -> Unit,
    labelText: String,
    testTag: String,
    colors: SelectableChipColors
) {
    val kvakica= if (isSystemInDarkTheme()) Color.White else Color.Black
    val textboja = if (isSystemInDarkTheme()) Color.White else Color.Black

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = labelText,
                style = MaterialTheme.typography.bodyMedium.copy(color = textboja)
            )
        },
        modifier = Modifier.testTag(testTag),
        colors = colors,
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    tint = kvakica,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else null
    )
}

@Composable
fun NewsFeedScreen() {
    val allNews by remember { mutableStateOf(NewsData.getAllNews()) }
    var kategorije by remember { mutableStateOf(setOf("Sve")) }

    val bojachipa = FilterChipDefaults.filterChipColors(
        containerColor = if (isSystemInDarkTheme()) Color(0xFF534B4B) else Color(0xFF798DDC),
        selectedContainerColor = if (isSystemInDarkTheme()) Color(0xFF3E3A3A) else Color(0xFF42608a)
    )

    val bojapozadine = if (isSystemInDarkTheme()) Color(0xFF0F101B) else Color(0xFFBDBFCB)

    val chipovi = listOf(
        ChipData("Sve", "filter_chip_all", "Sve"),
        ChipData("Politika", "filter_chip_pol", "Politika"),
        ChipData("Sport", "filter_chip_spo", "Sport"),
        ChipData("Nauka/Tehnologija", "filter_chip_sci", "Nauka/Tehnologija"),
        ChipData("Zdravlje", "filter_chip_none", "Zdravlje")
    )

    val filteredNews = if (kategorije.contains("Sve")) {
        allNews
    } else {
        allNews.filter { it.category in kategorije }
    }

    fun onCategoryClick(category: String) {
        kategorije = when {
            category == "Sve" -> setOf("Sve")//pocetno stanje
            kategorije.contains(category) -> {
                val azuriran= kategorije - category
                if (azuriran.isEmpty()) setOf("Sve") else azuriran//test za zdravlje
            }
            else -> (kategorije - "Sve") + category
        }
    }

    Surface(
        color = bojapozadine,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(top = 28.dp)) {
            Spacer(modifier = Modifier.padding(vertical = 6.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 3.5.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(chipovi) { chip ->
                    FilterChipComponent(
                        isSelected = kategorije.contains(chip.kategorija),//ako je odabrana ta kategorije
                        onClick = { onCategoryClick(chip.kategorija) },
                        labelText = chip.kategorija,
                        testTag = chip.tag,
                        colors = bojachipa
                    )
                }
            }

            if (filteredNews.isNotEmpty()) {
                NewsList(filteredNews)
            } else {
                MessageCard(kategorije.joinToString())
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewNewsFeedScreen() {
    NewsFeedAppTheme {
            NewsFeedScreen()
        }
    }

