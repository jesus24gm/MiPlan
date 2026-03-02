package com.miplan.services

import com.miplan.models.responses.NotificationResponse
import com.miplan.repositories.NotificationRepository
import java.time.format.DateTimeFormatter

/**
 * Servicio para gestión de notificaciones
 */
class NotificationService(
    private val notificationRepository: NotificationRepository
) {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    /**
     * Crea una notificación para un usuario
     */
    suspend fun createNotification(
        userId: Int,
        taskId: Int?,
        message: String,
        type: String = "INFO"
    ) {
        notificationRepository.create(
            userId = userId,
            taskId = taskId,
            message = message,
            type = type
        )
    }
    
    /**
     * Obtiene todas las notificaciones de un usuario
     */
    suspend fun getUserNotifications(userId: Int): List<NotificationResponse> {
        val notifications = notificationRepository.findByUserId(userId)
        return notifications.map { notification ->
            NotificationResponse(
                id = notification.id,
                userId = notification.userId,
                taskId = notification.taskId,
                message = notification.message,
                type = notification.type,
                isRead = notification.isRead,
                createdAt = notification.createdAt.format(dateFormatter)
            )
        }
    }
    
    /**
     * Obtiene las notificaciones no leídas de un usuario
     */
    suspend fun getUnreadNotifications(userId: Int): List<NotificationResponse> {
        val notifications = notificationRepository.findUnreadByUserId(userId)
        return notifications.map { notification ->
            NotificationResponse(
                id = notification.id,
                userId = notification.userId,
                taskId = notification.taskId,
                message = notification.message,
                type = notification.type,
                isRead = notification.isRead,
                createdAt = notification.createdAt.format(dateFormatter)
            )
        }
    }
    
    /**
     * Marca una notificación como leída
     */
    suspend fun markAsRead(notificationId: Int, userId: Int) {
        // Verificar que la notificación pertenece al usuario
        if (!notificationRepository.isNotificationOwnedByUser(notificationId, userId)) {
            throw IllegalArgumentException("No tienes permiso para modificar esta notificación")
        }
        
        val success = notificationRepository.markAsRead(notificationId)
        if (!success) {
            throw IllegalArgumentException("No se pudo marcar la notificación como leída")
        }
    }
    
    /**
     * Marca todas las notificaciones de un usuario como leídas
     */
    suspend fun markAllAsRead(userId: Int) {
        notificationRepository.markAllAsRead(userId)
    }
    
    /**
     * Elimina una notificación
     */
    suspend fun deleteNotification(notificationId: Int, userId: Int) {
        // Verificar que la notificación pertenece al usuario
        if (!notificationRepository.isNotificationOwnedByUser(notificationId, userId)) {
            throw IllegalArgumentException("No tienes permiso para eliminar esta notificación")
        }
        
        val success = notificationRepository.delete(notificationId)
        if (!success) {
            throw IllegalArgumentException("No se pudo eliminar la notificación")
        }
    }
    
    /**
     * Crea notificación de tarea compartida
     */
    suspend fun notifyTaskShared(
        userId: Int,
        taskId: Int,
        taskTitle: String,
        sharedByName: String
    ) {
        createNotification(
            userId = userId,
            taskId = taskId,
            message = "$sharedByName te ha compartido la tarea: $taskTitle",
            type = "TASK_SHARED"
        )
    }
}
