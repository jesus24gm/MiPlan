package com.miplan.data.remote.dto.response

import com.miplan.domain.model.Card
import kotlinx.serialization.Serializable

@Serializable
data class CardResponse(
    val id: Int,
    val columnId: Int,
    val title: String,
    val description: String? = null,
    val position: Int,
    val taskId: Int? = null,
    val dueDate: String? = null,
    val priority: String? = null,
    val labels: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val checklists: List<ChecklistResponse>? = null,
    val attachments: List<AttachmentResponse>? = null
) {
    fun toDomain(): Card {
        return Card(
            id = id,
            columnId = columnId,
            title = title,
            description = description,
            position = position,
            taskId = taskId,
            dueDate = dueDate,
            priority = priority,
            labels = labels,
            createdAt = createdAt,
            updatedAt = updatedAt,
            checklists = checklists?.map { it.toDomain() } ?: emptyList(),
            attachments = attachments?.map { it.toDomain() } ?: emptyList()
        )
    }
}
