package gr.indice.identity.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.*

class UUIDAdapter {
    @ToJson
    fun toJson(value: UUID) = value.toString()

    @FromJson
    fun fromJson(value: String) = UUID.fromString(value)

}