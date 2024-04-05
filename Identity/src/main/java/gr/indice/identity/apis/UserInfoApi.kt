package gr.indice.identity.apis

import gr.indice.identity.models.UserInfo
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Url

interface UserInfoApi {

    @GET
    suspend fun getUserInfo(@Url url: String): Response<UserInfo>

    companion object {
        fun create(retrofit: Retrofit) : UserInfoApi = retrofit.create()
    }
}