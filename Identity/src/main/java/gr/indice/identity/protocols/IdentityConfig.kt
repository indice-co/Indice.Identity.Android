package gr.indice.identity.protocols

data class IdentityConfig (

    var baseUrl: String,
    var authorizationEndPoint: String,
    var tokenEndpoint         : String,
    var revokeEndpoint        : String,
    var logoutEndpoint        : String,

    var authCodeResponseType  : String,
    var authCodeResponseMode  : String,
    var deviceRegistration    : DeviceTrustEndpoint

){
    data class DeviceTrustEndpoint(
        val initializeEndpoint: String,
        val completionEndpoint: String,
        val authorizeEndpoint: String
    )
}