package com.miplan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.miplan.domain.model.Board

/**
 * Entidad de Room para tableros Kanban
 */
@Entity(tableName = "boards")
data class BoardEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val serverId: Int? = null,
    val name: String,
    val description: String?,
    val color: String,
    val backgroundImageUrl: String?,
    val userId: Int?,
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
) {
    fun toDomain(): Board {
        return Board(
            id = localId, // Usar localId para consistencia (igual que TaskEntity)
            name = name,
            description = description,
            color = color,
            backgroundImageUrl = backgroundImageUrl,
            userId = userId ?: 0,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        fun fromDomain(board: Board, isSynced: Boolean = true): BoardEntity {
            return BoardEntity(
                serverId = board.id,
                name = board.name,
                description = board.description,
                color = board.color,
                backgroundImageUrl = board.backgroundImageUrl,
                userId = board.userId,
                createdAt = board.createdAt,
                updatedAt = board.updatedAt,
                isSynced = isSynced
            )
        }
    }
}
