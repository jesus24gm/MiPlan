package com.miplan.domain.model

/**
 * Modelo de dominio para Tablero
 */
data class Board(
    val id: Int,
    val name: String,
    val description: String?,
    val color: String,
    val userId: Int,
    val createdAt: String,
    val updatedAt: String,
    val taskCount: Int = 0
)
