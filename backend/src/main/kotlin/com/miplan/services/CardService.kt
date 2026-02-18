package com.miplan.services

import com.miplan.models.Card
import com.miplan.models.requests.*
import com.miplan.models.responses.CardDetailResponse
import com.miplan.models.responses.CardResponse
import com.miplan.repositories.CardRepository
import com.miplan.repositories.ChecklistRepository
import com.miplan.repositories.AttachmentRepository
import java.time.format.DateTimeFormatter

/**
 * Servicio para lógica de negocio de tarjetas
 */
class CardService(
    private val cardRepository: CardRepository,
    private val checklistRepository: ChecklistRepository,
    private val attachmentRepository: AttachmentRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    /**
     * Crear una nueva tarjeta
     */
    fun createCard(request: CreateCardRequest): CardResponse {
        val card = cardRepository.create(
            columnId = request.columnId,
            title = request.title,
            description = request.description,
            coverImageUrl = request.coverImageUrl,
            position = request.position,
            taskId = request.taskId
        )
        
        return cardToResponse(card)
    }
    
    /**
     * Obtener tarjeta por ID (simple)
     */
    fun getCardById(id: Int): CardResponse? {
        val card = cardRepository.findById(id) ?: return null
        return cardToResponse(card)
    }
    
    /**
     * Obtener tarjeta con detalles completos (checklists y attachments)
     */
    fun getCardDetail(id: Int): CardDetailResponse? {
        val card = cardRepository.findById(id) ?: return null
        
        // Obtener checklists con items
        val checklists = checklistRepository.findChecklistsByCardId(id).map { checklist ->
            val items = checklistRepository.findItemsByChecklistId(checklist.id)
            val progress = checklistRepository.calculateProgress(checklist.id)
            
            com.miplan.models.responses.ChecklistWithItemsResponse(
                id = checklist.id,
                cardId = checklist.cardId,
                title = checklist.title,
                items = items.map { item ->
                    com.miplan.models.responses.ChecklistItemResponse(
                        id = item.id,
                        checklistId = item.checklistId,
                        title = item.title,
                        isCompleted = item.isCompleted,
                        position = item.position,
                        createdAt = item.createdAt.format(dateFormatter)
                    )
                },
                progress = progress,
                createdAt = checklist.createdAt.format(dateFormatter)
            )
        }
        
        // Obtener attachments
        val attachments = attachmentRepository.findByCardId(id).map { attachment ->
            com.miplan.models.responses.AttachmentResponse(
                id = attachment.id,
                cardId = attachment.cardId,
                fileUrl = attachment.fileUrl,
                fileName = attachment.fileName,
                fileType = attachment.fileType,
                createdAt = attachment.createdAt.format(dateFormatter)
            )
        }
        
        return CardDetailResponse(
            id = card.id,
            columnId = card.columnId,
            title = card.title,
            description = card.description,
            coverImageUrl = card.coverImageUrl,
            position = card.position,
            taskId = card.taskId,
            checklists = checklists,
            attachments = attachments,
            createdAt = card.createdAt.format(dateFormatter),
            updatedAt = card.updatedAt.format(dateFormatter)
        )
    }
    
    /**
     * Obtener todas las tarjetas de una columna
     */
    fun getCardsByColumnId(columnId: Int): List<CardResponse> {
        val cards = cardRepository.findByColumnId(columnId)
        return cards.map { cardToResponse(it) }
    }
    
    /**
     * Actualizar tarjeta
     */
    fun updateCard(id: Int, request: UpdateCardRequest): CardResponse? {
        val card = cardRepository.update(
            id = id,
            title = request.title,
            description = request.description,
            coverImageUrl = request.coverImageUrl,
            position = request.position,
            taskId = request.taskId
        ) ?: return null
        
        return cardToResponse(card)
    }
    
    /**
     * Eliminar tarjeta
     */
    fun deleteCard(id: Int): Boolean {
        return cardRepository.delete(id)
    }
    
    /**
     * Mover tarjeta a otra columna y/o posición
     */
    fun moveCard(id: Int, request: MoveCardRequest): CardResponse? {
        val card = cardRepository.moveCard(
            id = id,
            newColumnId = request.newColumnId,
            newPosition = request.newPosition
        ) ?: return null
        
        return cardToResponse(card)
    }
    
    /**
     * Copiar tarjeta
     */
    fun copyCard(id: Int, request: CopyCardRequest): CardResponse? {
        val card = cardRepository.copyCard(
            id = id,
            targetColumnId = request.targetColumnId
        ) ?: return null
        
        return cardToResponse(card)
    }
    
    /**
     * Mapear Card a CardResponse
     */
    private fun cardToResponse(card: Card): CardResponse {
        return CardResponse(
            id = card.id,
            columnId = card.columnId,
            title = card.title,
            description = card.description,
            coverImageUrl = card.coverImageUrl,
            position = card.position,
            taskId = card.taskId,
            createdAt = card.createdAt.format(dateFormatter),
            updatedAt = card.updatedAt.format(dateFormatter)
        )
    }
}
