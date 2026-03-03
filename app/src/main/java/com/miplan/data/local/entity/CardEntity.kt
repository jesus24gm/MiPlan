package com.miplan.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.miplan.domain.model.Card

/**
 * Entidad de Room para tarjetas de columnas Kanban
 */
@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = ColumnEntity::class,
            parentColumns = ["localId"],
            childColumns = ["columnLocalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("columnLocalId")]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val serverId: Int? = null,
    val columnLocalId: Int,
    val columnServerId: Int?,
    val title: String,
    val description: String?,
    val dueDate: String?,
    val priority: String?,
    val labels: String?,
    val position: Int,
    val taskId: Int?,
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
) {
    fun toDomain(): Card {
        return Card(
            id = localId, // Usar localId para consistencia
            columnId = columnLocalId, // Usar columnLocalId para consistencia
            title = title,
            description = description,
            position = position,
            taskId = taskId,
            dueDate = dueDate,
            priority = priority,
            labels = labels,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        fun fromDomain(card: Card, columnLocalId: Int, isSynced: Boolean = true): CardEntity {
            return CardEntity(
                serverId = card.id,
                columnLocalId = columnLocalId,
                columnServerId = card.columnId,
                title = card.title,
                description = card.description,
                dueDate = card.dueDate,
                priority = card.priority,
                labels = card.labels,
                position = card.position,
                taskId = card.taskId,
                createdAt = card.createdAt,
                updatedAt = card.updatedAt,
                isSynced = isSynced
            )
        }
    }
}
