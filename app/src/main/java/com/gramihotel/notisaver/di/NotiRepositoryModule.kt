package com.gramihotel.notisaver.di

import com.gramihotel.notisaver.data.repository.NotiRepository
import com.gramihotel.notisaver.data.repository.NotiRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotiRepositoryModule {
    @Binds
    @Singleton
    abstract fun provideNotiRepository(
        notiRepository: NotiRepositoryImpl
    ): NotiRepository
}