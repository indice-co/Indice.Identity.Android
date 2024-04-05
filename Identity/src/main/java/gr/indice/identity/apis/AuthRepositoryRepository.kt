package gr.indice.identity.apis

import gr.indice.identity.models.TokenResponse
import gr.indice.identity.models.TokenType
import gr.indice.identity.protocols.IdentityConfig
import retrofit2.Response

class AuthRepositoryRepository(private val configuration: IdentityConfig, private val openIdApi: OpenIdApi) {
    suspend fun authorize(grand: OpenIdApi.OAuth2Grant): Response<TokenResponse> = openIdApi.authorize(url = configuration.tokenEndpoint, params = grand.params)

    suspend fun revoke(token: TokenType, withBasicAuth: String) = openIdApi.endSession(
        authorization = withBasicAuth,
        url = configuration.revokeEndpoint,
        token = token.value,
        tokenHint = token.typeHint
    )
}