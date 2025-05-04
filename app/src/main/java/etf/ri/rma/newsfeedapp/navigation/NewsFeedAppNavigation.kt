package etf.ri.rma.newsfeedapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import etf.ri.rma.newsfeedapp.screen.FilterScreen
import etf.ri.rma.newsfeedapp.screen.NewsDetailsScreen
import etf.ri.rma.newsfeedapp.screen.NewsFeedScreen

@Composable
fun NewsFeedAppNavigation() {
    val navController = rememberNavController()

    var kategorije by remember { mutableStateOf(setOf("Sve")) }
    var dateRange by remember { mutableStateOf<Pair<Long, Long>?>(null) }
    var nepozeljneRijeci by remember { mutableStateOf(listOf<String>()) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            NewsFeedScreen(
                navController = navController,
                kategorije = kategorije,
                dateRange = dateRange,
                nepozeljneRijeci = nepozeljneRijeci,
                onKategorijeUpdate = { kategorije = it }
            )
        }
        composable("filters") {
            FilterScreen(
                sel_kategorije = kategorije,
                RasponDatuma = dateRange,
                NepozeljneRijeci = nepozeljneRijeci,
                onApply = { newKat, newDate, newWords ->
                    kategorije = newKat
                    dateRange = newDate
                    nepozeljneRijeci = newWords
                    navController.popBackStack()
                }
            )
        }
        composable("details/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            id?.let {
                NewsDetailsScreen(
                    newsId = it, navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
