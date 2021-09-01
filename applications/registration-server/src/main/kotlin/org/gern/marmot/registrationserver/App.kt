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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.gern.marmot.confirmation.ConfirmationDataGateway
import org.gern.marmot.confirmation.ConfirmationService
import org.gern.marmot.rabbitsupport.buildConnectionFactory
import org.gern.marmot.rabbitsupport.declare
import org.gern.marmot.rabbitsupport.listen
import org.gern.marmot.rabbitsupport.publish
import org.gern.marmot.registration.registration
import java.net.URI
import java.util.*

@KtorExperimentalLocationsAPI
fun Application.module(connectionFactory: ConnectionFactory) {
    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        jackson()
    }

    val registrationRequestExchange = "registration-request-exchange"
    connectionFactory.declare(exchange = registrationRequestExchange, queue = "registration-request")
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

    val registrationNotificationExchange = "registration-notification-exchange"
    connectionFactory.declare(exchange = registrationNotificationExchange, queue = "registration-notification")

    val publishNotification = publish(connectionFactory, registrationNotificationExchange)

    val confirmationService = ConfirmationService(
        gateway = ConfirmationDataGateway(),
        publishNotification = publishNotification,
        uuidProvider = { -> UUID.randomUUID()},
        mapper = jacksonObjectMapper(),
    )

    launch {
        val channel = connectionFactory.newConnection().createChannel()
        listen(queue = "registration-request", channel = channel) { email ->
            confirmationService.generateCodeAndPublish(email)
        }
    }

    embeddedServer(
        factory = Jetty,
        port = port,
        module = { module(connectionFactory) }
    ).start()
}
