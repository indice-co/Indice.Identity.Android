package gr.indice.identity.client.services

import gr.indice.identity.utils.ServiceErrorException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
internal abstract class BaseService {

    @Throws(ServiceErrorException::class)
    protected suspend inline fun <reified T> load(crossinline request: suspend () -> Response<T>): T {
        val response = withContext(Dispatchers.IO) {
            request()
        }

        if (response.isSuccessful) {
            if (T::class.java == Unit::class.java) {
                return Unit as T
            }
            return response.body()!!
        }

       throw ServiceErrorException(response.code(), response.errorBody()!!)

    }
}

