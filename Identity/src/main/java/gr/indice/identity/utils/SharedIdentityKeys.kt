package gr.indice.identity.utils

import gr.indice.identity.protocols.StorageKey

object SharedIdentityKeys {

    val StorageKey.Companion.hasFingerPrint get() = StorageKey("device_registration_fingerprint")
    val StorageKey.Companion.devicePinKey get() = StorageKey("device_registration_device_pin")
    val StorageKey.Companion.uniqueKey get() = StorageKey("device_id_key")
    val StorageKey.Companion.registrationIdKey get() = StorageKey("registration_id_key")
}