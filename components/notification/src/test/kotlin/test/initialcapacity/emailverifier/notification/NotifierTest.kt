package test.initialcapacity.emailverifier.notification

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import io.initialcapacity.emailverifier.notification.Emailer
import io.initialcapacity.emailverifier.notification.NotificationDataGateway
import io.initialcapacity.emailverifier.notification.Notifier
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class NotifierTest {
    @Test
    fun testNotify() = runBlocking {
        val uuid = UUID.fromString("aaaaaaaa-866f-47a6-90d4-359e866da123")
        val gateway = NotificationDataGateway()
        val emailer = mockk<Emailer>()
        every { emailer.send(any(), any(), any()) } returns true
        val notifier = Notifier(gateway, emailer)


        notifier.notify("a@example.com", uuid)

        assertEquals(uuid, gateway.find("a@example.com"))
        coVerify { emailer.send("a@example.com", "Confirmation code", "Your confirmation code is $uuid") }
    }
}
