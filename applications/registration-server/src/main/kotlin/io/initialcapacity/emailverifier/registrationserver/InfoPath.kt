package io.initialcapacity.emailverifier.registrationserver

import io.ktor.application.call
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route

@KtorExperimentalLocationsAPI
@Location("/")
class InfoPath

@KtorExperimentalLocationsAPI
fun Route.info() {
    get<InfoPath> {
        call.respond(mapOf("application" to "registration server"))
    }
}
