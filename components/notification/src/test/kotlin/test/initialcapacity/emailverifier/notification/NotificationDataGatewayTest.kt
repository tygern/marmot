package test.initialcapacity.emailverifier.notification

import io.initialcapacity.emailverifier.notification.NotificationDataGateway
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NotificationDataGatewayTest {
    @Test
    fun testSave() {
        val gateway = NotificationDataGateway()
        val uuid = UUID.fromString("020b82f6-866f-47a6-90d4-359e866da123")

        gateway.save("a@example.com", uuid)

        assertEquals(uuid, gateway.find("a@example.com"))
    }

    @Test
    fun find_notFound() {
        val gateway = NotificationDataGateway()

        assertNull(gateway.find("notthere@example.com"))
    }
}
