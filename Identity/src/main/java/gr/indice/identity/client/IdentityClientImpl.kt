package gr.indice.identity.client

import gr.indice.identity.client.services.AccountService
import gr.indice.identity.client.services.AccountServiceImpl
import gr.indice.identity.client.services.AuthorizationService
import gr.indice.identity.client.services.AuthorizationServiceImpl
import gr.indice.identity.client.services.DevicesService
import gr.indice.identity.client.services.DevicesServiceImpl
import gr.indice.identity.client.services.UserRegistrationService
import gr.indice.identity.client.services.UserRegistrationServiceImpl
import gr.indice.identity.client.services.UserService
import gr.indice.identity.client.services.UserServiceImpl
import gr.indice.identity.factories.DefaultRepositoryFactory
import gr.indice.identity.factories.RepositoryFactory
import gr.indice.identity.protocols.Client
import gr.indice.identity.protocols.CurrentDeviceInfoProvider
import gr.indice.identity.protocols.IdentityConfig
import gr.indice.identity.protocols.IdentityEncryptedStorage
import gr.indice.identity.protocols.TokenStorage
import gr.indice.identity.protocols.TokenStorageAccessor
import retrofit2.Retrofit

class IdentityClientImpl(
    private val client: Client,
    private val configuration: IdentityConfig,
    private val options: IdentityClientOptions = IdentityClientOptions(maxTrustedDevicesCount = 1),
    private val currentDeviceInfoProvider: CurrentDeviceInfoProvider,
    private val encryptedStorage: IdentityEncryptedStorage,
    private val tokenStorage: TokenStorage = TokenStorage.Ephemeral(),
    private val retrofitClient: Retrofit
) : IdentityClient {

    private val repositories by lazy { Repositories(DefaultRepositoryFactory(), configuration, encryptedStorage, currentDeviceInfoProvider, retrofitClient) }

    override val tokens: TokenStorageAccessor = tokenStorage

    override val authorizationService: AuthorizationService by lazy { AuthorizationServiceImpl(
        authRepositoryRepository = repositories.authRepository,
        devicesRepository = repositories.deviceRepository,
        thisDeviceRepository = repositories.thisDeviceRepository,
        deviceService = devicesService,
        tokenStorage = tokenStorage,
        client = client,
        configuration = configuration
    ) }
    override val userService: UserService by lazy { UserServiceImpl(repositories.userRepository) }
    override val accountService: AccountService by lazy { AccountServiceImpl(repositories.accountRepository, userService, authorizationService) }
    override val devicesService: DevicesService by lazy { DevicesServiceImpl(options, repositories.thisDeviceRepository, repositories.deviceRepository, encryptedStorage, client) }
    override val userRegistrationService: UserRegistrationService by lazy { UserRegistrationServiceImpl(repositories.accountRepository) }
}


private class Repositories(
    private val repositoryFactory: RepositoryFactory,
    private val configuration: IdentityConfig,
    private val encryptedStorage: IdentityEncryptedStorage,
    private val currentDeviceInfoProvider: CurrentDeviceInfoProvider,
    private val retrofitClient: Retrofit
) {
    val authRepository by lazy { repositoryFactory.authRepository(configuration, retrofitClient) }
    val accountRepository by lazy { repositoryFactory.myAccountRepository(configuration, retrofitClient) }
    val deviceRepository by lazy { repositoryFactory.devicesRepository(configuration, retrofitClient) }
    val userRepository by lazy { repositoryFactory.userRepository(configuration, retrofitClient) }
    val thisDeviceRepository by lazy {  repositoryFactory.thisDeviceRepository(currentDeviceInfoProvider, encryptedStorage)  }

}