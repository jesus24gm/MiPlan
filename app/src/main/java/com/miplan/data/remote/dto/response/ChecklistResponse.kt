package com.miplan.data.remote.dto.response

import com.miplan.domain.model.Checklist
import com.miplan.domain.model.ChecklistItem
import kotlinx.serialization.Serializable

@Serializable
data class ChecklistResponse(
    val id: Int,
    val cardId: Int,
    val title: String,
    val createdAt: String,
    val updatedAt: String? = null,
    val items: List<ChecklistItemResponse>? = null
) {
    fun toDomain(): Checklist {
        return Checklist(
            id = id,
            cardId = cardId,
            title = title,
            createdAt = createdAt,
            updatedAt = updatedAt ?: createdAt,
            items = items?.map { it.toDomain() } ?: emptyList()
        )
    }
}

@Serializable
data class ChecklistItemResponse(
    val id: Int,
    val checklistId: Int,
    val title: String,
    val isCompleted: Boolean,
    val position: Int,
    val createdAt: String,
    val updatedAt: String? = null
) {
    fun toDomain(): ChecklistItem {
        return ChecklistItem(
            id = id,
            checklistId = checklistId,
            title = title,
            isCompleted = isCompleted,
            position = position,
            createdAt = createdAt,
            updatedAt = updatedAt ?: createdAt
        )
    }
}
