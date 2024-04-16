package gr.indice.identity.client.services

import android.net.Uri
import android.util.Base64
import gr.indice.identity.apis.AuthRepositoryRepository
import gr.indice.identity.apis.DevicesRepository
import gr.indice.identity.apis.OpenIdApi
import gr.indice.identity.apis.ThisDeviceRepository
import gr.indice.identity.models.DeviceAuthentications
import gr.indice.identity.models.extensions.AuthCodeGrant
import gr.indice.identity.models.extensions.AuthRequest
import gr.indice.identity.models.extensions.ClientCredentialsGrand
import gr.indice.identity.models.extensions.DeviceAuthGrant
import gr.indice.identity.models.extensions.PasswordGrant
import gr.indice.identity.models.extensions.RefreshTokenGrant
import gr.indice.identity.models.extensions.biometricAuth
import gr.indice.identity.pixie.PKCE
import gr.indice.identity.protocols.Client
import gr.indice.identity.protocols.IdentityConfig
import gr.indice.identity.protocols.IdentityEncryptedStorage
import gr.indice.identity.protocols.TokenStorage
import gr.indice.identity.protocols.basicAuth
import gr.indice.identity.utils.CryptoUtils
import gr.indice.identity.utils.Serializer
import gr.indice.identity.utils.ServiceErrorException
import java.security.Signature
import java.util.concurrent.CancellationException

interface AuthorizationService {
    /** Try login with any grant */
    @Throws(ServiceErrorException::class)
    suspend fun login(grand: OpenIdApi.OAuth2Grant)
    /** Try login using the password grant */
    @Throws(ServiceErrorException::class)
    suspend fun login(userName: String, password: String)
    /** Try login using the device_authentication grant - using the 4pin mode */
    @Throws(ServiceErrorException::class)
    suspend fun login(pin: String)
    /** Try login using the device_authentication grant - using the fingerprint mode */
    @Throws(ServiceErrorException::class)
    suspend fun loginBiometric(signatureUnlock: suspend (Signature) -> Signature)
    /** Try login using the AuthCode grant - using the PKCE mode */
    @Throws(ServiceErrorException::class)
    suspend fun loginWithCode(code: String, verifier: String)
    /** Try to refresh current token */
    @Throws(ServiceErrorException::class)
    suspend fun refreshToken()
    /* Try to authorize the current client (ClientCredentials) */
    @Throws(ServiceErrorException::class)
    suspend fun authorizeClient()

    /**
     * Request to revoke a use's access & refresh tokens.
     * **access_token can be revoked only if it is a reference token**.
     */
    @Throws(ServiceErrorException::class)
    suspend fun revokeTokens()
    /** Generate the url used to initiate a authorization_code flow  */
    fun authorizationUrl(pkce: PKCE, andPrompt: String? = "login"): Uri
    /** Generate the url used to end a user's session  */
    fun endSessionUrl(): Uri

}

internal class AuthorizationServiceImpl(
    private val authRepositoryRepository: AuthRepositoryRepository,
    private val devicesRepository: DevicesRepository,
    private val thisDeviceRepository: ThisDeviceRepository,
    private val encryptedStorage: IdentityEncryptedStorage,
    private val deviceService: DevicesService,
    private val tokenStorage: TokenStorage,
    private val client: Client,
    private val configuration: IdentityConfig
): BaseService(), AuthorizationService {
    override suspend fun login(grand: OpenIdApi.OAuth2Grant) {
        val tokenResponse = load { authRepositoryRepository.authorize(grand) }
        tokenStorage.parse(tokenResponse)
    }

    override suspend fun login(userName: String, password: String) =
        login(grand = PasswordGrant(username = userName, password = password, deviceId = thisDeviceRepository.ids.device, client = client))


    override suspend fun login(pin: String) {
        try {
            val pinHash = CryptoUtils.createPinHash(pin, thisDeviceRepository.ids.device)
            login(DeviceAuthGrant.pin(pin = pinHash, deviceIds = thisDeviceRepository.ids, client = client))
        } catch (e: Exception) {
            throw e
        }
    }


    override suspend fun loginBiometric(signatureUnlock: suspend (Signature) -> Signature) {
        val codeVerifier = CryptoUtils.createCodeVerifier()
        val verifierHash = CryptoUtils.sha256(codeVerifier)

        val authRequest = DeviceAuthentications.AuthorizationRequest.biometricAuth(
            codeChallenge = verifierHash, deviceIds = thisDeviceRepository.ids, client = client
        )

        val challenge = load { devicesRepository.authorize(authRequest = authRequest) }.challenge!!

        try {
            val signature = CryptoUtils.getSignature()
            val key = CryptoUtils.getPrivateKey(CryptoUtils.KeyType.BIOMETRIC)
            signature.initSign(key)

            val signed = signatureUnlock(signature).run {
                update(challenge.toByteArray())
                sign().let { Base64.encodeToString(it, Base64.NO_WRAP) }
            }

            val public = CryptoUtils.getPemFromKey(CryptoUtils.KeyType.BIOMETRIC)

            login(grand = DeviceAuthGrant.biometric(
                challenge = challenge,
                codeSignature = signed,
                verifier = codeVerifier,
                deviceIds = thisDeviceRepository.ids,
                publicKey = public,
                client = client))

        } catch (e: Exception) {
            if (e is CancellationException) { // Canceled prompt by user
                throw e
            }
            deviceService.removeRegistrationFingerprint()
            throw e
        }
    }

    override suspend fun loginWithCode(code: String, verifier: String) =
        login(grand = AuthCodeGrant(
            redirect_uri = client.urls.authorization.orEmpty(),
            code = code,
            code_verifier = verifier,
            scope = client.scope,
            client = client))
    override suspend fun refreshToken() {
        val refresh = tokenStorage.refreshToken ?: throw Exception("Unauthenticated")
        login(grand = RefreshTokenGrant(refreshToken = refresh.value, client = client))
    }

    override suspend fun authorizeClient() = login(grand = ClientCredentialsGrand(client))

    override suspend fun revokeTokens() {
        val accessToken = tokenStorage.accessToken
        val refreshToken = tokenStorage.refreshToken
        tokenStorage.clear()

        if (accessToken != null) {
            load {
                authRepositoryRepository.revoke(
                    token = accessToken,
                    withBasicAuth = client.basicAuth()
                )
            }
        }
        if (refreshToken != null) {
            load {
                authRepositoryRepository.revoke(
                    token = refreshToken,
                    withBasicAuth = client.basicAuth()
                )
            }
        }
    }

    override fun authorizationUrl(pkce: PKCE, andPrompt: String?): Uri {
        if (configuration.authorizationEndPoint.isEmpty())
            throw Exception("Authorization endpoint url is invalid")

        val authRequest = AuthRequest(client, configuration, andPrompt, pkce)

        return Uri.parse(configuration.authorizationEndPoint)
            .buildUpon()
            .apply {
                when(val params = Serializer.toMap(authRequest)) {
                   null -> Unit
                   else -> params.forEach {
                       appendQueryParameter(it.key, it.value.toString())
                   }
                }
            }.build()

    }

    override fun endSessionUrl(): Uri {
        if (configuration.authorizationEndPoint.isEmpty())
            throw Exception("Authorization endpoint url is invalid")

        return Uri.parse(configuration.authorizationEndPoint)
            .buildUpon()
            .appendQueryParameter("id_token_hint", tokenStorage.idToken)
            .appendQueryParameter("post_logout_redirect_uri", client.urls.postLogout)
            .build()
    }

}