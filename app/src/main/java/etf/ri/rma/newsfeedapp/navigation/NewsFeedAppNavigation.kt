package etf.ri.rma.newsfeedapp.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import kotlinx.coroutines.launch
import etf.ri.rma.newsfeedapp.data.NewsDatabase

import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.data.network.ImagaDAO
import etf.ri.rma.newsfeedapp.data.network.NewsDAO
import etf.ri.rma.newsfeedapp.data.network.NewsRepository
import etf.ri.rma.newsfeedapp.screen.FilterScreen
import etf.ri.rma.newsfeedapp.screen.NewsDetailsScreen
import etf.ri.rma.newsfeedapp.screen.NewsFeedScreen

@Composable
fun NewsFeedAppNavigation() {
    val navController = rememberNavController()


    var kategorije by remember { mutableStateOf(setOf("Sve")) }
    var dateRange by remember { mutableStateOf<Pair<Long, Long>?>(null) }
    var nepozeljneRijeci by remember { mutableStateOf(listOf<String>()) }


    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context,
            NewsDatabase::class.java,
            "news_db"
        ).build()
    }
    val savedNewsDao = remember { db.savedNewsDAO() }


    val repository = remember {
        NewsRepository(
            context      = context,
            newsDao      = NewsDAO(),
            imaggaDao    = ImagaDAO(),
            savedNewsDao = savedNewsDao
        )
    }


    LaunchedEffect(Unit) {
        launch {
            NewsData.newsItems.forEach { item ->
                savedNewsDao.saveNews(item)
            }
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            NewsFeedScreen(
                navController       = navController,
                repository          = repository,
                kategorije          = kategorije,
                dateRange           = dateRange,
                nepozeljneRijeci    = nepozeljneRijeci,
                onKategorijeUpdate  = { newSet -> kategorije = newSet }
            )
        }
        composable("filters") {
            FilterScreen(
                sel_kategorije   = kategorije,
                RasponDatuma     = dateRange,
                NepozeljneRijeci = nepozeljneRijeci,
                onApply          = { newSet, newRange, newWords ->
                    kategorije       = newSet
                    dateRange        = newRange
                    nepozeljneRijeci = newWords
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "details/{uuid}",
            arguments = listOf(navArgument("uuid") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments!!.getString("uuid")!!
            NewsDetailsScreen(
                newsId       = id,
                navController= navController,
                repository   = repository,
                onBack       = { navController.popBackStack() }
            )
        }
    }
}
