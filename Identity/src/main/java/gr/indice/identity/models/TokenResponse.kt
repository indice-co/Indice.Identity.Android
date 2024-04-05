package gr.indice.identity.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenResponse(
    val access_token: String,
    val expires_in: Int,
    val token_type: String,
    val scope: String,
    val refresh_token: String?,
    val id_token: String?,
)
