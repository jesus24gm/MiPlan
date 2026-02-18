package com.miplan.models.entities

import java.time.LocalDateTime

/**
 * Entidad de Tablero
 */
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
