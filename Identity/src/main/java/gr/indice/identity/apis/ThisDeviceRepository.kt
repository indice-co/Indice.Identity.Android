package gr.indice.identity.apis

import gr.indice.identity.protocols.CurrentDeviceInfoProvider
import gr.indice.identity.protocols.IdentityEncryptedStorage
import gr.indice.identity.protocols.StorageKey
import gr.indice.identity.utils.SharedIdentityKeys.devicePinKey
import gr.indice.identity.utils.SharedIdentityKeys.hasFingerPrint
import gr.indice.identity.utils.SharedIdentityKeys.registrationIdKey
import gr.indice.identity.utils.SharedIdentityKeys.uniqueKey
import java.util.UUID

class ThisDeviceRepository(
    private val currentDeviceInfoProvider: CurrentDeviceInfoProvider,
    private val encryptedStorage: IdentityEncryptedStorage
) : ThisDeviceApi {


    override val ids: Ids
        get() = Ids(
            device = deviceIdGetter(),
            registration = registrationIdGetter())

    override val info: Info
        get() = Info(
            name = currentDeviceInfoProvider.name,
            model = currentDeviceInfoProvider.model,
            osVersion = currentDeviceInfoProvider.osVersion)

    override fun resetIds() {
        encryptedStorage.run {
            remove(listOf(
                StorageKey.uniqueKey,
                StorageKey.registrationIdKey,
                StorageKey.hasFingerPrint,
                StorageKey.devicePinKey)
            )
        }
    }

    override fun update(registrationId: String?) {
       if (registrationId != null)
           encryptedStorage.store(StorageKey.registrationIdKey, registrationId)
       else
           encryptedStorage.remove(StorageKey.registrationIdKey)
    }

    private fun deviceIdGetter() : String {
        encryptedStorage.read(StorageKey.uniqueKey)?.let { uniqueId ->
            return uniqueId
        }
        val newDeviceId = UUID.randomUUID().toString()
        encryptedStorage.store(StorageKey.uniqueKey, newDeviceId)
        return newDeviceId
    }

    private fun registrationIdGetter() : String? {
        return encryptedStorage.read(StorageKey.registrationIdKey)
    }
}