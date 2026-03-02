package com.miplan.domain.repository

import com.miplan.domain.model.Attachment

/**
 * Interface del repositorio de adjuntos
 */
interface AttachmentRepository {
    
    /**
     * Obtiene todos los adjuntos de una tarjeta
     */
    suspend fun getAttachmentsByCard(cardId: Int): Result<List<Attachment>>
    
    /**
     * Elimina un adjunto
     */
    suspend fun deleteAttachment(id: Int): Result<Unit>
}
