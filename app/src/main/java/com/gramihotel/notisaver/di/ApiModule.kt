package com.gramihotel.notisaver.di

import com.gramihotel.notisaver.BuildConfig
import com.gramihotel.notisaver.data.auth.NotiAuthApiService
import com.gramihotel.notisaver.data.interceptor.AuthInterceptor
import com.gramihotel.notisaver.data.mapper.EnumConverterFactory
import com.gramihotel.notisaver.data.mapper.JackSonMapper
import com.gramihotel.notisaver.data.utils.BASE_URL
import com.gramihotel.notisaver.data.utils.CONNECT_TIMEOUT
import com.gramihotel.notisaver.data.utils.READ_TIMEOUT
import com.gramihotel.notisaver.data.utils.WRITE_TIMEOUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideNotiAuthApiService(
        retrofit: Retrofit
    ): NotiAuthApiService = retrofit.create(NotiAuthApiService::class.java)

    @Singleton
    @Provides
    fun provideAuthRetrofit(
        client: OkHttpClient,
        jackson: JacksonConverterFactory,
        enum: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(jackson)
        .addConverterFactory(enum)
        .build()


    @Singleton
    @Provides
    fun provideJacksonConverterFactory(
        jacksonMapper: JackSonMapper
    ): JacksonConverterFactory =
        JacksonConverterFactory.create(jacksonMapper.generatedMapper())

    @Singleton
    @Provides
    fun provideEnumConverterFactory(): Converter.Factory = EnumConverterFactory()

    @Singleton
    @Provides
    fun provideAuthOkHttpClient(
        logging: HttpLoggingInterceptor,
        interceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder().apply {
        connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            addInterceptor(logging)
        }

        addInterceptor(interceptor)
    }.build()

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    @Singleton
    @Provides
    fun authInterceptor(): Interceptor = AuthInterceptor()
}