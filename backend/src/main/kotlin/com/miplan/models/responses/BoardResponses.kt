package com.miplan.models.responses

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
)

@Serializable
data class BoardDetailResponse(
    val id: Int,
    val name: String,
    val description: String? = null,
    val color: String,
    val backgroundImageUrl: String? = null,
    val userId: Int,
    val columns: List<ColumnWithCardsResponse>,
    val createdAt: String,
    val updatedAt: String
)

// ==================== COLUMN RESPONSES ====================
@Serializable
data class ColumnResponse(
    val id: Int,
    val boardId: Int,
    val title: String,
    val position: Int,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class ColumnWithCardsResponse(
    val id: Int,
    val boardId: Int,
    val title: String,
    val position: Int,
    val cards: List<CardResponse>,
    val createdAt: String,
    val updatedAt: String
)

// ==================== CARD RESPONSES ====================
@Serializable
data class CardResponse(
    val id: Int,
    val columnId: Int,
    val title: String,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val position: Int,
    val taskId: Int? = null,
    val checklists: List<ChecklistResponse> = emptyList(),
    val attachments: List<AttachmentResponse> = emptyList(),
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CardDetailResponse(
    val id: Int,
    val columnId: Int,
    val title: String,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val position: Int,
    val taskId: Int? = null,
    val checklists: List<ChecklistWithItemsResponse>,
    val attachments: List<AttachmentResponse>,
    val createdAt: String,
    val updatedAt: String
)

// ==================== CHECKLIST RESPONSES ====================
@Serializable
data class ChecklistResponse(
    val id: Int,
    val cardId: Int,
    val title: String,
    val createdAt: String
)

@Serializable
data class ChecklistWithItemsResponse(
    val id: Int,
    val cardId: Int,
    val title: String,
    val items: List<ChecklistItemResponse>,
    val progress: Int, // Porcentaje de completado (0-100)
    val createdAt: String
)

// ==================== CHECKLIST ITEM RESPONSES ====================
@Serializable
data class ChecklistItemResponse(
    val id: Int,
    val checklistId: Int,
    val title: String,
    val isCompleted: Boolean,
    val position: Int,
    val createdAt: String
)

// ==================== ATTACHMENT RESPONSES ====================
@Serializable
data class AttachmentResponse(
    val id: Int,
    val cardId: Int,
    val fileUrl: String,
    val fileName: String,
    val fileType: String,
    val createdAt: String
)
