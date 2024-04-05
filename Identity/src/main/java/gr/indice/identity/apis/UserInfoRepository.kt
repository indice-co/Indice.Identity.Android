package gr.indice.identity.apis

import gr.indice.identity.protocols.IdentityConfig

class UserInfoRepository(private val configuration: IdentityConfig, private val userInfoApi: UserInfoApi) {

    suspend fun getUserInfo() = userInfoApi.getUserInfo("${configuration.baseUrl}/connect/userinfo")

}