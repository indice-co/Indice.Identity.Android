package gr.indice.identity.models

data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val newPasswordConfirmation: String
)
