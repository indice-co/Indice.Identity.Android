package gr.indice.identity.models

data class CreateDeviceRequest(val deviceId: String,
                               val name: String,
                               val platform: DevicePlatform,
                               val clientType: DeviceClientType,
                               val pnsHandle: String?,
                               val tags: List<String>?,
                               val model: String?,
                               val osVersion: String?,
                               val data: String?)
{
    companion object
}
