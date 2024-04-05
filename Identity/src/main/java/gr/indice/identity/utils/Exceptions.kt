package gr.indice.identity.utils

import okhttp3.ResponseBody

/**
 * Throw when something goes wrong with Identity.
 * @param code [Int]
 * @param error [ResponseBody]
 */
class ServiceErrorException(val code: Int?, val error: ResponseBody): Exception()