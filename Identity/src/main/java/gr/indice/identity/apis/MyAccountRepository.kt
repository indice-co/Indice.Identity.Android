package gr.indice.identity.apis

import gr.indice.identity.models.ForgotPasswordConfirmation
import gr.indice.identity.models.ForgotPasswordRequest
import gr.indice.identity.models.OtpTokenRequest
import gr.indice.identity.models.RegisterUserRequest
import gr.indice.identity.models.UpdateEmailRequest
import gr.indice.identity.models.UpdatePasswordRequest
import gr.indice.identity.models.UpdatePhoneRequest
import gr.indice.identity.models.ValidatePasswordRequest
import gr.indice.identity.models.ValidateUserNameRequest
import gr.indice.identity.protocols.IdentityConfig

class MyAccountRepository(private val configuration: IdentityConfig, private val accountApi: MyAccountApi) {

    suspend fun register(request: RegisterUserRequest) = accountApi.register("${configuration.baseUrl}/api/account/register",request)
    suspend fun verify(password: ValidatePasswordRequest) = accountApi.validatePassword("${configuration.baseUrl}/api/account/validate-password",password)
    suspend fun verify(userName: ValidateUserNameRequest) = accountApi.usernameIsAvailable("${configuration.baseUrl}/api/account/username-exists", userName)
    suspend fun forgot(password: ForgotPasswordRequest) = accountApi.forgotPassword("${configuration.baseUrl}/api/account/forgot-password", password)
    suspend fun forgot(passwordConfirmation: ForgotPasswordConfirmation) = accountApi.forgotPassword("${configuration.baseUrl}/api/account/forgot-password/confirmation", passwordConfirmation)
    suspend fun update(password: UpdatePasswordRequest) = accountApi.update("${configuration.baseUrl}/api/my/account/password", password)
    suspend fun update(email: UpdateEmailRequest) = accountApi.updateEmail("${configuration.baseUrl}/api/my/account/email", email)
    suspend fun update(phone: UpdatePhoneRequest) = accountApi.updatePhone("${configuration.baseUrl}/api/my/account/phone-number", phone)
    suspend fun verifyEmail(otpRequest: OtpTokenRequest) = accountApi.verifyEmail("${configuration.baseUrl}/api/my/account/email/confirmation", otpRequest)
    suspend fun verifyPhone(otpRequest: OtpTokenRequest) = accountApi.verifyPhone("${configuration.baseUrl}/api/my/account/phone-number/confirmation", otpRequest)
}