package com.miplan.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class TaskResponse(
    val id: Int,
    val title: String,
    val description: String? = null,
    val status: String,
    val priority: String,
    val dueDate: String? = null,
    val imageUrl: String? = null,
    val boardId: Int? = null,
    val boardName: String? = null,
    val createdBy: Int,
    val createdAt: String,
    val updatedAt: String,
    // Campos de recurrencia
    val recurrenceType: String = "NONE",
    val recurrenceInterval: Int = 1,
    val recurrenceDays: String? = null,
    val recurrenceEndDate: String? = null,
    val isRecurringInstance: Boolean = false,
    val parentTaskId: Int? = null
)
