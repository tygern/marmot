package org.gern.marmot.notificationserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rabbitmq.client.ConnectionFactory
import okhttp3.OkHttpClient
import org.gern.marmot.notification.Emailer
import org.gern.marmot.notification.NotificationDataGateway
import org.gern.marmot.notification.Notifier
import org.gern.marmot.rabbitsupport.buildConnectionFactory
import org.gern.marmot.rabbitsupport.declare
import org.gern.marmot.rabbitsupport.listen
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URL
import java.util.*

class App

private val logger = LoggerFactory.getLogger(App::class.java)

fun main() {
    val rabbitUrl = System.getenv("RABBIT_URL")?.let(::URI)
        ?: throw RuntimeException("Please set the RABBIT_URL environment variable")
    val sendgridUrl = System.getenv("SENDGRID_URL")?.let(::URL)
        ?: throw RuntimeException("Please set the SENDGRID_URL environment variable")
    val sendgridApiKey = System.getenv("SENDGRID_API_KEY")
        ?: throw RuntimeException("Please set the SENDGRID_API_KEY environment variable")
    val fromAddress = System.getenv("FROM_ADDRESS")
        ?: throw RuntimeException("Please set the FROM_ADDRESS environment variable")

    start(
        sendgridUrl = sendgridUrl,
        sendgridApiKey = sendgridApiKey,
        fromAddress = fromAddress,
        rabbitUrl = rabbitUrl,
        registrationNotificationExchange = "registration-notification-exchange",
        registrationNotificationQueue = "registration-notification"
    )
}

fun start(
    sendgridUrl: URL,
    sendgridApiKey: String,
    fromAddress: String,
    rabbitUrl: URI,
    registrationNotificationExchange: String,
    registrationNotificationQueue: String
) {
    val objectMapper = jacksonObjectMapper()
    val notifier = createNotifier(sendgridUrl, sendgridApiKey, fromAddress)

    val connectionFactory = buildConnectionFactory(rabbitUrl)

    connectionFactory.declare(exchange = registrationNotificationExchange, queue = registrationNotificationQueue)

    logger.info("listening for registration notifications")
    listenForNotificationRequests(connectionFactory, objectMapper, notifier)
}

private fun createNotifier(
    sendgridUrl: URL,
    sendgridApiKey: String,
    fromAddress: String
): Notifier {
    val emailer = Emailer(
        client = OkHttpClient(),
        sendgridUrl = sendgridUrl,
        sendgridApiKey = sendgridApiKey,
        fromAddress = fromAddress,
    )
    val gateway = NotificationDataGateway()
    return Notifier(gateway, emailer)
}

private fun listenForNotificationRequests(
    connectionFactory: ConnectionFactory,
    objectMapper: ObjectMapper,
    notifier: Notifier
) {
    val channel = connectionFactory.newConnection().createChannel()

    listen(queue = "registration-notification", channel = channel) {
        val message = objectMapper.readValue(it, NotificationMessage::class.java)
        logger.debug("received registration notification {}", message)
        notifier.notify(message.email, message.confirmationCode)
    }
}

private data class NotificationMessage(
    val email: String,
    val confirmationCode: UUID,
)
