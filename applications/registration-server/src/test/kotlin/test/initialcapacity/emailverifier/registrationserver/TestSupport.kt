package test.initialcapacity.emailverifier.registrationserver

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import io.initialcapacity.emailverifier.confirmation.ConfirmationDataGateway
import io.initialcapacity.emailverifier.rabbitsupport.RabbitExchange
import io.initialcapacity.emailverifier.rabbitsupport.buildConnectionFactory
import io.initialcapacity.emailverifier.registrationserver.module
import java.net.URI

@KtorExperimentalLocationsAPI
fun testApp(callback: TestApplicationEngine.() -> Unit) {
    val connectionFactory = buildConnectionFactory(URI("amqp://localhost:5672"))

    withTestApplication({ module(ConfirmationDataGateway(), connectionFactory, RabbitExchange("test-request-exchange")) }) { callback() }
}

val objectMapper = jacksonObjectMapper()
