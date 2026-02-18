package com.miplan.services

import com.miplan.models.CardAttachment
import com.miplan.models.requests.CreateAttachmentRequest
import com.miplan.models.responses.AttachmentResponse
import com.miplan.repositories.AttachmentRepository
import java.time.format.DateTimeFormatter

/**
 * Servicio para lógica de negocio de attachments
 */
class AttachmentService(
    private val attachmentRepository: AttachmentRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    /**
     * Crear un nuevo attachment
     */
    fun createAttachment(request: CreateAttachmentRequest): AttachmentResponse {
        val attachment = attachmentRepository.create(
            cardId = request.cardId,
            fileUrl = request.fileUrl,
            fileName = request.fileName,
            fileType = request.fileType
        )
        
        return attachmentToResponse(attachment)
    }
    
    /**
     * Obtener attachment por ID
     */
    fun getAttachmentById(id: Int): AttachmentResponse? {
        val attachment = attachmentRepository.findById(id) ?: return null
        return attachmentToResponse(attachment)
    }
    
    /**
     * Obtener todos los attachments de una tarjeta
     */
    fun getAttachmentsByCardId(cardId: Int): List<AttachmentResponse> {
        val attachments = attachmentRepository.findByCardId(cardId)
        return attachments.map { attachmentToResponse(it) }
    }
    
    /**
     * Eliminar attachment
     */
    fun deleteAttachment(id: Int): Boolean {
        return attachmentRepository.delete(id)
    }
    
    /**
     * Mapear CardAttachment a AttachmentResponse
     */
    private fun attachmentToResponse(attachment: CardAttachment): AttachmentResponse {
        return AttachmentResponse(
            id = attachment.id,
            cardId = attachment.cardId,
            fileUrl = attachment.fileUrl,
            fileName = attachment.fileName,
            fileType = attachment.fileType,
            createdAt = attachment.createdAt.format(dateFormatter)
        )
    }
}
