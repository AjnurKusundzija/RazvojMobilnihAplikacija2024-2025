package etf.ri.rma.newsfeedapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import etf.ri.rma.newsfeedapp.data.NewsDatabase
import etf.ri.rma.newsfeedapp.data.NewsRepository                                   // <-- repo
                         // <-- db
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.screen.FilterScreen
import etf.ri.rma.newsfeedapp.screen.NewsDetailsScreen
import etf.ri.rma.newsfeedapp.screen.NewsFeedScreen

@Composable
fun NewsFeedAppNavigation() {
    val navController = rememberNavController()

    // lokalni state za filtere
    var kategorije by remember { mutableStateOf(setOf("Sve")) }
    var dateRange by remember { mutableStateOf<Pair<Long, Long>?>(null) }
    var nepozeljneRijeci by remember { mutableStateOf(listOf<String>()) }

    // --- instanciraj Room bazu i repozitorij ---
    val context = LocalContext.current
    val db = remember { NewsDatabase.getDatabase(context) }
    val repository = remember { NewsRepository(db) }

    // --- proslijedi repo u DAO-e ---
    val newsDAO = remember { NewsDAO(repository) }      // sada prima repository
    val imagaDAO = remember { ImagaDAO(repository) }    // sada prima repository

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            NewsFeedScreen(
                navController   = navController,
                newsDAO         = newsDAO,
                kategorije      = kategorije,
                dateRange       = dateRange,
                nepozeljneRijeci= nepozeljneRijeci,
                onKategorijeUpdate = { newKat ->
                    kategorije = newKat
                }
            )
        }

        composable("filters") {
            FilterScreen(
                sel_kategorije = kategorije,
                RasponDatuma   = dateRange,
                NepozeljneRijeci = nepozeljneRijeci,
                onApply = { newKat, newDate, newWords ->
                    kategorije       = newKat
                    dateRange        = newDate
                    nepozeljneRijeci = newWords
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "details/{uuid}",
            arguments = listOf(navArgument("uuid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uuid = backStackEntry.arguments!!.getString("uuid")!!
            NewsDetailsScreen(
                newsId       = uuid,
                navController= navController,
                newsDAO      = newsDAO,
                imagaDAO     = imagaDAO,
                onBack       = { navController.popBackStack() }
            )
        }
    }
}
