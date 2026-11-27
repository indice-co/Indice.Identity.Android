package gr.indice.identity.utils

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import gr.indice.identity.adapters.OffsetDateTimeAdapter
import gr.indice.identity.adapters.UUIDAdapter
import gr.indice.identity.utils.Serializer.moshiBuilder
import java.lang.reflect.Type
import java.math.BigDecimal

object Serializer {

    @JvmStatic
    val moshiBuilder: Moshi.Builder = Moshi.Builder()
        //.add(SmartAnyAdapterFactory())
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

    class SmartAnyAdapterFactory : JsonAdapter.Factory {
        override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
            val raw = Types.getRawType(type)

            return when (raw) {
                Any::class.java -> SmartNumberAnyAdapter()
                else -> null
            }
        }
    }

    class SmartNumberAnyAdapter() : JsonAdapter<Any>() {
        override fun fromJson(reader: JsonReader): Any? {
            return when (reader.peek()) {
                JsonReader.Token.BEGIN_OBJECT -> {
                    val map = mutableMapOf<String, Any?>()
                    reader.beginObject()
                    while (reader.hasNext()) {
                        map[reader.nextName()] = fromJson(reader)
                    }
                    reader.endObject()
                    map
                }

                JsonReader.Token.BEGIN_ARRAY -> {
                    val list = mutableListOf<Any?>()
                    reader.beginArray()
                    while (reader.hasNext()) {
                        list.add(fromJson(reader))
                    }
                    reader.endArray()
                    list
                }

                JsonReader.Token.STRING  -> reader.nextString()
                JsonReader.Token.BOOLEAN -> reader.nextBoolean()
                JsonReader.Token.NULL    -> reader.nextNull()
                JsonReader.Token.NUMBER  -> parseNumber(reader.nextString())

                else -> throw JsonDataException("Unexpected token ${reader.peek()}")
            }
        }

        override fun toJson(writer: JsonWriter, value: Any?) {
            when (value) {
                null -> writer.nullValue()
                is Number -> writer.value(value)
                is Boolean -> writer.value(value)
                is String -> writer.value(value)

                is List<*> -> {
                    writer.beginArray()
                    value.forEach { toJson(writer, it) }
                    writer.endArray()
                }

                is Map<*, *> -> {
                    writer.beginObject()
                    value.forEach { (k, v) ->
                        writer.name(k.toString())
                        toJson(writer, v)
                    }
                    writer.endObject()
                }

                else -> throw JsonDataException("Unsupported type ${value::class}")
            }
        }

        private fun parseNumber(raw: String): Number {
            val dec = BigDecimal(raw)

            return try {
                dec.longValueExact()
            } catch (_: Exception) {
                try {
                    dec.intValueExact()
                } catch (_: Exception) {
                    if (dec.scale() == 0) dec
                    else dec
                }
            }
        }
    }
}
