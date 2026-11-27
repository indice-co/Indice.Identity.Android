package gr.indice.identity.utils

import gr.indice.identity.models.IdentityErrorMap
import okhttp3.ResponseBody
import java.io.IOException

/**
 * Throw when something goes wrong with Identity.
 * @param code [Int]
 * @param error [ResponseBody]
 */
class ServiceErrorException(val code: Int?, val error: ResponseBody): IOException()
class LoginServiceErrorException(val code: Int?, val errorMap: IdentityErrorMap): IOException()