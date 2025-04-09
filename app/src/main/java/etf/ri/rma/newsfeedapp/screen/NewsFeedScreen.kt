import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
<<<<<<< HEAD
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
=======
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
>>>>>>> e2cb5af (Spirala1)
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
<<<<<<< HEAD
=======
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
>>>>>>> e2cb5af (Spirala1)
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
<<<<<<< HEAD
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.screen.NewsList


=======
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.data.ChipData
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.screen.NewsList

>>>>>>> e2cb5af (Spirala1)
@Composable
fun FilterChipComponent(
    selectedCategory: String,
    onClick: (String) -> Unit,
    assignedCategory: String,
<<<<<<< HEAD
    tag: String,
    colors: SelectableChipColors
) {
    val tintboja = if (isSystemInDarkTheme()) Color.White else Color.Black
    val bojatekstachip= if (isSystemInDarkTheme()) Color.White else Color.Black
    FilterChip(
        onClick = { onClick(assignedCategory) },
        label = {Text(
            text = assignedCategory,
            style = MaterialTheme.typography.bodyMedium.copy(color = bojatekstachip)
        )  },
        modifier = Modifier.testTag(tag),
=======
    testTag: String,
    colors: SelectableChipColors
) {
    val tintboja = if (isSystemInDarkTheme()) Color.White else Color.Black
    val bojatekstachip = if (isSystemInDarkTheme()) Color.White else Color.Black
    FilterChip(
        onClick = { onClick(assignedCategory) },
        label = {
            Text(
                text = assignedCategory,
                style = MaterialTheme.typography.bodyMedium.copy(color = bojatekstachip)
            )
        },
        modifier = Modifier.testTag(testTag),
>>>>>>> e2cb5af (Spirala1)
        selected = selectedCategory == assignedCategory,
        colors = colors,
        leadingIcon = if (selectedCategory == assignedCategory) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
<<<<<<< HEAD
                    tint= tintboja,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}


=======
                    tint = tintboja,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else null
    )
}

>>>>>>> e2cb5af (Spirala1)
@Composable
fun NewsFeedScreen() {
    val newsItems by remember { mutableStateOf(NewsData.getAllNews()) }
    var filteredNews by remember { mutableStateOf(newsItems) }
<<<<<<< HEAD
    var selectedCategory by remember { mutableStateOf("All") }

    fun changeCategory(newCategory: String) {
        selectedCategory = newCategory
        filteredNews = when (newCategory) {
            "All" -> newsItems
            else -> newsItems.filter { it.category == newCategory }
=======
    var selectedCategory by remember { mutableStateOf("Sve") }

    fun changeCategory(newCategory: String) {
        selectedCategory = newCategory
        filteredNews = if (newCategory == "Sve") {
            newsItems
        } else {
            newsItems.filter { it.category == newCategory }
>>>>>>> e2cb5af (Spirala1)
        }
    }

    val chipboja = if (isSystemInDarkTheme()) {
        Color(0xFF534B4B)
    } else {
        Color(0xFF798DDC)
    }

    val selektovanchip = if (isSystemInDarkTheme()) {
        Color(0xFF3E3A3A)
    } else {
        Color(0xFF42608a)
    }

<<<<<<< HEAD

=======
>>>>>>> e2cb5af (Spirala1)
    val chip_boja = FilterChipDefaults.filterChipColors(
        containerColor = chipboja,
        selectedContainerColor = selektovanchip
    )

    val pozadina = if (isSystemInDarkTheme()) Color(0xFF0F101B) else Color(0xFFBDBFCB)

<<<<<<< HEAD

    Surface(
        color = pozadina,
        modifier = Modifier.fillMaxSize()
    )
    {
        Column(
            modifier = Modifier.padding(top = 28.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChipComponent(
                    assignedCategory = "Sve",
                    selectedCategory = selectedCategory,
                    onClick = { changeCategory(it) },
                    tag = "filter_chip_all",
                    colors = chip_boja
                )
                FilterChipComponent(
                    assignedCategory = "Politika",
                    selectedCategory = selectedCategory,
                    onClick = { changeCategory(it) },
                    tag = "filter_chip_pol",
                    colors = chip_boja
                )
                FilterChipComponent(
                    assignedCategory = "Sport",
                    selectedCategory = selectedCategory,
                    onClick = { changeCategory(it) },
                    tag = "filter_chip_spo",
                    colors = chip_boja
                )
                FilterChipComponent(
                    assignedCategory = "Nauka/Tehnologija",
                    selectedCategory = selectedCategory,
                    onClick = { changeCategory(it) },
                    tag = "filter_chip_sci",
                    colors = chip_boja
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChipComponent(
                    assignedCategory = "Zdravlje",
                    selectedCategory = selectedCategory,
                    onClick = { changeCategory(it) },
                    tag = "filter_chip_zdravlje",
                    colors = chip_boja
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (filteredNews.isNotEmpty()) {
                    NewsList(filteredNews)
                } else {
                    MessageCard(selectedCategory)
                }
            }
=======
    Surface(
        color = pozadina,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(top = 28.dp)
        ) {
            Spacer(modifier = Modifier.padding(vertical = 6.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filteri = listOf(
                    ChipData("Sve", "filter_chip_all", "Sve"),
                    ChipData("Politika", "filter_chip_pol", "Politika"),
                    ChipData("Sport", "filter_chip_spo", "Sport"),
                    ChipData("Nauka/Tehnologija", "filter_chip_sci", "Nauka/Tehnologija"),
                    ChipData("Zdravlje", "filter_chip_zdravlje", "Zdravlje")
                )
                items(filteri) { chip ->
                    FilterChipComponent(
                        assignedCategory = chip.kategorija,
                        selectedCategory = selectedCategory,
                        onClick = { changeCategory(it) },
                        testTag = chip.tag,
                        colors = chip_boja
                    )
                }
            }
            if (filteredNews.isNotEmpty()) {
                NewsList(filteredNews)
            } else {
                MessageCard(selectedCategory)
            }
>>>>>>> e2cb5af (Spirala1)
        }
    }
}