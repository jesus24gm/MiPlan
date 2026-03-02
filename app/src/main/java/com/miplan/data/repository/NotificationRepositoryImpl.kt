package com.miplan.data.repository

import com.miplan.data.remote.ApiService
import com.miplan.domain.model.Notification
import com.miplan.domain.repository.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de notificaciones
 */
@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : NotificationRepository {
    
    override suspend fun getNotifications(): Result<List<Notification>> {
        return try {
            val response = apiService.getNotifications()
            
            if (response.success && response.data != null) {
                val notifications = response.data.map { it.toDomain() }
                Result.success(notifications)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUnreadNotifications(): Result<List<Notification>> {
        return try {
            val response = apiService.getUnreadNotifications()
            
            if (response.success && response.data != null) {
                val notifications = response.data.map { it.toDomain() }
                Result.success(notifications)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markAsRead(id: Int): Result<Unit> {
        return try {
            val response = apiService.markNotificationAsRead(id)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markAllAsRead(): Result<Unit> {
        return try {
            val response = apiService.markAllNotificationsAsRead()
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteNotification(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteNotification(id)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
