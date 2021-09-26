package io.initialcapacity.emailverifier.fakesendgridendpoints

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.slf4j.LoggerFactory

class FakeSendgrid

private val logger = LoggerFactory.getLogger(FakeSendgrid::class.java)

fun Application.fakeSendgridRoutes(authToken: String) {
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
