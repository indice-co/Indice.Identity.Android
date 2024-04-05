package gr.indice.identity.models

import java.time.OffsetDateTime

data class DeviceInfo(
    val deviceId: String?,
    val platform: DevicePlatform?,
    val name: String?,
    val model: String?,
    val osVersion: String?,
    val dateCreated: OffsetDateTime?,
    val lastSignInDate: OffsetDateTime?,
    val isPushNotificationsEnabled: Boolean?,
    val supportsPinLogin: Boolean?,
    val supportsFingerprintLogin: Boolean?,
    val requiresPassword: Boolean?,
    val trustActivationDate: OffsetDateTime?,
    val isTrusted: Boolean?,
    val canActivateDeviceTrust: Boolean?,
    val data: String?,
    val clientType: DeviceClientType,
    val pnsHandler: String?
)