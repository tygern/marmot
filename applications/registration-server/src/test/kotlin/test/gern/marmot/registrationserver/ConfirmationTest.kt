package test.gern.marmot.registrationserver

import io.ktor.locations.KtorExperimentalLocationsAPI
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.gern.marmot.confirmation.ConfirmationDataGateway
import org.gern.marmot.rabbitsupport.RabbitExchange
import org.gern.marmot.rabbitsupport.buildConnectionFactory
import org.gern.marmot.registrationserver.registrationServer
import org.junit.After
import org.junit.Before
import java.net.URI
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@KtorExperimentalLocationsAPI
class ConfirmationTest {
    private val connectionFactory = buildConnectionFactory(URI("amqp://localhost:5672"))
    private val requestExchange = RabbitExchange("test-request-exchange")
    private val confirmationDataGateway = ConfirmationDataGateway()
    private val confirmationCode = "cccccccc-1d21-442e-8fc0-a2259ec09190"

    init {
        confirmationDataGateway.save("pickles@example.com", UUID.fromString(confirmationCode))
    }

    private val regServer = registrationServer(
        port = 9120,
        confirmationDataGateway = confirmationDataGateway,
        connectionFactory = connectionFactory,
        registrationRequestExchange = requestExchange,
    )

    private val client = OkHttpClient()

    @Before
    fun setUp() {
        regServer.start(wait = false)
    }

    @After
    fun tearDown() {
        regServer.stop(50, 50)
    }

    @Test
    fun testConfirmation(): Unit = runBlocking {
        val request = Request.Builder()
            .url("http://localhost:9120/confirmation")
            .addHeader("Content-Type", "application/json")
            .post(
                """{"email": "pickles@example.com", "confirmationCode": "$confirmationCode"}""".toRequestBody(
                    "application/json; charset=utf-8".toMediaType()
                )
            )
            .build()

        client.newCall(request).execute().use { assertTrue(it.isSuccessful) }
    }

    @Test
    fun testConfirmationWrongCode(): Unit = runBlocking {
        val request = Request.Builder()
            .url("http://localhost:9120/confirmation")
            .addHeader("Content-Type", "application/json")
            .post(
                """{"email": "pickles@example.com", "confirmationCode": "00000000-1d21-442e-8fc0-a2259ec09190"}""".toRequestBody(
                    "application/json; charset=utf-8".toMediaType()
                )
            )
            .build()

        client.newCall(request).execute().use { assertFalse(it.isSuccessful) }
    }
}
