package org.gern.marmot.notificationserver

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        jackson()
    }

    install(Routing) {
        info()
    }
}

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080

    embeddedServer(
        factory = Jetty,
        port = port,
        module = { module() }
    ).start()
}
