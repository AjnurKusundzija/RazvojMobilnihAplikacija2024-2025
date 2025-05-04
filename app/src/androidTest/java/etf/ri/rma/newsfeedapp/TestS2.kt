package etf.ri.rma.newsfeedapp

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import etf.ri.rma.newsfeedapp.data.NewsData
import etf.ri.rma.newsfeedapp.model.NewsItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.test.assertFailsWith


@RunWith(AndroidJUnit4::class)
class TestS2 {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun startMainGoToFilters(){
        composeTestRule.onNodeWithTag("filter_daterange_display").assertDoesNotExist()
        composeTestRule.onNodeWithTag("filter_chip_more").performClick()
        composeTestRule.onNodeWithTag("filter_daterange_display").assertExists()

    }

    @Test
    fun politikaFilterSelectedAtStartShouldBeSelectedInFiltersScreen(){
        composeTestRule.onNodeWithTag("filter_chip_pol").performClick()
        composeTestRule.onNodeWithTag("filter_chip_more").performClick()
        composeTestRule.onNodeWithTag("filter_daterange_display").assertExists()
        composeTestRule.onNodeWithTag("filter_chip_pol").assertIsSelected()
    }

    @Test
    fun politikaStartFilterMoreSportShouldBeSportWhenReturneToNewsList(){
        composeTestRule.onNodeWithTag("filter_chip_pol").performClick()
        composeTestRule.onNodeWithTag("filter_chip_more").performClick()
        composeTestRule.onNodeWithTag("filter_daterange_display").assertExists()
        composeTestRule.onNodeWithTag("filter_chip_pol").assertIsSelected()
        composeTestRule.onNodeWithTag("filter_chip_spo").performClick()
        composeTestRule.onNodeWithTag("filter_apply_button").performClick()
        composeTestRule.onNodeWithTag("filter_daterange_display").assertDoesNotExist()
        composeTestRule.onNodeWithTag("filter_chip_pol").assertIsNotSelected()
        composeTestRule.onNodeWithTag("filter_chip_spo").assertIsSelected()
    }

    @Test
    fun filterOutWordFromThirdNews(){
        //provjera filtriranja rijeci iz novosti
        val rijec = NewsData.getAllNews().get(2).title.split(" ").find { it -> it.length>3}
        val rijec2 = NewsData.getAllNews().get(2).title.split(" ").first()
        var listnode = composeTestRule.onNodeWithTag("news_list")
        listnode.performScrollToNode(hasText(NewsData.getAllNews().get(2).title)).assertExists()
        composeTestRule.onNodeWithTag("filter_chip_more").performClick()
        composeTestRule.onNodeWithTag("filter_unwanted_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("filter_unwanted_input").performTextInput(rijec?:rijec2)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("filter_unwanted_add_button").performClick()
        Espresso.closeSoftKeyboard()
        composeTestRule.onNodeWithTag("filter_unwanted_list").performScrollToNode(hasText(rijec?:rijec2)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("filter_apply_button").performClick()
        listnode = composeTestRule.onNodeWithTag("news_list")
        assertFailsWith<AssertionError>{listnode.performScrollToNode(hasText(NewsData.getAllNews().get(2).title))}
    }

    @Test
    fun detaljiVijesti(){
        //provjera ispravnog prikaza detalja vijesti
        val news = NewsData.getAllNews().get(2)
        var listnode = composeTestRule.onNodeWithTag("news_list")
        listnode.performScrollToNode(hasText(news.title)).performClick()
        composeTestRule.onNodeWithTag("details_title").assert(hasText(news.title))
        composeTestRule.onNodeWithTag("details_snippet").assert(hasText(news.snippet))
        composeTestRule.onNodeWithTag("details_source").assert(hasText(news.source,substring = true))
        composeTestRule.onNodeWithTag("details_date").assert(hasText(news.publishedDate,substring = true))
        Espresso.pressBack()
        listnode.performScrollToNode(hasText(news.title)).assertIsDisplayed()
    }

    @Test
    fun detaljiVijestiNajblizeVijesti(){
        //provjera za najblize vijesti
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        var newsId=-1
        var news:NewsItem
        var newsFromSameCategory:List<NewsItem>
        do {
            newsId++
            news = NewsData.getAllNews().get(newsId)
            newsFromSameCategory = NewsData.getAllNews().filter { it->it.category==news.category}
        }while (newsFromSameCategory.size<2)
        newsFromSameCategory = newsFromSameCategory.sortedBy{it.title}
        newsFromSameCategory = newsFromSameCategory.sortedBy { it->Math.abs(ChronoUnit.DAYS.between(LocalDate.parse(it.publishedDate,formatter),LocalDate.parse(news.publishedDate,formatter))) }

        var listnode = composeTestRule.onNodeWithTag("news_list")
        listnode.performScrollToNode(hasText(news.title))
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(news.title).assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("details_title").assertExists()
        composeTestRule.onNodeWithTag("details_title").assert(hasText(news.title))
        composeTestRule.onNodeWithTag("details_snippet").assert(hasText(news.snippet))
        composeTestRule.onNodeWithTag("details_source").assert(hasText(news.source,substring = true))
        composeTestRule.onNodeWithTag("details_date").assert(hasText(news.publishedDate,substring = true))
        composeTestRule.onNodeWithTag("related_news_title_1").assert(hasText(newsFromSameCategory.get(1).title))
        composeTestRule.onNodeWithTag("related_news_title_2").assert(hasText(newsFromSameCategory.get(2).title))
    }

    @Test
    fun detaljiVijestiNajblizeVijestiBack(){
        //test otvara prvu vijest, otvara nakon toga prvu najblizu vijest i pritisne sistemsko dugme back
        var listnode = composeTestRule.onNodeWithTag("news_list")
        val naslov=NewsData.getAllNews().get(0).title;
        listnode.performScrollToNode(hasText(naslov))
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(naslov).assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("related_news_title_1").performClick()
        composeTestRule.waitForIdle()
        Espresso.pressBack()
        composeTestRule.waitForIdle()
        //Treba se vratiti na pocetni screen
        composeTestRule.onNodeWithTag("news_list").assertExists()
    }
}