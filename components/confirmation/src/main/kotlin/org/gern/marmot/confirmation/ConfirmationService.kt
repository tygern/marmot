package org.gern.marmot.confirmation

import java.util.*

class ConfirmationService(
    private val confirmationGateway: ConfirmationDataGateway
) {
    fun confirm(email: String, confirmationCode: UUID): Boolean {
        val storedCode = confirmationGateway.get(email)

        return storedCode == confirmationCode
    }
}
