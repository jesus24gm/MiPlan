package com.miplan.data.remote.dto.response

import com.miplan.domain.model.Board
import kotlinx.serialization.Serializable

@Serializable
data class BoardResponse(
    val id: Int,
    val name: String,
    val description: String? = null,
    val color: String,
    val backgroundImageUrl: String? = null,
    val userId: Int,
    val createdAt: String,
    val updatedAt: String,
    val taskCount: Int = 0
) {
    fun toDomain(): Board {
        return Board(
            id = id,
            name = name,
            description = description,
            color = color,
            backgroundImageUrl = backgroundImageUrl,
            userId = userId,
            createdAt = createdAt,
            updatedAt = updatedAt,
            taskCount = taskCount
        )
    }
}
