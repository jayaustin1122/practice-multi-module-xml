package com.projects.core.di

import com.projects.core.data.repository.AuthRepositoryImpl
import com.projects.core.data.source.FirebaseAuthDataSource
import com.projects.core.data.source.FirestoreUserDataSource
import com.projects.core.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        authDataSource: FirebaseAuthDataSource,
        userDataSource: FirestoreUserDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(authDataSource, userDataSource)
    }
}