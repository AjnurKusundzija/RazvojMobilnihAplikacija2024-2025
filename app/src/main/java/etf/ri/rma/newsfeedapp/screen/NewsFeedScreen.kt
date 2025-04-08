import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.screen.NewsList


@Composable
fun FilterChipComponent(
    selectedCategory: String,
    onClick: (String) -> Unit,
    assignedCategory: String,
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
        selected = selectedCategory == assignedCategory,
        colors = colors,
        leadingIcon = if (selectedCategory == assignedCategory) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    tint= tintboja,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}


@Composable
fun NewsFeedScreen() {
    val newsItems by remember { mutableStateOf(NewsData.getAllNews()) }
    var filteredNews by remember { mutableStateOf(newsItems) }
    var selectedCategory by remember { mutableStateOf("All") }

    fun changeCategory(newCategory: String) {
        selectedCategory = newCategory
        filteredNews = when (newCategory) {
            "All" -> newsItems
            else -> newsItems.filter { it.category == newCategory }
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


    val chip_boja = FilterChipDefaults.filterChipColors(
        containerColor = chipboja,
        selectedContainerColor = selektovanchip
    )

    val pozadina = if (isSystemInDarkTheme()) Color(0xFF0F101B) else Color(0xFFBDBFCB)


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
        }
    }
}