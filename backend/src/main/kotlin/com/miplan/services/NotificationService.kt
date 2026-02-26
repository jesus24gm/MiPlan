package com.miplan.services

import com.miplan.database.Notifications
import com.miplan.repositories.NotificationRepository
import org.jetbrains.exposed.sql.transactions.transaction

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
        title: String,
        message: String,
        type: String = "INFO"
    ) {
        notificationRepository.createNotification(
            userId = userId,
            title = title,
            message = message,
            type = type
        )
    }
    
    /**
     * Crea notificación de tarea compartida
     */
    suspend fun notifyTaskShared(
        userId: Int,
        taskTitle: String,
        sharedByName: String
    ) {
        createNotification(
            userId = userId,
            title = "Tarea compartida",
            message = "$sharedByName te ha compartido la tarea: $taskTitle",
            type = "TASK_SHARED"
        )
    }
}
