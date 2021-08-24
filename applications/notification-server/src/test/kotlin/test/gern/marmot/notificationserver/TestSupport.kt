package test.gern.marmot.notificationserver

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import org.gern.marmot.notificationserver.module

fun testApp(callback: TestApplicationEngine.() -> Unit) {
    withTestApplication({ module() }) { callback() }
}

val objectMapper = jacksonObjectMapper()
