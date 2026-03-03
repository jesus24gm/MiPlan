package com.miplan.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.miplan.domain.model.Column

/**
 * Entidad de Room para columnas de tableros Kanban
 */
@Entity(
    tableName = "columns",
    foreignKeys = [
        ForeignKey(
            entity = BoardEntity::class,
            parentColumns = ["localId"],
            childColumns = ["boardLocalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("boardLocalId")]
)
data class ColumnEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val serverId: Int? = null,
    val boardLocalId: Int, // Referencia local al tablero
    val boardServerId: Int?, // ID del tablero en el servidor
    val title: String,
    val position: Int,
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
) {
    fun toDomain(): Column {
        return Column(
            id = localId, // Usar localId para consistencia
            boardId = boardLocalId, // Usar boardLocalId para consistencia
            title = title,
            position = position,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        fun fromDomain(column: Column, boardLocalId: Int, isSynced: Boolean = true): ColumnEntity {
            return ColumnEntity(
                serverId = column.id,
                boardLocalId = boardLocalId,
                boardServerId = column.boardId,
                title = column.title,
                position = column.position,
                createdAt = column.createdAt,
                updatedAt = column.updatedAt,
                isSynced = isSynced
            )
        }
    }
}
