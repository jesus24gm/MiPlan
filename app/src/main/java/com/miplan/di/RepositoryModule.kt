package com.miplan.di

import com.miplan.data.local.TokenManager
import com.miplan.data.local.dao.BoardDao
import com.miplan.data.local.dao.CardDao
import com.miplan.data.local.dao.ColumnDao
import com.miplan.data.local.dao.TaskDao
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
        apiService: ApiService,
        taskDao: TaskDao
    ): TaskRepository {
        return TaskRepositoryOfflineImpl(apiService, taskDao)
    }
    
    @Provides
    @Singleton
    fun provideBoardRepository(
        apiService: ApiService,
        boardDao: BoardDao
    ): BoardRepository {
        return BoardRepositoryOfflineImpl(apiService, boardDao)
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
        apiService: ApiService,
        tokenManager: TokenManager
    ): UserRepository {
        return UserRepositoryImpl(apiService, tokenManager)
    }
    
    @Provides
    @Singleton
    fun provideColumnRepository(
        apiService: ApiService,
        columnDao: ColumnDao,
        boardDao: BoardDao,
        cardDao: CardDao
    ): ColumnRepository {
        return ColumnRepositoryOfflineImpl(apiService, columnDao, boardDao, cardDao)
    }
    
    @Provides
    @Singleton
    fun provideCardRepository(
        apiService: ApiService,
        cardDao: CardDao,
        columnDao: ColumnDao
    ): CardRepository {
        return CardRepositoryOfflineImpl(apiService, cardDao, columnDao)
    }
    
    @Provides
    @Singleton
    fun provideChecklistRepository(
        apiService: ApiService
    ): ChecklistRepository {
        return ChecklistRepositoryImpl(apiService)
    }
    
    @Provides
    @Singleton
    fun provideAttachmentRepository(
        apiService: ApiService
    ): AttachmentRepository {
        return AttachmentRepositoryImpl(apiService)
    }
}
