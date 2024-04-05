package gr.indice.identity.models.extensions

import gr.indice.identity.apis.ThisDeviceIds
import gr.indice.identity.apis.ThisDeviceInfo
import gr.indice.identity.models.DeviceAuthentications
import gr.indice.identity.models.DevicePlatform
import gr.indice.identity.models.TrustDeviceMode

fun DeviceAuthentications.RegistrationRequest.Companion.biometric(
    code: String, codeVerifier: String, codeSignature: String, deviceIds: ThisDeviceIds,
    deviceInfo: ThisDeviceInfo, publicPem: String, otp: String?
) : DeviceAuthentications.RegistrationRequest {
    return DeviceAuthentications.RegistrationRequest(
        code = code,
        code_verifier = codeVerifier,
        code_signature = codeSignature,
        mode = TrustDeviceMode.FINGERPRINT,
        device_id = deviceIds.device,
        device_name = deviceInfo.name,
        device_platform = DevicePlatform.Android,
        otp = otp,
        public_key = publicPem,
        pin = null
    )
}

fun DeviceAuthentications.RegistrationRequest.Companion.pin(
    code: String, codeVerifier: String, codeSignature: String, deviceIds: ThisDeviceIds,
    deviceInfo: ThisDeviceInfo, devicePin: String, otp: String?
) : DeviceAuthentications.RegistrationRequest {
    return DeviceAuthentications.RegistrationRequest(
        code = code,
        code_verifier = codeVerifier,
        code_signature = codeSignature,
        mode = TrustDeviceMode.FINGERPRINT,
        device_id = deviceIds.device,
        device_name = deviceInfo.name,
        device_platform = DevicePlatform.Android,
        otp = otp,
        public_key = null,
        pin = devicePin
    )
}