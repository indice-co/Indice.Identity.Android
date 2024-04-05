package gr.indice.identity.client.services

import gr.indice.identity.apis.MyAccountRepository
import gr.indice.identity.models.ForgotPasswordConfirmation
import gr.indice.identity.models.ForgotPasswordRequest
import gr.indice.identity.models.OtpTokenRequest
import gr.indice.identity.models.TotpDeliveryChannel
import gr.indice.identity.models.UpdateEmailRequest
import gr.indice.identity.models.UpdatePasswordRequest
import gr.indice.identity.models.UpdatePhoneRequest
import gr.indice.identity.utils.ServiceErrorException
/**
 * Service responsible for updating the users account
 */
interface AccountService {
    /** Update  the user's current phone number */
    @Throws(ServiceErrorException::class)
    suspend fun updatePhone(phone: String, otpChannel: TotpDeliveryChannel? = null): suspend (String) -> Unit
    /** Update  the user's current email */
    @Throws(ServiceErrorException::class)
    suspend fun updateEmail(email: String)
    /** Update the user's current password */
    @Throws(ServiceErrorException::class)
    suspend fun updatePassword(password: UpdatePasswordRequest)
    /** Initiate the forgot password flow */
    @Throws(ServiceErrorException::class)
    suspend fun forgotPasswordInitialize(email: String, returnUrl: String)
    /** Confirm forgot password and set a new one */
    @Throws(ServiceErrorException::class)
    suspend fun forgotPasswordConfirmation(token: String, email: String, password: String, passwordConfirmation: String, returnUrl: String)


}

internal class AccountServiceImpl(
    private val accountRepository: MyAccountRepository,
    private val userService: UserService,
    private val authorizationService: AuthorizationService
) : BaseService(), AccountService {
    override suspend fun updatePhone(phone: String, otpChannel: TotpDeliveryChannel?): suspend (String) -> Unit {

        load { accountRepository.update(UpdatePhoneRequest(phoneNumber = phone, deliveryChannel = otpChannel)) }

        return { otpValue ->
            load { accountRepository.verifyPhone(OtpTokenRequest(otpValue)) }
            authorizationService.refreshToken()
            userService.refreshUserInfo()
        }
    }

    override suspend fun updateEmail(email: String) =
        load { accountRepository.update(UpdateEmailRequest(email = email, returnUrl = null)) }


    override suspend fun updatePassword(password: UpdatePasswordRequest) = load { accountRepository.update(password) }

    override suspend fun forgotPasswordInitialize(
        email: String,
        returnUrl: String
    ) = load { accountRepository.forgot(ForgotPasswordRequest(email, returnUrl)) }

    override suspend fun forgotPasswordConfirmation(
        token: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        returnUrl: String
    ) =
        load {
            accountRepository.forgot(
                ForgotPasswordConfirmation(
                    email = email,
                    newPassword = password,
                    newPasswordConfirmation = passwordConfirmation,
                    returnUrl = returnUrl,
                    token = token
                )
            )
        }

}