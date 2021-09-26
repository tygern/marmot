package test.initialcapacity.emailverifier.registrationserver

import io.ktor.locations.KtorExperimentalLocationsAPI
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import io.initialcapacity.emailverifier.confirmation.ConfirmationDataGateway
import io.initialcapacity.emailverifier.rabbitsupport.RabbitExchange
import io.initialcapacity.emailverifier.rabbitsupport.RabbitQueue
import io.initialcapacity.emailverifier.rabbitsupport.buildConnectionFactory
import io.initialcapacity.emailverifier.rabbitsupport.declare
import io.initialcapacity.emailverifier.registrationserver.listenForRegistrationRequests
import io.initialcapacity.emailverifier.registrationserver.registrationServer
import org.junit.After
import org.junit.Before
import test.initialcapacity.emailverifier.testsupport.assertMessageReceived
import java.net.URI
import java.util.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration

@KtorExperimentalLocationsAPI
class RegistrationTest {
    private val connectionFactory = buildConnectionFactory(URI("amqp://localhost:5672"))
    private val notificationExchange = RabbitExchange("test-notification-exchange")
    private val notificationQueue = RabbitQueue("test-notification-queue")
    private val requestExchange = RabbitExchange("test-request-exchange")
    private val requestQueue = RabbitQueue("test-request-queue")

    private val confirmationDataGateway = ConfirmationDataGateway()

    private val regServer = registrationServer(
        port = 9120,
        connectionFactory = connectionFactory,
        registrationRequestExchange = requestExchange,
        confirmationDataGateway = confirmationDataGateway,
    )

    private val client = OkHttpClient()

    @Before
    fun setUp() {
        connectionFactory.declare(notificationExchange, notificationQueue)
        connectionFactory.declare(requestExchange, requestQueue)
        regServer.start(wait = false)
    }

    @After
    fun tearDown() {
        regServer.stop(50, 50)
    }

    @Test
    fun testRegistration():Unit = runBlocking {
        listenForRegistrationRequests(
            confirmationDataGateway = confirmationDataGateway,
            connectionFactory = connectionFactory,
            registrationNotificationExchange = notificationExchange,
            registrationRequestQueue = requestQueue,
            uuidProvider = { UUID.fromString("cccccccc-1d21-442e-8fc0-a2259ec09190") }
        )

        val request = Request.Builder()
            .url("http://localhost:9120/register")
            .addHeader("Content-Type", "application/json")
            .post("""{"email": "user@example.com"}""".toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        client.newCall(request).execute().use { assertTrue(it.isSuccessful) }

        val expectedMessage = """
            {
              "email" : "user@example.com",
              "confirmationCode" : "cccccccc-1d21-442e-8fc0-a2259ec09190"
            }
        """.trimIndent()

        connectionFactory.assertMessageReceived(
            queue = notificationQueue,
            message = expectedMessage,
            timeout = Duration.milliseconds(500)
        )
    }
}
