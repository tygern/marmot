package test.gern.marmot.notificationserver

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.gern.marmot.fakesendgridendpoints.fakeSendgridRoutes
import org.gern.marmot.notificationserver.start
import org.gern.marmot.rabbitsupport.buildConnectionFactory
import org.gern.marmot.rabbitsupport.publish
import org.junit.After
import org.junit.Before
import test.gern.marmot.testsupport.MockServer
import test.gern.marmot.testsupport.assertJsonEquals
import java.net.URI
import java.net.URL
import kotlin.test.Test
import kotlin.time.Duration

@ExperimentalCoroutinesApi
class AppTest {
    private val sendgridUrl = URL("http://localhost:9021")
    private val rabbitUri = URI("amqp://localhost:5672")

    private val sendgridServer = MockServer(
        port = 9021,
        module = { fakeSendgridRoutes("super-secret") },
    )

    @Before
    fun setUp() {
        sendgridServer.start()
    }

    @After
    fun tearDown() {
        sendgridServer.stop()
    }

    @Test
    fun testApp() = runBlocking {
        val connectionFactory = buildConnectionFactory(rabbitUri)
        val exchange = "notification-test-exchange"
        val notificationPublisher = publish(connectionFactory, exchange)

        start(
            sendgridUrl = sendgridUrl,
            sendgridApiKey = "super-secret",
            fromAddress = "from@example.com",
            connectionFactory = connectionFactory,
            registrationNotificationExchange = exchange,
            registrationNotificationQueue = "notification-test-queue",
        )

        notificationPublisher("""{"email": "to@example.com", "confirmationCode": "33333333-e89b-12d3-a456-426614174000"}""")

        val expectedCall = """
            {
                "personalizations": [{"to":[{"email": "to@example.com"}]}],
                "from": {"email": "from@example.com"},
                "subject": "Marmot confirmation code",
                "content": [{
                    "type": "text/plain",
                    "value": "Your confirmation code is 33333333-e89b-12d3-a456-426614174000"
                }]
            }"""

        val receivedCall = sendgridServer.waitForCall(Duration.seconds(2))

        assertJsonEquals(expectedCall, receivedCall)
    }
}
