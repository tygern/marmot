package test.gern.marmot.notificationserver

import io.ktor.http.HttpMethod
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.server.testing.handleRequest
import kotlin.test.assertNotNull

class AppTest {
    @Test
    fun testInfo() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(200, response.status()?.value)
            assertNotNull(response.content)

            val body = objectMapper.readTree(response.content)

            assertEquals("notification server", body["application"].asText())
        }
    }
}
