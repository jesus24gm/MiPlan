package com.miplan.data.repository

import com.miplan.data.remote.ApiService
import com.miplan.data.remote.dto.request.CreateChecklistItemRequest
import com.miplan.data.remote.dto.request.CreateChecklistRequest
import com.miplan.data.remote.dto.request.UpdateChecklistItemRequest
import com.miplan.data.remote.dto.request.UpdateChecklistRequest
import com.miplan.domain.model.Checklist
import com.miplan.domain.model.ChecklistItem
import com.miplan.domain.repository.ChecklistRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChecklistRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ChecklistRepository {
    
    override suspend fun getChecklistsByCard(cardId: Int): Result<List<Checklist>> {
        return try {
            val response = apiService.getChecklistsByCard(cardId)
            
            if (response.success && response.data != null) {
                val checklists = response.data.map { it.toDomain() }
                Result.success(checklists)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createChecklist(
        cardId: Int,
        title: String
    ): Result<Checklist> {
        return try {
            val request = CreateChecklistRequest(
                cardId = cardId,
                title = title
            )
            
            val response = apiService.createChecklist(request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateChecklist(
        id: Int,
        title: String
    ): Result<Checklist> {
        return try {
            val request = UpdateChecklistRequest(title = title)
            val response = apiService.updateChecklist(id, request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteChecklist(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteChecklist(id)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createChecklistItem(
        checklistId: Int,
        title: String,
        position: Int?
    ): Result<ChecklistItem> {
        return try {
            val request = CreateChecklistItemRequest(
                checklistId = checklistId,
                title = title,
                position = position
            )
            
            val response = apiService.createChecklistItem(request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateChecklistItem(
        id: Int,
        title: String?,
        isCompleted: Boolean?
    ): Result<ChecklistItem> {
        return try {
            val request = UpdateChecklistItemRequest(
                title = title,
                isCompleted = isCompleted
            )
            
            val response = apiService.updateChecklistItem(id, request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteChecklistItem(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteChecklistItem(id)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
