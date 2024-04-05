package gr.indice.identity.models

data class RegisterUserRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val userName: String? = null,
    val password: String? = null,
    val passwordConfirmation: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val hasReadPrivacyPolicy: Boolean = false,
    val hasAcceptedTerms: Boolean = false,
    val claims: List<BasicClaimInfo>? = null
)
