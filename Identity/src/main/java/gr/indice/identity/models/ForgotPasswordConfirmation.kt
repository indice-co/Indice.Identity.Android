package gr.indice.identity.models

data class ForgotPasswordConfirmation(
    val email: String,
    val newPassword: String,
    val newPasswordConfirmation: String,
    val returnUrl: String,
    val token: String)
