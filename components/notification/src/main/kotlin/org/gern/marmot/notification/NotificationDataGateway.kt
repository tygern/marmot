package org.gern.marmot.notification

import java.util.*

class NotificationDataGateway {
    private val store = mutableListOf<NotificationRecord>()

    fun find(email: String): UUID? =
        store.find { it.email == email }?.confirmationCode

    fun save(email: String, confirmationCode: UUID) {
        store.add(NotificationRecord(email, confirmationCode))
    }
}

private data class NotificationRecord(val email: String, val confirmationCode: UUID)
