package etf.ri.rma.newsfeedapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidImageURLException
import etf.ri.rma.newsfeedapp.data.network.exception.InvalidUUIDException
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class TestS3 {
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var clientCertificates: HandshakeCertificates

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    private lateinit var localCertificate: HeldCertificate
    private lateinit var serverCertificate: HandshakeCertificates

    @Before
    fun postaviCertifikate() {
        localCertificate = HeldCertificate.Builder()
            .commonName("localhost")
            .addSubjectAlternativeName("localhost")
            .build()

        serverCertificate = HandshakeCertificates.Builder()
            .heldCertificate(localCertificate)
            .build()

        clientCertificates = HandshakeCertificates.Builder()
            .addTrustedCertificate(localCertificate.certificate)
            .build()

        okHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(
                clientCertificates.sslSocketFactory(),
                clientCertificates.trustManager
            )
            .build()
    }

    @Test
    fun getTopNewsPolitika() = runTest(timeout = 20.seconds) {
        val server = MockWebServer()
        server.useHttps(serverCertificate.sslSocketFactory(), false)
        server.start()
        server.enqueue(MockResponse().setResponseCode(200).setBody(TestS3Data.getTop3V1()))
        val newsDAO = TestS3PripremljenRetrofit().getNewsDAOwithBaseURL(
            server.url("/").toString(),
            okHttpClient
        )
        newsDAO.getTopStoriesByCategory("politics")
        val request = server.takeRequest(10, TimeUnit.SECONDS)
        assertTrue(request != null)
        assertTrue(
            request.requestLine.contains("GET", ignoreCase = true),
            message = "Zahtjev treba biti upucen GET metodom"
        )
        assertTrue(
            request.requestLine.contains("categories=politics", ignoreCase = true),
            message = "Zahtjev treba sadrzavati parametar categoris sa ispravnom vrijednosti"
        )
        assertTrue(
            request.requestLine.contains("api_token", ignoreCase = true),
            message = "Zahtjev treba sadrzavati api kljuc"
        )
        server.shutdown()
    }

    @Test
    fun getTopNewsPolitikaDeepCompare() = runTest(timeout = 20.seconds) {
        val server = MockWebServer()
        server.useHttps(serverCertificate.sslSocketFactory(), false)
        server.start()
        server.enqueue(MockResponse().setResponseCode(200).setBody(TestS3Data.getTop3V1()))
        val newsDAO = TestS3PripremljenRetrofit().getNewsDAOwithBaseURL(
            server.url("/").toString(),
            okHttpClient
        )
        val lista = newsDAO.getTopStoriesByCategory("politics")
        assertTrue(lista.size == 3, message = "Lista treba imati tacno 3 vrijednosti")
        assertTrue(
            lista.find { el ->
                el.title.contains(
                    "No place in Congress",
                    ignoreCase = true
                )
            } != null,
            message = "U listi vijesti treba biti jedna koja u naslovu ima 'No place in Congress'"
        )
        assertTrue(
            lista.find { el ->
                el.title.contains(
                    "Italian Princess",
                    ignoreCase = true
                )
            } != null,
            message = "U listi vijesti treba biti jedna koja u naslovu ima 'Italian Princess'"
        )
        assertTrue(lista.find { el ->
            el.title.contains(
                "budget buzzer",
                ignoreCase = true
            )
        } != null, message = "U listi vijesti treba biti jedna koja u naslovu ima 'budget buzzer'")
        server.shutdown()

    }

    @Test
    fun testIfCacheingWorks() = runTest(timeout = 20.seconds) {
        val server = MockWebServer()
        server.useHttps(serverCertificate.sslSocketFactory(), false)
        server.start()
        server.enqueue(MockResponse().setResponseCode(200).setBody(TestS3Data.getTop3V1()))
        server.enqueue(MockResponse().setResponseCode(200).setBody(TestS3Data.getTop3V1()))
        val newsDAO = TestS3PripremljenRetrofit().getNewsDAOwithBaseURL(
            server.url("/").toString(),
            okHttpClient
        )
        val lista1 = newsDAO.getTopStoriesByCategory("politics")
        assertTrue(server.requestCount == 1, "Treba biti upucen 1 zahtjev")
        val lista2 = newsDAO.getTopStoriesByCategory("politics")
        assertTrue(server.requestCount == 1, "Drugi zahtjev ne treba biti upucen")
        server.shutdown()
    }

    @Test
    fun testIfCacheingWorks2() = runTest(timeout = 50.seconds) {
        val server = MockWebServer()
        server.useHttps(serverCertificate.sslSocketFactory(), false)
        server.start()
        server.enqueue(MockResponse().setResponseCode(200).setBody(TestS3Data.getTop3V1()))
        server.enqueue(MockResponse().setResponseCode(200).setBody(TestS3Data.getTop3V1()))
        val newsDAO = TestS3PripremljenRetrofit().getNewsDAOwithBaseURL(
            server.url("/").toString(),
            okHttpClient
        )
        val lista1 = newsDAO.getTopStoriesByCategory("politics")
        assertTrue(server.requestCount == 1, "Treba biti upucen 1 zahtjev")
        assertTrue(lista1.size == 3, message = "Lista treba imati tacno 3 vrijednosti")
        Thread.sleep(32_000)
        val lista2 = newsDAO.getTopStoriesByCategory("politics")
        assertTrue(lista2.size == 3, message = "Lista treba imati tacno 3 vrijednosti")
        assertTrue(server.requestCount == 2, "Drugi treba biti upucen")
        server.shutdown()
    }

    @Test
    fun testSimilarStories() = runTest {
        val server = MockWebServer()
        server.useHttps(serverCertificate.sslSocketFactory(), false)
        server.start()
        server.enqueue(MockResponse().setResponseCode(200).setBody(TestS3Data.getSimilar()))
        val newsDAO = TestS3PripremljenRetrofit().getNewsDAOwithBaseURL(
            server.url("/").toString(),
            okHttpClient
        )
        val lista = newsDAO.getSimilarStories("cc11e3ab-ced0-4a42-9146-e426505e2e67")
        val request = server.takeRequest(10, TimeUnit.SECONDS)
        assertTrue(request != null)
        assertTrue(
            request.requestLine.contains("news/similar", ignoreCase = true),
            message = "URL treba sadrzavati news/similar"
        )
        assertTrue(
            request.requestLine.contains(
                "cc11e3ab-ced0-4a42-9146-e426505e2e67",
                ignoreCase = true
            ), message = "URL treba sadrzavati uuid"
        )
        assertTrue(
            request.requestLine.contains("api_token", ignoreCase = true),
            message = "Zahtjev treba sadrzavati api kljuc"
        )
        assertTrue(lista.size == 2, message = "Lista treba sadrzavati dvije vijesti")
        assertTrue(
            lista.find { el ->
                el.uuid.contains(
                    "df4ad427-a672-4c67-b6c6-6f81aa00e164",
                    ignoreCase = true
                )
            } != null,
            message = "U listi vijesti treba biti jedna sa uuid 'df4ad427-a672-4c67-b6c6-6f81aa00e164'"
        )
        assertTrue(
            lista.find { el ->
                el.uuid.contains(
                    "c9a23881-12dd-4005-8982-7b6552a2eb50",
                    ignoreCase = true
                )
            } != null,
            message = "U listi vijesti treba biti jedna sa uuid 'c9a23881-12dd-4005-8982-7b6552a2eb50'"
        )
        server.shutdown()
    }

    @Test
    fun testSimilarSaIzuzetkom() = runTest {
        val server = MockWebServer()
        server.useHttps(serverCertificate.sslSocketFactory(), false)
        server.start()
        server.enqueue(MockResponse().setResponseCode(200).setBody(TestS3Data.getSimilar()))
        val newsDAO = TestS3PripremljenRetrofit().getNewsDAOwithBaseURL(
            server.url("/").toString(),
            okHttpClient
        )
        assertFailsWith<InvalidUUIDException> {
            val lista = newsDAO.getSimilarStories("cc11e3ab-ced0-4a42-9146e426505e2e67")
        }
        assertTrue(server.requestCount == 0, "Ne treba se poslati zahtjev ako postoji izuzetak")
        server.shutdown()
    }

    @Test
    fun testImagga() = runTest {
        val server = MockWebServer()
        server.useHttps(serverCertificate.sslSocketFactory(), false)
        server.start()
        server.enqueue(MockResponse().setResponseCode(200).setBody(TestS3Data.getTagsV1()))
        val imagga = TestS3PripremljenRetrofit().getImaggaDAOwithBaseURL(
            server.url("/").toString(),
            okHttpClient
        )

        val lista =
            imagga.getTags("https://t3.ftcdn.net/jpg/02/97/07/18/360_F_297071826_W4Jv8lgKJ338d9grf68ocN3AaNjohkZ3.jpg")
        val request = server.takeRequest(10, TimeUnit.SECONDS)
        assertTrue(request != null)
        assertTrue(
            request.requestLine.contains("v2/tags", ignoreCase = true),
            message = "URL treba sadrzavati v2/tags"
        )
        assertTrue(
            request.requestLine.contains(
                URLEncoder.encode("https://t3.ftcdn.net/jpg/02/97/07/18/360_F_297071826_W4Jv8lgKJ338d9grf68ocN3AaNjohkZ3.jpg","UTF-8"),
                ignoreCase = true
            ), message = "URL treba sadrzavati url slike"
        )
        assertTrue(
            lista.toString().contains("clouds", ignoreCase = true),
            "U tagovima treba biti tag 'clouds'"
        )
        server.shutdown()
    }
    @Test
    fun testImaggaFailed() = runTest {
        val server = MockWebServer()
        server.useHttps(serverCertificate.sslSocketFactory(), false)
        server.start()
        server.enqueue(MockResponse().setResponseCode(200).setBody(TestS3Data.getTagsV1()))
        val imagga = TestS3PripremljenRetrofit().getImaggaDAOwithBaseURL(
            server.url("/").toString(),
            okHttpClient
        )
        assertFailsWith<InvalidImageURLException> {
            val lista = imagga.getTags("url:url////url/url")
        }
        assertTrue(
            server.requestCount == 0,
            "Ne treba biti poslan nijedan zahtjev ako je url neispravan"
        )
        server.shutdown()
    }

}