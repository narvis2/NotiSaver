package com.gramihotel.notisaver.di

import com.gramihotel.notisaver.data.mapper.DateTimeFormatModule
import com.gramihotel.notisaver.data.mapper.JackSonMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDateTimeFormat(): DateTimeFormatModule = DateTimeFormatModule()

    @Singleton
    @Provides
    fun provideJackSonMapper(
        dateTimeFormatModule: DateTimeFormatModule
    ): JackSonMapper = JackSonMapper(dateTimeFormatModule)
}