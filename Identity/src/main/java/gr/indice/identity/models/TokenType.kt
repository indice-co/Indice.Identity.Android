package gr.indice.identity.models

sealed interface TokenType {
    val value    : String
    val typeHint : String
    class AccessToken(override val value: String): TokenType {
        override val typeHint = "access_token"
    }

    class RefreshToken(override val value: String): TokenType {
        override val typeHint = "refresh_token"
    }

}

fun accessToken(value: String?): TokenType? {
    return if (value != null)
        TokenType.AccessToken(value)
    else null
}
fun refreshToken(value: String?): TokenType? {
    return if (value != null)
        TokenType.RefreshToken(value)
    else null
}