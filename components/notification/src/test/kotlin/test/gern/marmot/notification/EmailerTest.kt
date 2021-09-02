package test.gern.marmot.notification

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.gern.marmot.fakesendgridendpoints.fakeSendgridRoutes
import org.gern.marmot.notification.Emailer
import org.junit.After
import org.junit.Before
import test.gern.marmot.testsupport.MockServer
import test.gern.marmot.testsupport.assertJsonEquals
import java.net.URL
import kotlin.test.Test
import kotlin.test.assertTrue


class EmailerTest {
    private val server = MockServer(
        port = 9021,
        module = { fakeSendgridRoutes("super-secret") },
    )

    @Before
    fun setUp() {
        server.start()
    }

    @After
    fun tearDown() {
        server.stop()
    }

    @Test
    fun testSend() = runBlocking {
        val emailer = Emailer(
            client = OkHttpClient(),
            sendgridUrl = URL("http://localhost:9021"),
            sendgridApiKey = "super-secret",
            fromAddress = "from@example.com",
        )

        val success = emailer.send("to@example.com", "Guess what?", "Hi there")

        assertTrue(success)

        val expectedResponse = """
            {
                "personalizations": [{"to":[{"email": "to@example.com"}]}],
                "from": {"email": "from@example.com"},
                "subject": "Guess what?",
                "content": [{
                    "type": "text/plain",
                    "value": "Hi there"
                }]
            }""".trimIndent()

        assertJsonEquals(expectedResponse, server.lastCallBody())
    }
}
