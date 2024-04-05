package gr.indice.identity.apis

import gr.indice.identity.models.TokenResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.*

interface OpenIdApi {

    interface OAuth2Grant {
        val params: Map<String, String>
        val grantType: String
    }

    @FormUrlEncoded
    @POST
    suspend fun authorize(
        @Url url: String,
        @FieldMap params: Map<String, String>
    ): Response<TokenResponse>

    @FormUrlEncoded
    @POST
    suspend fun endSession(
        @Header("Authorization") authorization : String?,
        @Url url: String,
        @Field("token") token: String?,
        @Field("token_type_hint") tokenHint: String = "access_token"
    ): Response<Unit>


    companion object {
        fun create(retrofit: Retrofit): OpenIdApi =
            retrofit.create()
    }

}