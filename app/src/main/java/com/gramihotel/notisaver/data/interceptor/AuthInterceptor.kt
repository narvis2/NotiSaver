package com.gramihotel.notisaver.data.interceptor

import com.gramihotel.notisaver.BuildConfig
import com.gramihotel.notisaver.data.utils.BASE_URL
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AuthInterceptor : Interceptor {
    private val client = OkHttpClient.Builder().apply {
        connectTimeout(30L, TimeUnit.SECONDS)
        writeTimeout(60L, TimeUnit.SECONDS)
        readTimeout(60L, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
    }.build()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val token = requestLogin()
        token?.let {
            val resultRequest = request.newBuilder().addHeader(
                "Authorization", "Bearer $it"
            ).build()


            return chain.proceed(resultRequest)
        }

        return chain.proceed(request)
    }


    private fun requestLogin(): String? {
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()

        val jsonBody = """
            {
                "email": "owner@naver.com",
                "password": "1q2w3e4r!"
            }
        """.trimIndent()

        val requestBody = jsonBody.toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url("${BASE_URL}/sign/sign-in")
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val jsonObject = JSONObject(responseBody)
                val accessToken = jsonObject.getJSONObject("data").getString("accessToken")

                accessToken
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}