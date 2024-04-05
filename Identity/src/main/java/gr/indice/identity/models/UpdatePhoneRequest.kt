package gr.indice.identity.models

data class UpdatePhoneRequest(
    val phoneNumber: String?,
    val deliveryChannel: TotpDeliveryChannel? = null
)
