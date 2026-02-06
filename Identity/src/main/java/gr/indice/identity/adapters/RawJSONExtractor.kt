package gr.indice.identity.adapters

import okhttp3.ResponseBody
import java.nio.charset.Charset

sealed class RawJSONExtractorError : Exception() {
    class EncodingError : RawJSONExtractorError()
    class KeyNotFound : RawJSONExtractorError()
    class ParseError : RawJSONExtractorError()
}


@Throws(RawJSONExtractorError::class)
fun ResponseBody.extractRawJsonValue(key: String): ByteArray {
    val bytes = bytes()
    return RawJSONExtractor.extractValue(forKey = key, from = bytes)
}

object RawJSONExtractor {

    @Throws(RawJSONExtractorError::class)
    fun extractValue(forKey: String, from: ByteArray): ByteArray {
        val jsonString = try {
            from.toString(Charset.forName("UTF-8"))
        } catch (e: Exception) {
            throw RawJSONExtractorError.EncodingError()
        }

        // Matches `"key":`
        val regex = Regex("\"${Regex.escape(forKey)}\"\\s*:")
        val match = regex.find(jsonString) ?: throw RawJSONExtractorError.KeyNotFound()

        var index = match.range.last + 1

        // skip whitespace
        while (index < jsonString.length && jsonString[index].isWhitespace()) {
            index++
        }

        if (index >= jsonString.length) {
            throw RawJSONExtractorError.ParseError()
        }

        val startIndex = index
        val firstChar = jsonString[index]

        var endIndex = index

        when (firstChar) {
            '{', '[' -> {
                var depth = 1
                index++

                val closing = if (firstChar == '{') '}' else ']'

                while (index < jsonString.length && depth > 0) {
                    val ch = jsonString[index]
                    if (ch == firstChar) {
                        depth++
                    } else if (ch == closing) {
                        depth--
                    }
                    index++
                }

                if (depth != 0) {
                    throw RawJSONExtractorError.ParseError()
                }

                endIndex = index
            }

            '"' -> {
                index++
                while (index < jsonString.length) {
                    val ch = jsonString[index]
                    val prev = jsonString[index - 1]
                    if (ch == '"' && prev != '\\') {
                        index++
                        break
                    }
                    index++
                }
                endIndex = index
            }

            else -> {
                // number, true, false, null
                while (index < jsonString.length &&
                    jsonString[index] != ',' &&
                    jsonString[index] != '}'
                ) {
                    index++
                }
                endIndex = index
            }
        }

        val rawString = jsonString.substring(startIndex, endIndex)

        return try {
            rawString.toByteArray(Charset.forName("UTF-8"))
        } catch (e: Exception) {
            throw RawJSONExtractorError.EncodingError()
        }
    }
}
