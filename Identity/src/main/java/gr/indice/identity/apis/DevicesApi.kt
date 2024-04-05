package gr.indice.identity.apis

import gr.indice.identity.models.ChallengeResponse
import gr.indice.identity.models.CreateDeviceRequest
import gr.indice.identity.models.DeviceInfo
import gr.indice.identity.models.DeviceInfoResultSet
import gr.indice.identity.models.DevicePlatform
import gr.indice.identity.models.RegistrationResult
import gr.indice.identity.models.SwapDeviceRequest
import gr.indice.identity.models.TotpDeliveryChannel
import gr.indice.identity.models.TrustDeviceMode
import gr.indice.identity.models.UpdateDeviceRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Url

interface DevicesApi {

    @POST
    @FormUrlEncoded
    suspend fun deviceRegistrationInitialization(
        @Url url: String,
        @Field("code_challenge") code_challenge: String?,
        @Field("device_id") device_id: String?,
        @Field("mode") mode: TrustDeviceMode?,
        @Field("channel") channel: TotpDeliveryChannel?
    ): Response<ChallengeResponse>

    @POST
    @FormUrlEncoded
    suspend fun deviceRegistrationComplete(
        @Url url: String,
        @Field("code") code: String?,
        @Field("code_verifier") code_verifier: String?,
        @Field("code_signature") code_signature: String?,
        @Field("mode") mode: TrustDeviceMode?,
        @Field("device_id") device_id: String?,
        @Field("device_name") device_name: String?,
        @Field("device_platform") device_platform: DevicePlatform?,
        @Field("otp") otp: String?,
        @Field("public_key") public_key: String?,
        @Field("pin") pin: String?,
    ): Response<RegistrationResult>


    @POST
    @FormUrlEncoded
    suspend fun deviceAuthorization(
        @Url url: String,
        @Field("code_challenge") code_challenge: String?,
        @Field("device_id") device_id: String?,
        @Field("registration_id") registration_id: String?,
        @Field("mode") mode: TrustDeviceMode?,
        @Field("client_id") client_id: String?,
        @Field("scope") scope: String?,
    ): Response<ChallengeResponse>


    @GET
    suspend fun getDevices(
        @Url url: String,
        @Query("Filter.IsPushNotificationEnabled") filterIsPushNotificationEnabled: Boolean? = null,
        @Query("Filter.IsTrusted") filterIsTrusted: Boolean? = null,
        @Query("Filter.Blocked") filterBlocked: Boolean? = null,
        @Query("Filter.ClientType") filterClientType: String? = null,
        @Query("Filter.IsPendingTrustActivation") filterIsPendingTrustActivation: Boolean? = null,
        @Query("Page") page: Int? = null,
        @Query("Size") size: Int? = null,
        @Query("Sort") sort: String? = null,
        @Query("Search") search: String? = null
    ): Response<DeviceInfoResultSet>

    @GET
    suspend fun getDevice(
        @Url url: String
    ): Response<DeviceInfo>

    @POST
    suspend fun registerDevice(
        @Url url: String,
        @Body parameters: CreateDeviceRequest
    ) : Response<Unit>

    @PUT
    suspend fun updateDevice(
        @Url url: String,
        @Body parameters: UpdateDeviceRequest
    ) : Response<Unit>

    @HTTP(method ="DELETE", hasBody = true)
    suspend fun unRegisterDevice(
        @Url url: String
    ): Response<Unit>

    @PUT
    suspend fun trust(
        @Url url: String,
        @Body otherDeviceId: SwapDeviceRequest
    ): Response<Unit>

    @PUT
    suspend fun unTrust(
        @Url url: String
    ): Response<Unit>


    companion object {
        fun create(retrofit: Retrofit): DevicesApi = retrofit.create()
    }
}