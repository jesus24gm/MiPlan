package com.miplan.domain.model

/**
 * Modelo de dominio para Checklist de Tarjeta
 */
data class Checklist(
    val id: Int,
    val cardId: Int,
    val title: String,
    val createdAt: String,
    val updatedAt: String,
    val items: List<ChecklistItem> = emptyList()
)

/**
 * Modelo de dominio para Item de Checklist
 */
data class ChecklistItem(
    val id: Int,
    val checklistId: Int,
    val title: String,
    val isCompleted: Boolean,
    val position: Int,
    val createdAt: String,
    val updatedAt: String
)
