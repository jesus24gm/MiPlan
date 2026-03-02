package com.miplan.data.remote.dto.response

import com.miplan.domain.model.Notification
import com.miplan.domain.model.NotificationType
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    val id: Int,
    val userId: Int,
    val taskId: Int? = null,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String
) {
    fun toDomain(): Notification {
        return Notification(
            id = id,
            userId = userId,
            taskId = taskId,
            message = message,
            type = NotificationType.fromString(type),
            isRead = isRead,
            createdAt = createdAt
        )
    }
}
