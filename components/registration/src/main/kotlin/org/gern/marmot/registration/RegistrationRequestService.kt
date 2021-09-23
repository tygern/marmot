package org.gern.marmot.registration

import com.fasterxml.jackson.databind.ObjectMapper
import org.gern.marmot.confirmation.ConfirmationDataGateway
import org.gern.marmot.rabbitsupport.PublishAction
import org.slf4j.LoggerFactory
import java.util.*

typealias UuidProvider = () -> UUID

class RegistrationRequestService(
    private val gateway: ConfirmationDataGateway,
    private val publishNotification: PublishAction,
    private val uuidProvider: UuidProvider,
    private val mapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(RegistrationRequestService::class.java)

    fun generateCodeAndPublish(email: String) {
        val confirmationCode = uuidProvider()
        gateway.save(email, confirmationCode)

        val message = mapper.writeValueAsString(ConfirmationMessage(email, confirmationCode))

        logger.debug("publishing notification request {}", message)
        publishNotification(message)
    }
}

data class ConfirmationMessage(
    val email: String,
    val confirmationCode: UUID,
)
