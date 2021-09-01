package org.gern.marmot.notification

import java.util.*

class Notifier(
    private val gateway: NotificationDataGateway,
    private val emailer: Emailer
) {
    fun notify(email: String, confirmationCode: UUID) {
        gateway.save(email, confirmationCode)
        emailer.send(
            toAddress = email,
            subject = "Marmot confirmation code",
            message = "Your confirmation code is $confirmationCode"
        )
        println("Sent! $email, $confirmationCode")
    }
}