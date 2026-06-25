package gr.indice.identity.models

enum class UserNameStatus {
    Available, Unavailable, Failed
}

data class UsernameAvailability(
    val status: UserNameStatus
)