package com.miplan.data.remote.dto.response

import com.miplan.domain.model.Column
import kotlinx.serialization.Serializable

@Serializable
data class ColumnResponse(
    val id: Int,
    val boardId: Int,
    val title: String,
    val position: Int,
    val createdAt: String,
    val updatedAt: String,
    val cards: List<CardResponse>? = null
) {
    fun toDomain(): Column {
        return Column(
            id = id,
            boardId = boardId,
            title = title,
            position = position,
            createdAt = createdAt,
            updatedAt = updatedAt,
            cards = cards?.map { it.toDomain() } ?: emptyList()
        )
    }
}
