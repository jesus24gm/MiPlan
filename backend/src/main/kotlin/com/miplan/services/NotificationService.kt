package com.miplan.services

import com.miplan.repositories.NotificationRepository

/**
 * Servicio para gestión de notificaciones
 */
class NotificationService(
    private val notificationRepository: NotificationRepository
) {
    
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
