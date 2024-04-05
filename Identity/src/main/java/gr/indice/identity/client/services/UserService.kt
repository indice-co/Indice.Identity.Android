package gr.indice.identity.client.services

import gr.indice.identity.apis.UserInfoRepository
import gr.indice.identity.models.UserInfo
import gr.indice.identity.utils.ServiceErrorException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
/** User info. Is it overkill to have a service for only refreshing [UserInfo]? */
interface UserService {

    val userInfo: StateFlow<UserInfo?>
    @Throws(ServiceErrorException::class)
    suspend fun refreshUserInfo()
}

internal class UserServiceImpl(private val userInfoRepository: UserInfoRepository): BaseService(), UserService {
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    override val userInfo = _userInfo.asStateFlow()

    override suspend fun refreshUserInfo() {
        _userInfo.value = load { userInfoRepository.getUserInfo() }
    }

}
