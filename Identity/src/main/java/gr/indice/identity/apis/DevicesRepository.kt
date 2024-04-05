package gr.indice.identity.apis

import gr.indice.identity.models.CreateDeviceRequest
import gr.indice.identity.models.DeviceAuthentications
import gr.indice.identity.models.DeviceClientType
import gr.indice.identity.models.SwapDeviceRequest
import gr.indice.identity.models.UpdateDeviceRequest
import gr.indice.identity.protocols.IdentityConfig

class DevicesRepository(private val configuration: IdentityConfig, private val devicesApi: DevicesApi) {

    suspend fun authorize(authRequest: DeviceAuthentications.AuthorizationRequest) =
        devicesApi.deviceAuthorization(
            url = configuration.deviceRegistration.authorizeEndpoint,
            code_challenge = authRequest.code_challenge,
            device_id = authRequest.device_id,
            registration_id = authRequest.registration_id.toString(),
            mode = authRequest.mode,
            client_id = authRequest.client_id,
            scope = authRequest.scope
        )

    suspend fun initialize(authRequest: DeviceAuthentications.AuthorizationRequest) =
        devicesApi.deviceRegistrationInitialization(
            url = configuration.deviceRegistration.initializeEndpoint,
            code_challenge = authRequest.code_challenge,
            device_id = authRequest.device_id,
            mode = authRequest.mode,
            channel = authRequest.channel,

            )
    suspend fun complete(request: DeviceAuthentications.RegistrationRequest) =
        devicesApi.deviceRegistrationComplete(
            url = configuration.deviceRegistration.completionEndpoint,
            code = request.code,
            code_verifier = request.code_verifier,
            code_signature = request.code_signature,
            mode = request.mode,
            device_id = request.device_id,
            device_name = request.device_name,
            device_platform = request.device_platform,
            otp = request.otp,
            public_key = request.public_key,
            pin = request.pin,
        )

    suspend fun devices() = devicesApi.getDevices("${configuration.baseUrl}/api/my/devices", filterClientType = DeviceClientType.NATIVE.name)
    suspend fun device(deviceId: String) = devicesApi.getDevice("${configuration.baseUrl}/api/my/devices/$deviceId")
    suspend fun create(device: CreateDeviceRequest) = devicesApi.registerDevice("${configuration.baseUrl}/api/my/devices", device)
    suspend fun update(deviceId: String, data: UpdateDeviceRequest) = devicesApi.updateDevice("${configuration.baseUrl}/api/my/devices/$deviceId", data)
    suspend fun delete(deviceId: String) = devicesApi.unRegisterDevice("${configuration.baseUrl}/api/my/devices/$deviceId")
    suspend fun trust(deviceId: String, swapDeviceId: String?) = devicesApi.trust("${configuration.baseUrl}/api/my/devices/$deviceId/trust", SwapDeviceRequest(swapDeviceId))
    suspend fun unTrust(deviceId: String) = devicesApi.unTrust("${configuration.baseUrl}/api/my/devices/$deviceId/untrust")
}