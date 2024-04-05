package gr.indice.identity.models

data class ForgotPasswordRequest(val email: String, val returnUrl: String?)
