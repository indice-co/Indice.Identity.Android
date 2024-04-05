package gr.indice.identity.apis

import gr.indice.identity.models.ForgotPasswordConfirmation
import gr.indice.identity.models.ForgotPasswordRequest
import gr.indice.identity.models.OtpTokenRequest
import gr.indice.identity.models.PasswordRules
import gr.indice.identity.models.RegisterUserRequest
import gr.indice.identity.models.UpdateEmailRequest
import gr.indice.identity.models.UpdatePasswordRequest
import gr.indice.identity.models.UpdatePhoneRequest
import gr.indice.identity.models.UserNameStatus
import gr.indice.identity.models.UsernameAvailability
import gr.indice.identity.models.ValidatePasswordRequest
import gr.indice.identity.models.ValidateUserNameRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface MyAccountApi {

    @POST
    suspend fun checkUserNameExists(
        @Url url: String,
        @Body userName: ValidateUserNameRequest? = null
    ) : Response<Unit>

    @POST
    suspend fun validatePassword(
        @Url url: String,
        @Body passwordRequest: ValidatePasswordRequest? = null
    ) : Response<PasswordRules>

    @POST
    suspend fun register(
        @Url url: String,
        @Body request: RegisterUserRequest? = null
    ) : Response<Unit>

    @PUT
    suspend fun updateEmail(
        @Url url: String,
        @Body request: UpdateEmailRequest? = null
    ): Response<Unit>

    @PUT
    suspend fun verifyEmail(
        @Url url: String,
        @Body otpRequest: OtpTokenRequest? = null
    ): Response<Unit>

    @PUT
    suspend fun updatePhone(
        @Url url: String,
        @Body request: UpdatePhoneRequest? = null
    ): Response<Unit>

    @PUT
    suspend fun verifyPhone(
        @Url url: String,
        @Body otpRequest: OtpTokenRequest? = null
    ): Response<Unit>

    @POST
    suspend fun forgotPassword(
        @Url url: String,
        @Body forgotPasswordRequest: ForgotPasswordRequest
    ): Response<Unit>

    @PUT
    suspend fun forgotPassword(
        @Url url: String,
        @Body confirmationRequest: ForgotPasswordConfirmation
    ): Response<Unit>

    @PUT
    suspend fun update(
        @Url url: String,
        @Body passwordRequest: UpdatePasswordRequest
    ): Response<Unit>

    companion object {
        fun create(retrofit: Retrofit) : MyAccountApi = retrofit.create()
    }
}

suspend fun MyAccountApi.usernameIsAvailable(
    url: String,
    userName: ValidateUserNameRequest? = null
):Response<UsernameAvailability> {
    return when (checkUserNameExists(url, userName).code()) {
        404 -> Response.success(UsernameAvailability(UserNameStatus.Available))
        else -> Response.success(UsernameAvailability(UserNameStatus.Unavailable))
    }
}