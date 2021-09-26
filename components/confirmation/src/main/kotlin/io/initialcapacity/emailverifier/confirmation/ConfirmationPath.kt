package io.initialcapacity.emailverifier.confirmation

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import java.util.*

@KtorExperimentalLocationsAPI
@Location("/confirmation")
class ConfirmationPath(
    val email: String? = null,
    val confirmationCode: UUID? = null,
)

@KtorExperimentalLocationsAPI
fun Route.confirmation(confirmationService: ConfirmationService) {
    post<ConfirmationPath> {
        val parameters = call.receive<ConfirmationPath>()

        if (parameters.email == null || parameters.confirmationCode == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val success = confirmationService.confirm(parameters.email, parameters.confirmationCode)

        if (success) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}
