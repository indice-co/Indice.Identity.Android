package gr.indice.identity.models.extensions

import gr.indice.identity.apis.ThisDeviceRepository
import gr.indice.identity.models.CreateDeviceRequest
import gr.indice.identity.models.DeviceClientType
import gr.indice.identity.models.DevicePlatform
import gr.indice.identity.models.UpdateDeviceRequest

fun UpdateDeviceRequest.Companion.from(service: ThisDeviceRepository, pnsHandle: String?, customTags: List<String>? = null): UpdateDeviceRequest {
    val info = service.info
    return UpdateDeviceRequest(
        name = info.name,
        deviceName = info.name,
        isPushNotificationsEnabled = pnsHandle != null,
        tags = customTags,
        pnsHandle = pnsHandle,
        model = info.model,
        osVersion = info.osVersion,
        data = null
    )
}

fun CreateDeviceRequest.Companion.from(service: ThisDeviceRepository, pnsHandle: String?, customTags: List<String>? = null): CreateDeviceRequest {
    val ids = service.ids
    val info = service.info
    return CreateDeviceRequest(
        deviceId = ids.device,
        name = info.name,
        platform = DevicePlatform.Android,
        clientType = DeviceClientType.NATIVE,
        pnsHandle = pnsHandle,
        tags = customTags,
        model = info.model,
        osVersion = info.osVersion,
        data = null
    )
}