package gr.indice.identity.models.extensions

import gr.indice.identity.pixie.PKCE
import gr.indice.identity.protocols.Client
import gr.indice.identity.protocols.IdentityConfig

data class AuthRequest(
    var client_id: String,
    var client_secret: String?,
    var scope: String,
    var redirect_uri: String?,
    var response_type: String,
    var response_mode: String,
    var prompt: String?,
    //var state: String?, Unused for now.
    var nonce: String?,
    var code_challenge: String?,
    var code_challenge_method: String?,
    //var acr_values: String? // ACR value could be added on the URL object manually if needed!
) {
    constructor(
        client: Client,
        config: IdentityConfig,
        prompt: String?,
        pkce: PKCE
    ) : this(
        client_id = client.id,
        client_secret = client.secret,
        scope = client.scope,
        redirect_uri = client.urls.authorization,
        response_type = config.authCodeResponseType,
        response_mode = config.authCodeResponseMode,
        prompt = prompt,
        nonce = pkce.nonce,
        code_challenge = pkce.challenge,
        code_challenge_method = pkce.challengeMethod.value
    )
}
