package gr.indice.identity.client.services

import gr.indice.identity.adapters.toType
import gr.indice.identity.models.ErrorResponse
import gr.indice.identity.models.getIdentityError
import gr.indice.identity.utils.LoginServiceErrorException
import gr.indice.identity.utils.ServiceErrorException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
internal abstract class BaseService {

    @Throws(ServiceErrorException::class, LoginServiceErrorException::class)
    protected suspend inline fun <reified T> load(crossinline request: suspend () -> Response<T>): T {
        val response = withContext(Dispatchers.IO) {
            request()
        }

        if (response.isSuccessful) {
            if (T::class.java == Unit::class.java) {
                return Unit as T
            }
            return response.body()!!
        } else {
            val loginError = response.errorBody()?.getIdentityError()

            if (loginError != null) {
                throw LoginServiceErrorException(response.code(), loginError)
            } else {
                throw ServiceErrorException(response.code(), response.errorBody()!!)
            }

        }
    }
}

