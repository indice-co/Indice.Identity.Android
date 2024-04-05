package gr.indice.identity.models

enum class UserNameStatus {
    Available, Unavailable
}

data class UsernameAvailability(
    val status: UserNameStatus
)