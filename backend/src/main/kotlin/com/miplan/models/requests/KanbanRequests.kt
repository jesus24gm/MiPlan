package com.miplan.models.requests

import kotlinx.serialization.Serializable

/**
 * DTOs de Request para el sistema Kanban
 */

// ==================== BOARD REQUESTS ====================
@Serializable
data class CreateBoardRequest(
    val name: String,
    val description: String? = null,
    val color: String = "#E3F2FD",
    val backgroundImageUrl: String? = null
)

@Serializable
data class UpdateBoardRequest(
    val name: String? = null,
    val description: String? = null,
    val color: String? = null,
    val backgroundImageUrl: String? = null
)

// ==================== COLUMN REQUESTS ====================
@Serializable
data class CreateColumnRequest(
    val boardId: Int,
    val title: String,
    val position: Int? = null
)

@Serializable
data class UpdateColumnRequest(
    val title: String? = null,
    val position: Int? = null
)

@Serializable
data class MoveColumnRequest(
    val newPosition: Int
)

// ==================== CARD REQUESTS ====================
@Serializable
data class CreateCardRequest(
    val columnId: Int,
    val title: String,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val position: Int? = null,
    val taskId: Int? = null
)

@Serializable
data class UpdateCardRequest(
    val title: String? = null,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val position: Int? = null,
    val taskId: Int? = null
)

@Serializable
data class MoveCardRequest(
    val newColumnId: Int,
    val newPosition: Int
)

@Serializable
data class CopyCardRequest(
    val targetColumnId: Int,
    val targetBoardId: Int? = null // Si es null, se copia en el mismo tablero
)

// ==================== CHECKLIST REQUESTS ====================
@Serializable
data class CreateChecklistRequest(
    val cardId: Int,
    val title: String
)

@Serializable
data class UpdateChecklistRequest(
    val title: String
)

// ==================== CHECKLIST ITEM REQUESTS ====================
@Serializable
data class CreateChecklistItemRequest(
    val checklistId: Int,
    val title: String,
    val position: Int? = null
)

@Serializable
data class UpdateChecklistItemRequest(
    val title: String? = null,
    val isCompleted: Boolean? = null,
    val position: Int? = null
)

// ==================== ATTACHMENT REQUESTS ====================
@Serializable
data class CreateAttachmentRequest(
    val cardId: Int,
    val fileUrl: String,
    val fileName: String,
    val fileType: String
)
