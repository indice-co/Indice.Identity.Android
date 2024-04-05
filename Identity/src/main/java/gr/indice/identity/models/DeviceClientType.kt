package gr.indice.identity.models

import com.squareup.moshi.Json

enum class DeviceClientType {

    @Json(name = "Browser")
    BROWSER,

    @Json(name = "Native")
    NATIVE

}