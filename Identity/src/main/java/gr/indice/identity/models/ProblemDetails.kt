package gr.indice.identity.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProblemDetails(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val detail: String? = null,
    val code: String? = null,
    val errors: Map<String, List<String>>? = null,
    @Json(name = "error_description")
    val errorDescription: String? = null,
    @Json(name = "authorization_details")
    val authorizationDetails: Any? = null
)
{
    val description : String get() {
        if (!errors.isNullOrEmpty()) {
            return errors
                .flatMap { it.value }
                .joinToString(separator = "\n")
        }
        return detail ?: errorDescription ?: "Unknown Business Error Occurred"
    }
}