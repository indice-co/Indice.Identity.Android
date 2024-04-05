package gr.indice.identity.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import gr.indice.identity.adapters.OffsetDateTimeAdapter
import gr.indice.identity.adapters.UUIDAdapter

object Serializer {

    @JvmStatic
    val moshiBuilder: Moshi.Builder = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(OffsetDateTimeAdapter())
        .add(UUIDAdapter())
        // .add(OffsetDateTimeAdapter())
        // .add(LocalDateTimeAdapter())
        // .add(LocalDateAdapter())
        // .add(UUIDAdapter())
        // .add(ByteArrayAdapter())
        // .add(URIAdapter())
        // .add(BigDecimalAdapter())
        // .add(BigIntegerAdapter())

    @JvmStatic
    val moshi: Moshi by lazy {
        moshiBuilder.build()
    }

    inline fun <reified T> fromJson(json: String) : T? {
        return try {
            moshi.adapter(T::class.java).fromJson(json)
        } catch (e: Exception) { null }
    }

    inline fun <reified T> fromMap(map: Map<String, Any>) : T? {
        return toJson(map)?.let { json ->
            fromJson(json)
        }
    }

    inline fun <reified T> toJson(obj: T) : String? {
        return try {
            moshi.adapter(T::class.java).toJson(obj)
        } catch (e: Exception) { null }
    }

    inline fun <reified T> toMap(obj: T) : Map<String, Any>? {
        return toJson(obj)?.let { json ->
            fromJson(json)
        }
    }
}