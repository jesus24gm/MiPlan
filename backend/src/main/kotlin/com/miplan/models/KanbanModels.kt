package com.miplan.models

import java.time.LocalDateTime

/**
 * Modelos de dominio para el sistema Kanban
 */

data class Column(
    val id: Int,
    val boardId: Int,
    val title: String,
    val position: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class Card(
    val id: Int,
    val columnId: Int,
    val title: String,
    val description: String?,
    val coverImageUrl: String?,
    val position: Int,
    val taskId: Int?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class CardChecklist(
    val id: Int,
    val cardId: Int,
    val title: String,
    val createdAt: LocalDateTime
)

data class ChecklistItem(
    val id: Int,
    val checklistId: Int,
    val title: String,
    val isCompleted: Boolean,
    val position: Int,
    val createdAt: LocalDateTime
)

data class CardAttachment(
    val id: Int,
    val cardId: Int,
    val fileUrl: String,
    val fileName: String,
    val fileType: String,
    val createdAt: LocalDateTime
)

// Modelo extendido de Board con backgroundImageUrl
data class Board(
    val id: Int,
    val name: String,
    val description: String?,
    val color: String,
    val backgroundImageUrl: String?,
    val userId: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
