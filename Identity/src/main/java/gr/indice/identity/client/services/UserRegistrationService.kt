package gr.indice.identity.client.services

import gr.indice.identity.apis.MyAccountRepository
import gr.indice.identity.models.PasswordRules
import gr.indice.identity.models.RegisterUserRequest
import gr.indice.identity.models.UsernameAvailability
import gr.indice.identity.models.ValidatePasswordRequest
import gr.indice.identity.models.ValidateUserNameRequest
import gr.indice.identity.utils.ServiceErrorException
/** Registers a new user and aids to the username/password verification prior. */
interface UserRegistrationService {
    @Throws(ServiceErrorException::class)
    suspend fun register(request: RegisterUserRequest)
    @Throws(ServiceErrorException::class)
    suspend fun verifyUserName(userName: String): UsernameAvailability
    @Throws(ServiceErrorException::class)
    suspend fun verifyPassword(password: String): PasswordRules
}
internal class UserRegistrationServiceImpl(private val accountRepository: MyAccountRepository): BaseService(), UserRegistrationService {
    override suspend fun register(request: RegisterUserRequest) = load { accountRepository.register(request) }

    override suspend fun verifyUserName(userName: String) = load { accountRepository.verify(ValidateUserNameRequest(userName)) }

    override suspend fun verifyPassword(password: String) = load { accountRepository.verify(ValidatePasswordRequest(password = password)) }

}