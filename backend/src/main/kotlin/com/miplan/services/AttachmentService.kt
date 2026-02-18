package com.miplan.services

import com.miplan.models.CardAttachment
import com.miplan.models.requests.CreateAttachmentRequest
import com.miplan.models.responses.AttachmentResponse
import com.miplan.repositories.AttachmentRepository
import java.time.format.DateTimeFormatter

class AttachmentService(
    private val attachmentRepository: AttachmentRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    suspend fun createAttachment(request: CreateAttachmentRequest): AttachmentResponse? {
        val attachment = attachmentRepository.create(
            cardId = request.cardId,
            fileUrl = request.fileUrl,
            fileName = request.fileName,
            fileType = request.fileType
        ) ?: return null
        
        return attachmentToResponse(attachment)
    }
    
    suspend fun getAttachmentById(id: Int): AttachmentResponse? {
        val attachment = attachmentRepository.findById(id) ?: return null
        return attachmentToResponse(attachment)
    }
    
    suspend fun getAttachmentsByCardId(cardId: Int): List<AttachmentResponse> {
        val attachments = attachmentRepository.findByCardId(cardId)
        return attachments.map { attachmentToResponse(it) }
    }
    
    suspend fun deleteAttachment(id: Int): Boolean {
        return attachmentRepository.delete(id)
    }
    
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
