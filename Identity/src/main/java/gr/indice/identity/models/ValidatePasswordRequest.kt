package gr.indice.identity.models

data class ValidatePasswordRequest(
    val token: String = "",
    val password: String,
    val userName: String = ""
)
