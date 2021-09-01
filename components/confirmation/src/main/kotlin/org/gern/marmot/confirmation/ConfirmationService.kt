package org.gern.marmot.confirmation

import com.fasterxml.jackson.databind.ObjectMapper
import org.gern.marmot.rabbitsupport.PublishAction
import java.util.*
import java.util.UUID.randomUUID

typealias UuidProvider = () -> UUID

class ConfirmationService(
    private val gateway: ConfirmationDataGateway,
    private val publishNotification: PublishAction,
    private val uuidProvider: UuidProvider,
    private val mapper: ObjectMapper
) {
    fun generateCodeAndPublish(email: String) {
        val confirmationCode = uuidProvider()
        gateway.save(email, confirmationCode)

        val message = mapper.writeValueAsString(ConfirmationMessage(email, confirmationCode))

        println("publishing $message")
        publishNotification(message)
    }
}

data class ConfirmationMessage(
    val email: String,
    val confirmationCode: UUID,
)

