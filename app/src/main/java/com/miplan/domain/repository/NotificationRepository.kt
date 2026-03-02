package com.miplan.domain.repository

import com.miplan.domain.model.Notification

/**
 * Interface del repositorio de notificaciones
 */
interface NotificationRepository {
    
    /**
     * Obtiene todas las notificaciones del usuario actual
     */
    suspend fun getNotifications(): Result<List<Notification>>
    
    /**
     * Obtiene notificaciones no leídas
     */
    suspend fun getUnreadNotifications(): Result<List<Notification>>
    
    /**
     * Marca una notificación como leída
     */
    suspend fun markAsRead(id: Int): Result<Unit>
    
    /**
     * Marca todas las notificaciones como leídas
     */
    suspend fun markAllAsRead(): Result<Unit>
    
    /**
     * Elimina una notificación
     */
    suspend fun deleteNotification(id: Int): Result<Unit>
}
