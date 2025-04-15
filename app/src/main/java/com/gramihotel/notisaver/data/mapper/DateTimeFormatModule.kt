package com.gramihotel.notisaver.data.mapper

import android.text.TextUtils
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import java.io.IOException


class DateTimeFormatModule : SimpleModule() {
    companion object {
        private val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(DateTimeZone.forID("Asia/Seoul"))
        private val localTimeFormatter: DateTimeFormatter = ISODateTimeFormat.dateTimeParser().withOffsetParsed()
    }

    init {
        addSerializer(DateTime::class.java, object : JsonSerializer<DateTime?>() {
            @Throws(IOException::class, JsonProcessingException::class)
            override fun serialize(
                value: DateTime?,
                gen: JsonGenerator,
                serializers: SerializerProvider?,
            ) {
                if (value == null) {
                    gen.writeNull()
                } else {
                    gen.writeString(dateTimeFormatter.print(value))
                }
            }
        })

        addDeserializer(DateTime::class.java, object : JsonDeserializer<DateTime?>() {
            @Throws(IOException::class, JsonProcessingException::class)
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): DateTime? {
                val t: JsonToken = p.currentToken
                if (t === JsonToken.VALUE_NUMBER_INT) {
                    return DateTime(p.longValue, dateTimeFormatter.zone)
                }

                if (t === JsonToken.VALUE_STRING) {
                    val str: String = p.text

                    return if (TextUtils.isEmpty(str) || str == "0000-00-00 00:00:00") {
                        null
                    } else {
                        val formattedDate = if (str.length == 10) "$str 00:00:00" else str
                        dateTimeFormatter.parseDateTime(formattedDate)
                    }
                }

                return null
            }
        })

        addSerializer(LocalDate::class.java, object : JsonSerializer<LocalDate?>() {
            @Throws(IOException::class, JsonProcessingException::class)
            override fun serialize(
                value: LocalDate?,
                gen: JsonGenerator,
                serializers: SerializerProvider?,
            ) {
                if (value == null) {
                    gen.writeNull()
                } else {
                    gen.writeString(localTimeFormatter.print(value))
                }
            }
        })

        addDeserializer(LocalDate::class.java, object : JsonDeserializer<LocalDate?>() {
            @Throws(IOException::class, JsonProcessingException::class)
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDate? {
                val t: JsonToken = p.currentToken
                if (t === JsonToken.VALUE_NUMBER_INT) {
                    return LocalDate(p.longValue, dateTimeFormatter.zone)
                }

                if (t === JsonToken.VALUE_STRING) {
                    val str: String = p.text
                    return if (TextUtils.isEmpty(str)) {
                        null
                    } else localTimeFormatter.parseLocalDate(str)
                }

                return null
            }
        })
    }
}