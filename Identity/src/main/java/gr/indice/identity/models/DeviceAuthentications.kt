package gr.indice.identity.models

typealias Platform = DevicePlatform
typealias Mode = TrustDeviceMode

class DeviceAuthentications {

    data class AuthorizationRequest(
        val code_challenge: String ,
        val device_id: String ,
        val mode: Mode,
        val client_id: String,
        val scope: String,
        val registration_id: String? ,
        val channel: TotpDeliveryChannel? = null
    ){
        companion object
    }

    data class RegistrationRequest(val code: String?,
                                   val code_verifier: String?,
                                   val code_signature: String?,
                                   val mode: Mode?,
                                   val device_id: String?,
                                   val device_name: String?,
                                   val device_platform: Platform?,
                                   val otp: String?,
                                   val public_key: String?,
                                   val pin: String?
    ){
        companion object
    }
}
