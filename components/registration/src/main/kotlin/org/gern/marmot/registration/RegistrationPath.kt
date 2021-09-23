package org.gern.marmot.registration

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import org.gern.marmot.rabbitsupport.PublishAction

@KtorExperimentalLocationsAPI
@Location("/register")
class RegistrationPath(val email: String? = null)

@KtorExperimentalLocationsAPI
fun Route.registration(publishRequest: PublishAction) {
    post<RegistrationPath> {
        val parameters = call.receive<RegistrationPath>()

        if (parameters.email != null) {
            publishRequest(parameters.email)
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}
