package gr.indice.identity.models

import java.util.UUID

data class RegistrationResult(
    val deviceId: String?,
    val registrationId: UUID?
)

