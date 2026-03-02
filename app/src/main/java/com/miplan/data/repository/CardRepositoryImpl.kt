package com.miplan.data.repository

import com.miplan.data.remote.ApiService
import com.miplan.data.remote.dto.request.CreateCardRequest
import com.miplan.data.remote.dto.request.CreateTaskFromCardRequest
import com.miplan.data.remote.dto.request.LinkTaskToCardRequest
import com.miplan.data.remote.dto.request.MoveCardRequest
import com.miplan.data.remote.dto.request.UpdateCardRequest
import com.miplan.domain.model.Card
import com.miplan.domain.repository.CardRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : CardRepository {
    
    override suspend fun getCardsByColumn(columnId: Int): Result<List<Card>> {
        return try {
            val response = apiService.getCardsByColumn(columnId)
            
            if (response.success && response.data != null) {
                val cards = response.data.map { it.toDomain() }
                Result.success(cards)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCardById(id: Int): Result<Card> {
        return try {
            val response = apiService.getCardById(id)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createCard(
        columnId: Int,
        title: String,
        description: String?,
        position: Int?,
        taskId: Int?,
        dueDate: String?,
        priority: String?,
        labels: String?
    ): Result<Card> {
        return try {
            val request = CreateCardRequest(
                columnId = columnId,
                title = title,
                description = description,
                taskId = taskId,
                position = position,
                dueDate = dueDate,
                priority = priority,
                labels = labels
            )
            
            val response = apiService.createCard(request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCard(
        id: Int,
        title: String?,
        description: String?,
        dueDate: String?,
        priority: String?,
        labels: String?
    ): Result<Card> {
        return try {
            val request = UpdateCardRequest(
                title = title,
                description = description,
                dueDate = dueDate,
                priority = priority,
                labels = labels
            )
            
            val response = apiService.updateCard(id, request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteCard(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteCard(id)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun moveCard(id: Int, newColumnId: Int, newPosition: Int): Result<Card> {
        return try {
            val request = MoveCardRequest(
                newColumnId = newColumnId,
                newPosition = newPosition
            )
            
            val response = apiService.moveCard(id, request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun linkTaskToCard(cardId: Int, taskId: Int): Result<Card> {
        return try {
            val request = LinkTaskToCardRequest(taskId = taskId)
            val response = apiService.linkTaskToCard(cardId, request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun unlinkTaskFromCard(cardId: Int): Result<Card> {
        return try {
            val response = apiService.unlinkTaskFromCard(cardId)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createTaskFromCard(
        cardId: Int,
        title: String,
        description: String?,
        priority: String,
        dueDate: String?
    ): Result<Card> {
        return try {
            val request = CreateTaskFromCardRequest(
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate
            )
            
            val response = apiService.createTaskFromCard(cardId, request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
