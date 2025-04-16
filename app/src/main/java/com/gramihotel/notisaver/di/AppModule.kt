package com.gramihotel.notisaver.di

import com.gramihotel.notisaver.data.mapper.JackSonMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideJackSonMapper(): JackSonMapper = JackSonMapper()
}