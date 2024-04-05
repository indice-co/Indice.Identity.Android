package gr.indice.identity.protocols

import gr.indice.identity.models.TokenResponse
import gr.indice.identity.models.TokenType
import gr.indice.identity.models.accessToken
import gr.indice.identity.models.refreshToken

interface TokenStorageAccessor {
    val tokenType: String?
    val idToken: String?
    val accessToken: TokenType?
    val refreshToken: TokenType?
    val authorization: String?
}

interface TokenStorage: TokenStorageAccessor {

    companion object

    fun parse(tokenResponse: TokenResponse)
    fun clear()

    class Ephemeral: TokenStorage {
        override fun parse(tokenResponse: TokenResponse) {
            tokenType = tokenResponse.token_type
            idToken = tokenResponse.id_token
            accessToken = accessToken(value = tokenResponse.access_token)
            refreshToken = refreshToken(value = tokenResponse.refresh_token)
        }

        override fun clear() {
            tokenType = null
            idToken = null
            accessToken = null
            refreshToken = null
        }

        override var tokenType: String? = null
            private set
        override var idToken: String? = null
            private set
        override var accessToken: TokenType? = null
            private set
        override var refreshToken: TokenType? = null
            private set

        override val authorization: String? get() {
            val type = tokenType ?: return null
            val access = accessToken ?: return null

            return "$type ${access.value}"
        }
    }


}