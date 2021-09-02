package test.gern.marmot.testsupport

import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.install
import io.ktor.features.DoubleReceive
import io.ktor.request.receive
import io.ktor.request.receiveText
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import kotlinx.coroutines.delay
import kotlin.test.fail
import kotlin.time.Duration

class MockServer(
    port: Int,
    module: Application.() -> Unit
) {
    private val calls = mutableListOf<String>()

    private val server = embeddedServer(
        factory = Jetty,
        port = port,
        module = {
            install(DoubleReceive)
            module()
            intercept(ApplicationCallPipeline.Monitoring) {
                calls.add(context.request.call.receiveText())
            }
        }
    )

    fun start() = server.start(wait = false)

    fun stop() = server.stop(50, 50)

    fun lastCallBody() = calls.lastOrNull() ?: fail("No calls received")

    suspend fun waitForCall(timeout: Duration = Duration.Companion.milliseconds(200)): String {
        var call = calls.lastOrNull()
        var elapsed = Duration.ZERO

        while (call == null) {
            if (elapsed >= timeout) {
                fail("No calls received")
            }

            val delayDuration = Duration.milliseconds(50)
            delay(delayDuration)
            elapsed += delayDuration

            call = calls.lastOrNull()
        }

        return call
    }
}
