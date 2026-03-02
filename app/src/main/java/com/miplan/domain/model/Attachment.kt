package com.miplan.domain.model

/**
 * Modelo de dominio para Adjunto de Tarjeta
 */
data class Attachment(
    val id: Int,
    val cardId: Int,
    val fileName: String,
    val fileUrl: String,
    val fileType: String,
    val fileSize: Long,
    val createdAt: String,
    val updatedAt: String
)
