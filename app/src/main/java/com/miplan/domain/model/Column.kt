package com.miplan.domain.model

/**
 * Modelo de dominio para Columna de Tablero Kanban
 */
data class Column(
    val id: Int,
    val boardId: Int,
    val title: String,
    val position: Int,
    val createdAt: String,
    val updatedAt: String,
    val cards: List<Card> = emptyList()
)
