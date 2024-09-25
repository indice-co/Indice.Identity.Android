package gr.indice.identity.adapters

import gr.indice.identity.utils.Serializer
import okhttp3.ResponseBody


fun <T> ResponseBody.toType(target: Class<T>) =
    Serializer.moshi.adapter(target)
        .run { fromJson(source().peek()) }
