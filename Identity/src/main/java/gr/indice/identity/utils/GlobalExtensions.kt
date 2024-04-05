package gr.indice.identity.utils

import gr.indice.identity.models.DeviceInfo

fun String.Companion.random(length: Int) : String {
    val base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    var randomString = ""

    repeat(length) {
        randomString += base.random()
    }

    return randomString
}

fun List<DeviceInfo>.getThisDevice(deviceId: String?): DeviceInfo? = firstOrNull { it.deviceId == deviceId }