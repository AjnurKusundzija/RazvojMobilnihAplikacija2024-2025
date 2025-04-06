import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.model.NewsItem
import etf.ri.rma.newsfeedapp.screen.NewsList

@Composable
fun FilterChipComponent(
    selectedCategory: String,
    onClick: (String) -> Unit,
    assignedCategory: String,
    tag: String
) {
    FilterChip(
        onClick = { onClick(assignedCategory) },
        label = { Text(assignedCategory) },
        modifier = Modifier.testTag(tag),
        selected = selectedCategory == assignedCategory,
        leadingIcon = if (selectedCategory == assignedCategory) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}

// Kotlin
@Composable
fun NewsFeedScreen() {
    val newsItems by remember { mutableStateOf(NewsData.getAllNews()) }
    var filteredNews by remember { mutableStateOf(newsItems) }
    var selectedCategory by remember { mutableStateOf("Sve") }

    fun changeCategory(newCategory: String) {
        selectedCategory = newCategory
        filteredNews = when (newCategory) {
            "All" -> newsItems
            else -> newsItems.filter { it.category == newCategory }
        }
    }

    Column(
        modifier = Modifier.padding(top = 28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 0.dp), // Reduced vertical padding
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChipComponent(
                assignedCategory = "Politika",
                selectedCategory = selectedCategory,
                onClick = { category -> changeCategory(category) },
                tag = "filter_chip_pol"
            )
            FilterChipComponent(
                assignedCategory = "Sport",
                selectedCategory = selectedCategory,
                onClick = { category -> changeCategory(category) },
                tag = "filter_chip_spo"
            )
            FilterChipComponent(
                assignedCategory = "Nauka/Tehnologija",
                selectedCategory = selectedCategory,
                onClick = { category -> changeCategory(category) },
                tag = "filter_chip_sci"
            )
            FilterChipComponent(
                assignedCategory = "Sve",
                selectedCategory = selectedCategory,
                onClick = { category -> changeCategory(category) },
                tag = "filter_chip_all"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChipComponent(
                assignedCategory = "Zdravlje",
                selectedCategory = selectedCategory,
                onClick = { category -> changeCategory(category) },
                tag = "filter_chip_zdravlje"
            )
        }

        if (filteredNews.isNotEmpty()) {
            NewsList(filteredNews)
        } else {
            MessageCard(selectedCategory)
        }
    }
}