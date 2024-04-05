package gr.indice.identity.client

import gr.indice.identity.client.services.AccountService
import gr.indice.identity.client.services.AuthorizationService
import gr.indice.identity.client.services.DevicesService
import gr.indice.identity.client.services.UserRegistrationService
import gr.indice.identity.client.services.UserService
import gr.indice.identity.protocols.Client
import gr.indice.identity.protocols.CurrentDeviceInfoProvider
import gr.indice.identity.protocols.IdentityConfig
import gr.indice.identity.protocols.IdentityEncryptedStorage
import gr.indice.identity.protocols.TokenStorage
import gr.indice.identity.protocols.TokenStorageAccessor
import retrofit2.Retrofit


typealias Options = IdentityClientOptions

/**
 *  The IdentityClient! Encapsulates and manages all the services provided by the Indice.AspNet Identity library that are relevant to a client application.
 *  One instance should be created.
 */
interface IdentityClient {

    val tokens                    : TokenStorageAccessor
    val authorizationService      : AuthorizationService
    val userService               : UserService
    val accountService            : AccountService
    val devicesService            : DevicesService
    val userRegistrationService   : UserRegistrationService
}

data class IdentityClientOptions(
    var maxTrustedDevicesCount: Int = 1
)


object IdentityClientFactory  {

    fun create(
        client: Client,
        configuration: IdentityConfig,
        currentDeviceInfoProvider: CurrentDeviceInfoProvider,
        encryptedStorage: IdentityEncryptedStorage,
        tokenStorage: TokenStorage = TokenStorage.Ephemeral(),
        retrofitClient: Retrofit
    ): IdentityClient =
        IdentityClientImpl(
            client = client,
            configuration = configuration,
            currentDeviceInfoProvider = currentDeviceInfoProvider,
            encryptedStorage = encryptedStorage,
            tokenStorage = tokenStorage,
            retrofitClient = retrofitClient
        )
}
