package com.miplan.data.remote.dto.response

import com.miplan.domain.model.Task
import com.miplan.domain.model.TaskPriority
import com.miplan.domain.model.TaskStatus
import kotlinx.serialization.Serializable

@Serializable
data class TaskResponse(
    val id: Int,
    val title: String,
    val description: String? = null,
    val status: String,
    val priority: String,
    val dueDate: String? = null,
    val boardId: Int? = null,
    val boardName: String? = null,
    val createdBy: Int,
    val createdAt: String,
    val updatedAt: String
) {
    fun toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            status = TaskStatus.fromString(status),
            priority = TaskPriority.fromString(priority),
            dueDate = dueDate,
            boardId = boardId,
            boardName = boardName,
            createdBy = createdBy,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
