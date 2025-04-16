package com.gramihotel.notisaver.data.mapper

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

class JackSonMapper {
    fun generatedMapper(): JsonMapper = JsonMapper.builder()
        .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .addModule(
            KotlinModule.Builder()
                .configure(KotlinFeature.NullIsSameAsDefault, enabled = true)
                .configure(KotlinFeature.StrictNullChecks, enabled = true)
                .build()
        )
        .disable(MapperFeature.AUTO_DETECT_IS_GETTERS) // 변수명에 is가 있는 경우 Boolean으로 자동으로 변환되는 기능 끄기
        .build()
}