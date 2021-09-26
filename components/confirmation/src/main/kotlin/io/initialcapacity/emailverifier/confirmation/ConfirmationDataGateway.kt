package io.initialcapacity.emailverifier.confirmation

import java.util.*

class ConfirmationDataGateway {
    private val codeStore = mutableMapOf<String, UUID>()

    fun save(email: String, confirmationCode: UUID) {
        codeStore[email] = confirmationCode
    }

    fun get(email: String) = codeStore[email]
}