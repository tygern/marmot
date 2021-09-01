package org.gern.marmot.fakesendgrid

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CallLogging
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class App

private val logger = LoggerFactory.getLogger(App::class.java)

fun Application.module(authToken: String) {
    routing {
        get("/") { call.respond("Fake Sendgrid") }
        post("/v3/mail/send") {
            val headers = call.request.headers
            if (headers["Authorization"] != "Bearer $authToken") {
                return@post call.respond(HttpStatusCode.Unauthorized)
            }
            if (headers["Content-Type"]?.lowercase() != "application/json; charset=utf-8") {
                return@post call.respond(HttpStatusCode.BadRequest)
            }

            val body = call.receive<String>()
            logger.info("email sent {}", body)

            call.respond(HttpStatusCode.Created)
        }
    }
}

fun main(): Unit = runBlocking {
    val port = System.getenv("PORT")?.toInt() ?: 9090

    logger.info("waiting for mail")
    embeddedServer(
        factory = Jetty,
        port = port,
        module = { module("super-secret") }
    ).start()
}
