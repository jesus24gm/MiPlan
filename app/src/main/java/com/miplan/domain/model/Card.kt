package com.miplan.domain.model

/**
 * Modelo de dominio para Tarjeta de Kanban
 */
data class Card(
    val id: Int,
    val columnId: Int,
    val title: String,
    val description: String?,
    val position: Int,
    val taskId: Int? = null,
    val dueDate: String?,
    val priority: String?,
    val labels: String?,
    val createdAt: String,
    val updatedAt: String,
    val checklists: List<Checklist> = emptyList(),
    val attachments: List<Attachment> = emptyList()
)
