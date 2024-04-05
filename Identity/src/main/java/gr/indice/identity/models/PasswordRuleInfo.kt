package gr.indice.identity.models

data class PasswordRuleInfo(
    val code: String?,
    val description: String,
    val requirement: String,
    val isValid: Boolean?
)
