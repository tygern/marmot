package org.gern.marmot.registrationserver

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rabbitmq.client.ConnectionFactory
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.gern.marmot.confirmation.ConfirmationDataGateway
import org.gern.marmot.confirmation.ConfirmationService
import org.gern.marmot.confirmation.UuidProvider
import org.gern.marmot.rabbitsupport.*
import org.gern.marmot.registration.registration
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.*

class App

private val logger = LoggerFactory.getLogger(App::class.java)

@KtorExperimentalLocationsAPI
fun Application.module(
    connectionFactory: ConnectionFactory,
    registrationRequestExchange: RabbitExchange,
) {
    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        jackson()
    }

    val publishRequest = publish(connectionFactory, registrationRequestExchange)

    install(Routing) {
        info()
        registration(publishRequest)
    }
}

@KtorExperimentalLocationsAPI
fun main(): Unit = runBlocking {
    val port = System.getenv("PORT")?.toInt() ?: 8081
    val rabbitUrl = System.getenv("RABBIT_URL")?.let(::URI)
        ?: throw RuntimeException("Please set the RABBIT_URL environment variable")

    val connectionFactory = buildConnectionFactory(rabbitUrl)

    val registrationNotificationExchange = RabbitExchange("registration-notification-exchange")
    val registrationNotificationQueue = RabbitQueue("registration-notification")
    val registrationRequestExchange = RabbitExchange("registration-request-exchange")
    val registrationRequestQueue = RabbitQueue("registration-request")

    connectionFactory.declare(exchange = registrationNotificationExchange, queue = registrationNotificationQueue)
    connectionFactory.declare(exchange = registrationRequestExchange, queue = registrationRequestQueue)

    listenForRegistrationRequests(connectionFactory, registrationNotificationExchange, registrationRequestQueue)
    registrationServer(port, connectionFactory, registrationRequestExchange).start()
}

@KtorExperimentalLocationsAPI
fun registrationServer(
    port: Int,
    connectionFactory: ConnectionFactory,
    registrationRequestExchange: RabbitExchange,
) = embeddedServer(
    factory = Jetty,
    port = port,
    module = { module(connectionFactory, registrationRequestExchange) }
)

fun CoroutineScope.listenForRegistrationRequests(
    connectionFactory: ConnectionFactory,
    registrationNotificationExchange: RabbitExchange,
    registrationRequestQueue: RabbitQueue,
    uuidProvider: UuidProvider = { -> UUID.randomUUID() },
) {
    val publishNotification = publish(connectionFactory, registrationNotificationExchange)

    val confirmationService = ConfirmationService(
        gateway = ConfirmationDataGateway(),
        publishNotification = publishNotification,
        uuidProvider = uuidProvider,
        mapper = jacksonObjectMapper(),
    )

    launch {
        logger.info("listening for registration requests")
        val channel = connectionFactory.newConnection().createChannel()
        listen(queue = registrationRequestQueue, channel = channel) { email ->
            logger.debug("received registration request for {}", email)
            confirmationService.generateCodeAndPublish(email)
        }
    }
}
