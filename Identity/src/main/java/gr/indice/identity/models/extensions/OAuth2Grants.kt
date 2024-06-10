package gr.indice.identity.models.extensions

import gr.indice.identity.apis.OpenIdApi
import gr.indice.identity.apis.ThisDeviceIds
import gr.indice.identity.protocols.Client
import gr.indice.identity.protocols.OAuth2Grant
import java.security.Signature


private fun Map<String, String?>.filterNulls() =
    mapNotNull { it.value?.let { v -> it.key to v } }.toMap()


data class ClientCredentialsGrand(val client: Client): OAuth2Grant {
    override val grantType: String = "client_credentials"
    override val params:  Map<String, Any> get() = mapOf(
        "grant_type" to grantType,
        "client_id" to client.id,
        "client_secret" to client.secret,
        "scope" to "identity",
    ).filterNulls()

}

//region Password grant
data class PasswordGrant(
    val username: String,
    val password: String,
    val deviceId: String,
    val client: Client
) : OAuth2Grant {
    override val grantType = "password"
    override val params: Map<String, Any> get() = mapOf(
        "grant_type" to grantType,
        "client_id" to client.id,
        "client_secret" to client.secret,
        "scope" to client.scope,
        "username" to username,
        "password" to password,
        "device_id" to deviceId
    ).filterNulls()
}
//endregion Password grant

//region AuthCode grant
data class AuthCodeGrant(
    var redirect_uri: String,
    var code: String,
    var code_verifier: String,
    val scope: String,
    val client: Client
): OAuth2Grant {
    override val grantType: String = "authorization_code"
    override val params: Map<String, Any> = mapOf(
        "grant_type" to grantType,
        "client_id" to client.id,
        "client_secret" to client.secret,
        "scope" to scope, // client.scope,
        "redirect_uri" to redirect_uri,
        "code" to code,
        "code_verifier" to code_verifier
    ).filterNulls()
}

data class RefreshTokenGrant(
    val refreshToken: String?,
    val client: Client
) : OAuth2Grant {
    override val grantType = "refresh_token"
    override val params: Map<String, Any> get() = mapOf(
        "grant_type" to grantType,
        "refresh_token" to refreshToken,
        "client_id" to client.id,
        "client_secret" to client.secret
    ).filterNulls()

}
//endregion AuthCode grant

//region Device Authentication grant
data class DeviceAuthGrant(
    val mode: String?,
    val pin: String?,
    val code: String?,
    val code_signature: String?,
    val code_verifier: String?,
    val device_id: String?,
    val registration_id: String?,
    val public_key: String?,
    val client_id: String?,
    val scope: String?,
): OAuth2Grant {

    sealed interface Info {
        data class Biometric(val signatureUnlock: suspend (Signature) -> Signature): Info
        data class Pin(val value: String): Info
    }

    override val grantType = "device_authentication"

    override val params: Map<String, Any> get() = mapOf(
        "grant_type" to grantType,
        "mode" to mode,
        "pin" to pin,
        "code" to code,
        "code_signature" to code_signature,
        "code_verifier" to code_verifier,
        "device_id" to device_id,
        "registration_id" to registration_id,
        "public_key" to public_key,
        "client_id" to client_id,
        "scope" to scope,
    ).filterNulls()

    companion object {
        fun pin(
            pin: String,
            deviceIds: ThisDeviceIds,
            client: Client
        ) = DeviceAuthGrant(
            mode = "pin",
            pin = pin,
            code = null,
            code_signature = null,
            code_verifier = null,
            device_id = deviceIds.device,
            registration_id = deviceIds.registration ?: throw Exception("Trust device registration not present"),
            public_key = null,
            client_id = client.id,
            scope = client.scope,
        )

        fun biometric(
            challenge: String,
            codeSignature: String,
            verifier: String,
            deviceIds: ThisDeviceIds,
            publicKey: String,
            client: Client
        ) = DeviceAuthGrant(
            mode = "fingerprint",
            pin = null,
            code = challenge,
            code_signature = codeSignature,
            code_verifier = verifier,
            device_id = deviceIds.device,
            registration_id = deviceIds.registration ?: throw Exception("Trust device registration not present"),
            public_key = publicKey,
            client_id = client.id,
            scope = client.scope,
        )
    }
}



//endregion Device Authentication grant