package gr.indice.identity.protocols

import android.util.Base64

data class Client (
    val id: String,
    val secret: String?,
    val scope: String,
    val urls: Urls
){
    data class Urls(
        val authorization: String? = null,
        val postLogout: String? = null
    )
}

fun Client.basicAuth() : String {
    return "Basic ${Base64.encodeToString("${id}:${secret}".toByteArray(), Base64.NO_WRAP)}"
}