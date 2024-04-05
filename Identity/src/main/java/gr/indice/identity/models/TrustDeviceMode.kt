package gr.indice.identity.models

import com.squareup.moshi.Json

enum class TrustDeviceMode {
    @Json(name = "fingerprint")
    FINGERPRINT,

    @Json(name = "pin")
    PIN
}