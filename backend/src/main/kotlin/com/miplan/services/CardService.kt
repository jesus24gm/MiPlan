package com.miplan.services

import com.miplan.models.Card
import com.miplan.models.requests.*
import com.miplan.models.responses.CardDetailResponse
import com.miplan.models.responses.CardResponse
import com.miplan.repositories.CardRepository
import com.miplan.repositories.ChecklistRepository
import com.miplan.repositories.AttachmentRepository
import java.time.format.DateTimeFormatter

class CardService(
    private val cardRepository: CardRepository,
    private val checklistRepository: ChecklistRepository,
    private val attachmentRepository: AttachmentRepository,
    private val taskRepository: com.miplan.repositories.TaskRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    suspend fun createCard(request: CreateCardRequest): CardResponse? {
        try {
            println("DEBUG: Creando tarjeta - columnId: ${request.columnId}, title: ${request.title}")
            
            val card = cardRepository.create(
                columnId = request.columnId,
                title = request.title,
                description = request.description,
                coverImageUrl = request.coverImageUrl,
                position = request.position,
                taskId = request.taskId,
                dueDate = request.dueDate?.takeIf { it.isNotBlank() }?.let { java.time.LocalDateTime.parse(it) }
            )
            
            if (card == null) {
                println("ERROR: cardRepository.create() devolvió null")
                return null
            }
            
            println("DEBUG: Tarjeta creada exitosamente - id: ${card.id}")
            return cardToResponse(card)
        } catch (e: Exception) {
            println("ERROR en createCard: ${e.message}")
            e.printStackTrace()
            return null
        }
    }
    
    suspend fun getCardById(id: Int): CardResponse? {
        val card = cardRepository.findById(id) ?: return null
        return cardToResponse(card)
    }
    
    suspend fun getCardDetail(id: Int): CardDetailResponse? {
        val card = cardRepository.findById(id) ?: return null
        
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
    
    suspend fun getCardsByColumnId(columnId: Int): List<CardResponse> {
        val cards = cardRepository.findByColumnId(columnId)
        return cards.map { cardToResponse(it) }
    }
    
    suspend fun updateCard(id: Int, request: UpdateCardRequest): CardResponse? {
        val updated = cardRepository.update(
            id = id,
            title = request.title,
            description = request.description,
            coverImageUrl = request.coverImageUrl,
            position = request.position,
            taskId = request.taskId,
            dueDate = request.dueDate?.takeIf { it.isNotBlank() }?.let { java.time.LocalDateTime.parse(it) }
        )
        
        if (!updated) return null
        
        return cardRepository.findById(id)?.let { cardToResponse(it) }
    }
    
    suspend fun deleteCard(id: Int): Boolean {
        return cardRepository.delete(id)
    }
    
    suspend fun moveCard(id: Int, request: MoveCardRequest): CardResponse? {
        val card = cardRepository.moveCard(
            id = id,
            newColumnId = request.newColumnId,
            newPosition = request.newPosition
        ) ?: return null
        
        return cardToResponse(card)
    }
    
    suspend fun copyCard(id: Int, request: CopyCardRequest): CardResponse? {
        val card = cardRepository.copyCard(
            id = id,
            targetColumnId = request.targetColumnId
        ) ?: return null
        
        return cardToResponse(card)
    }
    
    private suspend fun cardToResponse(card: Card): CardResponse {
        val checklists = checklistRepository.findChecklistsByCardId(card.id)
        val attachments = attachmentRepository.findByCardId(card.id)
        
        return CardResponse(
            id = card.id,
            columnId = card.columnId,
            title = card.title,
            description = card.description,
            coverImageUrl = card.coverImageUrl,
            position = card.position,
            taskId = card.taskId,
            dueDate = card.dueDate?.format(dateFormatter),
            checklists = checklists.map { checklist ->
                val items = checklistRepository.findItemsByChecklistId(checklist.id)
                com.miplan.models.responses.ChecklistResponse(
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
                    createdAt = checklist.createdAt.format(dateFormatter)
                )
            },
            attachments = attachments.map { attachment ->
                com.miplan.models.responses.AttachmentResponse(
                    id = attachment.id,
                    cardId = attachment.cardId,
                    fileUrl = attachment.fileUrl,
                    fileName = attachment.fileName,
                    fileType = attachment.fileType,
                    createdAt = attachment.createdAt.format(dateFormatter)
                )
            },
            createdAt = card.createdAt.format(dateFormatter),
            updatedAt = card.updatedAt.format(dateFormatter)
        )
    }
    
    suspend fun linkTaskToCard(cardId: Int, taskId: Int): CardResponse? {
        // Verificar que la tarea existe
        val task = taskRepository.findById(taskId) ?: return null
        
        // Actualizar la tarjeta con el taskId
        val updated = cardRepository.update(
            id = cardId,
            title = null,
            description = null,
            coverImageUrl = null,
            position = null,
            taskId = taskId,
            dueDate = null
        )
        
        if (!updated) return null
        
        // Obtener la tarjeta actualizada
        val card = cardRepository.findById(cardId) ?: return null
        return cardToResponse(card)
    }
    
    suspend fun unlinkTaskFromCard(cardId: Int): CardResponse? {
        // Actualizar la tarjeta removiendo el taskId
        val updated = cardRepository.update(
            id = cardId,
            title = null,
            description = null,
            coverImageUrl = null,
            position = null,
            taskId = null,
            dueDate = null
        )
        
        if (!updated) return null
        
        // Obtener la tarjeta actualizada
        val card = cardRepository.findById(cardId) ?: return null
        return cardToResponse(card)
    }
    
    suspend fun createTaskFromCard(cardId: Int, request: CreateTaskFromCardRequest, userId: Int): CardResponse? {
        // Obtener la tarjeta
        val card = cardRepository.findById(cardId) ?: return null
        
        // Crear la tarea
        val task = taskRepository.create(
            title = request.title,
            description = request.description,
            priority = request.priority,
            dueDate = request.dueDate?.let { java.time.LocalDateTime.parse(it) },
            imageUrl = card.coverImageUrl,
            boardId = null,
            createdBy = userId
        ) ?: return null
        
        // Vincular la tarea a la tarjeta
        return linkTaskToCard(cardId, task.id)
    }
}
