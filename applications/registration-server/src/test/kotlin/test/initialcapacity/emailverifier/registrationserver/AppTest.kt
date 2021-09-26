package test.initialcapacity.emailverifier.registrationserver

import io.ktor.http.HttpMethod
import io.ktor.locations.KtorExperimentalLocationsAPI
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.server.testing.handleRequest
import kotlin.test.assertNotNull

@KtorExperimentalLocationsAPI
class AppTest {
    @Test
    fun testInfo() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(200, response.status()?.value)
            assertNotNull(response.content)

            val body = objectMapper.readTree(response.content)

            assertEquals("registration server", body["application"].asText())
        }
    }
}
