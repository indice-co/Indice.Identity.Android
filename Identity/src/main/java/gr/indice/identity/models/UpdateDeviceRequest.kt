package gr.indice.identity.models

data class UpdateDeviceRequest(val name: String,
                               val deviceName: String,
                               val pnsHandle: String?,
                               val isPushNotificationsEnabled: Boolean?,
                               val tags: List<String>?,
                               val model: String?,
                               val osVersion: String?,
                               val data: String?)
{
    companion object
}
