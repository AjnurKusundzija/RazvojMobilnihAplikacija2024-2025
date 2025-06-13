package etf.ri.rma.newsfeedapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import etf.ri.rma.newsfeedapp.navigation.NewsFeedAppNavigation

import etf.ri.rma.newsfeedapp.ui.theme.NewsFeedAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsFeedAppTheme {
                NewsFeedAppNavigation()
            }
        }

    }
    }







