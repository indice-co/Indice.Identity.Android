package gr.indice.identity.models.extensions

import gr.indice.identity.apis.ThisDeviceIds
import gr.indice.identity.models.DeviceAuthentications
import gr.indice.identity.models.TotpDeliveryChannel
import gr.indice.identity.models.TrustDeviceMode
import gr.indice.identity.protocols.Client


fun DeviceAuthentications.AuthorizationRequest.Companion.biometricInit(
    codeChallenge: String, deviceIds: ThisDeviceIds,
    client: Client
) : DeviceAuthentications.AuthorizationRequest {
    return create(codeChallenge, deviceIds, client, requiresReqId = false, mode = TrustDeviceMode.FINGERPRINT)
}

fun DeviceAuthentications.AuthorizationRequest.Companion.biometricAuth(
    codeChallenge: String, deviceIds: ThisDeviceIds,
    client: Client
) : DeviceAuthentications.AuthorizationRequest {
    return create(codeChallenge, deviceIds, client, requiresReqId = true, mode = TrustDeviceMode.FINGERPRINT)
}

fun DeviceAuthentications.AuthorizationRequest.Companion.pinInit(
    codeChallenge: String,
    deviceIds: ThisDeviceIds,
    client: Client
): DeviceAuthentications.AuthorizationRequest {
    return create(codeChallenge, deviceIds, client, requiresReqId = false, mode = TrustDeviceMode.PIN)
}


private fun create(codeChallenge: String,
                   deviceIds: ThisDeviceIds, client: Client, requiresReqId: Boolean,
                   mode: TrustDeviceMode, channel: TotpDeliveryChannel? = null) : DeviceAuthentications.AuthorizationRequest {

    val registrationID = if (requiresReqId && deviceIds.registration != null)  deviceIds.registration else ""

    return DeviceAuthentications.AuthorizationRequest(
        code_challenge = codeChallenge,
        device_id = deviceIds.device,
        mode = mode,
        client_id = client.id,
        scope = client.scope,
        registration_id = registrationID,
        channel = channel
    )
}