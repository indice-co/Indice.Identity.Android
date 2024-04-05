package gr.indice.identity.models

data class UpdateEmailRequest(
    val email: String,
    val returnUrl: String? = null
)
