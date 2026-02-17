package com.miplan.models.responses

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
)
