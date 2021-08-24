package org.gern.marmot.notificationserver

import io.ktor.application.call
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route

@Location("/")
class InfoPath

fun Route.info() {
    get<InfoPath> {
        call.respond(mapOf("application" to "notification server"))
    }
}
