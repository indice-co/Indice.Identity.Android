package gr.indice.identity.apis

data class ThisDeviceIds(var device: String, var registration: String?)
data class ThisDeviceInfo(var name: String, var model: String, var osVersion: String)

typealias Ids = ThisDeviceIds
typealias Info = ThisDeviceInfo

interface ThisDeviceApi {
    val ids  : Ids
    val info : Info

    fun resetIds()

    fun update(registrationId: String?)
}