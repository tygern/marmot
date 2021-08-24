package test.gern.marmot.registrationserver

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import org.gern.marmot.registrationserver.module

fun testApp(callback: TestApplicationEngine.() -> Unit) {
    withTestApplication({ module() }) { callback() }
}

val objectMapper = jacksonObjectMapper()
