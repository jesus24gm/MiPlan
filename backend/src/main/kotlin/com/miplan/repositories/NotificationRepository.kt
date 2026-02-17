package com.miplan.repositories

import com.miplan.database.DatabaseFactory.dbQuery
import com.miplan.database.Notifications
import com.miplan.models.entities.Notification
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

/**
 * Repositorio para operaciones de notificaciones en la base de datos
 */
class NotificationRepository {
    
    /**
     * Crea una nueva notificación
     */
    suspend fun create(
        userId: Int,
        taskId: Int?,
        message: String,
        type: String
    ): Notification? = dbQuery {
        val insertStatement = Notifications.insert {
            it[Notifications.userId] = userId
            it[Notifications.taskId] = taskId
            it[Notifications.message] = message
            it[Notifications.type] = type
            it[Notifications.isRead] = false
            it[Notifications.createdAt] = LocalDateTime.now()
        }
        
        insertStatement.resultedValues?.singleOrNull()?.let(::rowToNotification)
    }
    
    /**
     * Obtiene todas las notificaciones de un usuario
     */
    suspend fun findByUserId(userId: Int): List<Notification> = dbQuery {
        Notifications.select { Notifications.userId eq userId }
            .orderBy(Notifications.createdAt to SortOrder.DESC)
            .map(::rowToNotification)
    }
    
    /**
     * Obtiene notificaciones no leídas de un usuario
     */
    suspend fun findUnreadByUserId(userId: Int): List<Notification> = dbQuery {
        Notifications.select {
            (Notifications.userId eq userId) and (Notifications.isRead eq false)
        }
            .orderBy(Notifications.createdAt to SortOrder.DESC)
            .map(::rowToNotification)
    }
    
    /**
     * Marca una notificación como leída
     */
    suspend fun markAsRead(id: Int): Boolean = dbQuery {
        Notifications.update({ Notifications.id eq id }) {
            it[isRead] = true
        } > 0
    }
    
    /**
     * Marca todas las notificaciones de un usuario como leídas
     */
    suspend fun markAllAsRead(userId: Int): Boolean = dbQuery {
        Notifications.update({ Notifications.userId eq userId }) {
            it[isRead] = true
        } > 0
    }
    
    /**
     * Elimina una notificación
     */
    suspend fun delete(id: Int): Boolean = dbQuery {
        Notifications.deleteWhere { Notifications.id eq id } > 0
    }
    
    /**
     * Verifica si una notificación pertenece a un usuario
     */
    suspend fun isNotificationOwnedByUser(notificationId: Int, userId: Int): Boolean = dbQuery {
        Notifications.select {
            (Notifications.id eq notificationId) and (Notifications.userId eq userId)
        }.count() > 0
    }
    
    /**
     * Convierte una fila de la base de datos a un objeto Notification
     */
    private fun rowToNotification(row: ResultRow): Notification {
        return Notification(
            id = row[Notifications.id],
            userId = row[Notifications.userId],
            taskId = row[Notifications.taskId],
            message = row[Notifications.message],
            type = row[Notifications.type],
            isRead = row[Notifications.isRead],
            createdAt = row[Notifications.createdAt]
        )
    }
}
