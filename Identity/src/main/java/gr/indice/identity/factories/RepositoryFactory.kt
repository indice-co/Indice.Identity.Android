package gr.indice.identity.factories

import gr.indice.identity.apis.AuthRepositoryRepository
import gr.indice.identity.apis.DevicesApi
import gr.indice.identity.apis.DevicesRepository
import gr.indice.identity.apis.MyAccountApi
import gr.indice.identity.apis.MyAccountRepository
import gr.indice.identity.apis.OpenIdApi
import gr.indice.identity.apis.ThisDeviceRepository
import gr.indice.identity.apis.UserInfoApi
import gr.indice.identity.apis.UserInfoRepository
import gr.indice.identity.protocols.CurrentDeviceInfoProvider
import gr.indice.identity.protocols.IdentityConfig
import gr.indice.identity.protocols.IdentityEncryptedStorage
import retrofit2.Retrofit

interface RepositoryFactory {
    fun authRepository(configuration: IdentityConfig, retrofitClient: Retrofit): AuthRepositoryRepository
    fun userRepository(configuration: IdentityConfig, retrofitClient: Retrofit): UserInfoRepository
    fun myAccountRepository(configuration: IdentityConfig, retrofitClient: Retrofit): MyAccountRepository
    fun devicesRepository(configuration: IdentityConfig, retrofitClient: Retrofit): DevicesRepository
    fun thisDeviceRepository(currentDeviceInfoProvider: CurrentDeviceInfoProvider, encryptedStorage: IdentityEncryptedStorage): ThisDeviceRepository
}
class DefaultRepositoryFactory:  RepositoryFactory {
    override fun authRepository(configuration: IdentityConfig, retrofitClient: Retrofit): AuthRepositoryRepository =
        AuthRepositoryRepository(configuration, OpenIdApi.create(retrofitClient))
    override fun userRepository(configuration: IdentityConfig, retrofitClient: Retrofit): UserInfoRepository =
        UserInfoRepository(configuration, UserInfoApi.create(retrofitClient))
    override fun myAccountRepository(configuration: IdentityConfig, retrofitClient: Retrofit): MyAccountRepository =
        MyAccountRepository(configuration, MyAccountApi.create(retrofitClient))
    override fun devicesRepository(configuration: IdentityConfig, retrofitClient: Retrofit): DevicesRepository =
        DevicesRepository(configuration, DevicesApi.create(retrofitClient))
    override fun thisDeviceRepository(currentDeviceInfoProvider: CurrentDeviceInfoProvider, encryptedStorage: IdentityEncryptedStorage): ThisDeviceRepository =
        ThisDeviceRepository(currentDeviceInfoProvider = currentDeviceInfoProvider, encryptedStorage = encryptedStorage)
}