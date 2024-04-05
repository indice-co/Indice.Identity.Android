package gr.indice.identity.models.enums

import com.squareup.moshi.Json

enum class AcrValues(val value: String) {
    @Json(name = "idp:Apple")
    APPLE(value = "idp:Apple"),
    @Json(name = "idp:Google")
    GOOGLE(value = "idp:Google"),
    @Json(name = "idp:Microsoft")
    MICROSOFT(value = "idp:Microsoft")
}