package gr.indice.identity.models

import gr.indice.identity.adapters.toType
import okhttp3.ResponseBody


sealed interface IdentityErrorMap {
    data object LockedOut: IdentityErrorMap //104
    data object NotAllowed: IdentityErrorMap //105
    data object InvalidCredentials: IdentityErrorMap //106
    data object NotFound: IdentityErrorMap //107
    data object Blocked: IdentityErrorMap //108
    data object Traveler: IdentityErrorMap //109
    data object TemporaryPasswordExpired: IdentityErrorMap //110
    data object NotMobileClient: IdentityErrorMap //204
    data object MissingDeviceId: IdentityErrorMap //205
    data object DeviceNotFound: IdentityErrorMap //206
    data class Custom(val error: ErrorResponse): IdentityErrorMap
}


fun ResponseBody.getIdentityError(): IdentityErrorMap? {
    return runCatching {
        toType(ErrorResponse::class.java)?.let {
            when(it.error_description) {
                "104" -> IdentityErrorMap.LockedOut
                "105" -> IdentityErrorMap.NotAllowed
                "106" -> IdentityErrorMap.InvalidCredentials
                "107" -> IdentityErrorMap.NotFound
                "108" -> IdentityErrorMap.Blocked
                "109" -> IdentityErrorMap.Traveler
                "110" -> IdentityErrorMap.TemporaryPasswordExpired
                "204" -> IdentityErrorMap.NotMobileClient
                "205" -> IdentityErrorMap.MissingDeviceId
                "206" -> IdentityErrorMap.DeviceNotFound
                else -> IdentityErrorMap.Custom(it)
            }
        }
    }.getOrNull()
}

