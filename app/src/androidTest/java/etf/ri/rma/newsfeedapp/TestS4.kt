package etf.ri.rma.newsfeedapp

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4

import etf.ri.rma.newsfeedapp.data.SavedNewsDAO
import etf.ri.rma.newsfeedapp.data.NewsDatabase
import etf.ri.rma.newsfeedapp.model.NewsItem
//svi entiteti neka se nalaze sa istim imenom kao tabela u paketu model

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.UUID
import kotlin.test.assertContains


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class TestS4 {
    private val countNews = "SELECT COUNT(*) AS broj_novosti FROM News"
    private val getByTitle = "SELECT id FROM News WHERE title = ? LIMIT 1"
    private val countTags = "SELECT COUNT(*) AS broj_tagova FROM Tags"
    private val countNewsTags = "SELECT COUNT(*) AS broj_novosttagova FROM NewsTags"
    private val describeNews = "pragma table_info('News')"
    private val describeTags = "pragma table_info('Tags')"
    private val describeNewsTags = "pragma table_info('NewsTags')"
    //data


    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    companion object {
        var news1 = NewsItem(uuid=UUID.randomUUID().toString(), title = "Naslova T0", snippet = "snpt", category = "politics", imageUrl = "", isFeatured = true, publishedDate = "01-01-2025", source = "izvor")
        var news2 = NewsItem(uuid=UUID.randomUUID().toString(), title = "Naslova 2", snippet = "snpt 2", category = "sport", imageUrl = "", isFeatured = false, publishedDate = "01-02-2025", source = "izvor 2")
        lateinit var roomDb: NewsDatabase
        lateinit var savedNewsDAO: SavedNewsDAO
        lateinit var db: SupportSQLiteDatabase

        @JvmStatic
        @BeforeClass
        fun postaviBazu(): Unit = runTest {
            roomDb = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                NewsDatabase::class.java
            ).build()
            savedNewsDAO = roomDb.savedNewsDAO()
            savedNewsDAO.allNews()
            db = roomDb.openHelper.readableDatabase
        }
    }
    private val kolone = mapOf(
        "Tags" to arrayListOf("id", "value"),
        "NewsTags" to arrayListOf("id", "newsId", "tagsId"),
        "News" to arrayListOf(
            "id",
            "uuid",
            "title",
            "category",
            "source",
            "publishedDate"
        )
    )

    private fun executeCountAndCheck(query: String, column: String, value: Long) {
        var rezultat = db.query(query)
        rezultat.moveToFirst()
        var brojOdgovora = rezultat.getLong(0)
        assertThat(brojOdgovora, `is`(equalTo(value)))
    }

    private fun getLastID(title:String):Long{
        val stmt = db.compileStatement(getByTitle)
        stmt.bindString(1,title)
        var rezultat = stmt.simpleQueryForLong()
        return rezultat
    }

    private fun checkColumns(query: String, naziv: String) {
        var rezultat = db.query(query)
        val list = (1..rezultat.count).map {
            rezultat.moveToNext()
            rezultat.getString(1)
        }
        assertThat(list, hasItems(*kolone[naziv]!!.toArray()))
    }

    @Test
    fun a00_tableNewsHasAllColumns() = runTest {
        checkColumns(describeNews, "News")
    }

    @Test
    fun a01_tableTagsHasAllColumns() = runTest {
        checkColumns(describeTags, "Tags")
    }

    @Test
    fun a02_insertNews() = runTest{
        executeCountAndCheck(countNews,"broj_novosti",0)
        val saveResult = savedNewsDAO.saveNews(news1)
        executeCountAndCheck(countNews,"broj_novosti",1)
        assertEquals(true, saveResult)
    }

    @Test
    fun a03_allNews() = runTest {
        executeCountAndCheck(countNews,"broj_novosti",1)
        var allnews = savedNewsDAO.allNews()
        assertThat(allnews.size, `is`(equalTo(1)))
    }

    @Test
    fun a04_insertAnotherAndCheckSize() = runTest{
        val saveResult = savedNewsDAO.saveNews(news2)
        assertEquals(true, saveResult)
        executeCountAndCheck(countNews,"broj_novosti",2)
        var allnews = savedNewsDAO.allNews()
        assertThat(allnews.size, `is`(equalTo(2)))
    }

    @Test
    fun a05_insertDuplicate() = runTest {
        val saveResult = savedNewsDAO.saveNews(news2)
        assertEquals(false, saveResult)
    }

    @Test
    fun a06_getNewsWithCategory1() = runTest {
        val newsPolitics = savedNewsDAO.getNewsWithCategory("politics")
        assertEquals(1,newsPolitics.size)
    }

    @Test
    fun a07_getNewsWithCategoryAll() = runTest {
        val newsPolitics = savedNewsDAO.getNewsWithCategory("politics")
        assertEquals(1,newsPolitics.size)
        val newsSport= savedNewsDAO.getNewsWithCategory("sport")
        assertEquals(1,newsSport.size)
        val newsGeneral= savedNewsDAO.getNewsWithCategory("general")
        assertEquals(0,newsGeneral.size)
    }

    @Test
    fun a08_addTagsToNews1() = runTest{
        savedNewsDAO.addTags(listOf("prva","probna"),getLastID(news1.title).toInt())
        executeCountAndCheck(countTags,"broj_tagova",2)
        executeCountAndCheck(countNewsTags,"broj_novosttagova",2)
    }

    @Test
    fun a09_addTagsToNews2() = runTest {
        savedNewsDAO.addTags(listOf("druga","probna"),getLastID(news2.title).toInt())
        executeCountAndCheck(countTags,"broj_tagova",3)
        executeCountAndCheck(countNewsTags,"broj_novosttagova",4)
    }

    @Test
    fun a10_getTags() = runTest{
        var tags = savedNewsDAO.getTags(getLastID(news1.title).toInt())
        assertContains(tags,"prva")
        assertContains(tags,"probna")
    }

    @Test
    fun a11_allNewsGetsTags() = runTest {
        var allnews = savedNewsDAO.allNews().find { it.imageTags.find { it.value=="prva"}!=null}
        assertThat(allnews, `is`(notNullValue()))
        assertEquals(2,allnews!!.imageTags.size)
        assertThat(allnews.imageTags, not(hasItems(hasText("prva"))))
    }

    @Test
    fun a12_getSimilar() = runTest {
        var similar = savedNewsDAO.getSimilarNews(listOf("prva"))
        assertEquals(1,similar.size)
        assertEquals("Naslova T0",similar[0].title)
    }

    @Test
    fun a13_getSimilar2() = runTest {
        var similar = savedNewsDAO.getSimilarNews(listOf("druga"))
        assertEquals(1,similar.size)
        assertEquals("Naslova 2",similar[0].title)
    }

}