package gr.indice.identity.models

import gr.indice.identity.models.enums.PasswordExpirationPolicy
import java.time.OffsetDateTime

data class UserInfo(
    val sub: String,
    val name: String?,
    val given_name: String?,
    val family_name: String?,
    val profile_id: String?,
    val otp_channel: TotpDeliveryChannel?,
    val otp_channel_disabled: String?,
    val password_expiration_date: OffsetDateTime?,
    val password_expiration_policy: PasswordExpirationPolicy?,
    val admin: Boolean?,
    val preferred_username: String?,
    val email: String?,
    val email_verified: Boolean?,
    val phone_number: String?,
    val phone_number_verified: Boolean?,
    val max_devices_count: String?
)
