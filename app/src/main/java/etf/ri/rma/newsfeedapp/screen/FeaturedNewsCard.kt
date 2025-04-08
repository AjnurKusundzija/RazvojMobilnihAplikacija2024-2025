import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.R

@Composable
fun FeaturedNewsCard(
    title: String,
    snippet: String,
    imageUrl: String?,
    source: String,
    publishedDate: String
) {
    val bojakartice = if (isSystemInDarkTheme()) {

        Color(0xFF534B4B)
    } else {

        Color(0xFF798DDC)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.5.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(containerColor = bojakartice)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.vijesti),
                contentDescription = "Featured News Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = snippet,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = source,
                    style = MaterialTheme.typography.bodySmall,

                )
                Text(
                    text = " ● ",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = publishedDate,
                    style = MaterialTheme.typography.bodySmall,

                )
            }
        }
    }
}