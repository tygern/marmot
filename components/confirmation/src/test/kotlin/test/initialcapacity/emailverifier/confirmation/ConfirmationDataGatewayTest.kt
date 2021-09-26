package test.initialcapacity.emailverifier.confirmation

import io.initialcapacity.emailverifier.confirmation.ConfirmationDataGateway
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfirmationDataGatewayTest {
    @Test
    fun testGet() {
        val uuid = UUID.fromString("11111111-e89b-12d3-a456-426614174000")
        val gateway = ConfirmationDataGateway()

        gateway.save("email@example.com", uuid)

        assertEquals(uuid, gateway.get("email@example.com"))
        assertEquals(null, gateway.get("not_there@example.com"))
    }
}