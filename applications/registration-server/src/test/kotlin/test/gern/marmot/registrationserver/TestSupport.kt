package test.gern.marmot.registrationserver

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import org.gern.marmot.rabbitsupport.buildConnectionFactory
import org.gern.marmot.registrationserver.module
import java.net.URI

@KtorExperimentalLocationsAPI
fun testApp(callback: TestApplicationEngine.() -> Unit) {
    val connectionFactory = buildConnectionFactory(URI("amqp://localhost:5672"))
    withTestApplication({ module(connectionFactory) }) { callback() }
}

val objectMapper = jacksonObjectMapper()
