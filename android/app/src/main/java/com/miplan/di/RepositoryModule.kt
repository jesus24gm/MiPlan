package com.miplan.di

import com.miplan.data.local.TokenManager
import com.miplan.data.remote.ApiService
import com.miplan.data.repository.*
import com.miplan.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de repositorios
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, tokenManager)
    }
    
    @Provides
    @Singleton
    fun provideTaskRepository(
        apiService: ApiService
    ): TaskRepository {
        return TaskRepositoryImpl(apiService)
    }
    
    @Provides
    @Singleton
    fun provideBoardRepository(
        apiService: ApiService
    ): BoardRepository {
        return BoardRepositoryImpl(apiService)
    }
    
    @Provides
    @Singleton
    fun provideNotificationRepository(
        apiService: ApiService
    ): NotificationRepository {
        return NotificationRepositoryImpl(apiService)
    }
    
    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: ApiService
    ): UserRepository {
        return UserRepositoryImpl(apiService)
    }
}
