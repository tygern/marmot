package test.gern.marmot.registration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.gern.marmot.confirmation.ConfirmationDataGateway
import org.gern.marmot.registration.RegistrationRequestService
import test.gern.marmot.testsupport.assertJsonEquals
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RegistrationRequestServiceTest {
    @Test
    fun testGenerateCodeAndPublish() {
        val uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        var publishedMessage: String? = null

        val gateway = ConfirmationDataGateway()
        val publish = { message: String -> publishedMessage = message }
        val uuidProvider = {  -> uuid }

        val expectedMessage = """
            {
              "email":"test@example.com",
              "confirmationCode":"123e4567-e89b-12d3-a456-426614174000"
            }
            """.trimIndent()

        val service = RegistrationRequestService(
            gateway,
            publish,
            uuidProvider,
            jacksonObjectMapper()
        )

        service.generateCodeAndPublish("test@example.com")

        assertEquals(uuid, gateway.get("test@example.com"))
        assertJsonEquals(expectedMessage, publishedMessage)
    }
}
