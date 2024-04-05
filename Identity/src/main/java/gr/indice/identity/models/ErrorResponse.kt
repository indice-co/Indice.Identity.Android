package gr.indice.identity.models

// @JsonClass(generateAdapter = true)
data class ErrorResponse(
    val error: String,
    val error_description: String? = null
)