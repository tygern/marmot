package test.gern.marmot.confirmation

import org.gern.marmot.confirmation.ConfirmationDataGateway
import org.gern.marmot.confirmation.ConfirmationService
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConfirmationServiceTest {
    @Test
    fun testConfirm() {
        val gateway = ConfirmationDataGateway()
        val service = ConfirmationService(gateway)

        val uuid = UUID.fromString("55555555-1d21-442e-8fc0-a2259ec09190")
        gateway.save("there@example.com", uuid)

        assertTrue(service.confirm("there@example.com", uuid))
        assertFalse(service.confirm("not-there@example.com", uuid))
        assertFalse(service.confirm("there@example.com", UUID.fromString("eeeeeeee-1d21-442e-8fc0-a2259ec09190")))
    }
}
