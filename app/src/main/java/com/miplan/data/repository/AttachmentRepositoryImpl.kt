package com.miplan.data.repository

import com.miplan.data.remote.ApiService
import com.miplan.domain.model.Attachment
import com.miplan.domain.repository.AttachmentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttachmentRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AttachmentRepository {
    
    override suspend fun getAttachmentsByCard(cardId: Int): Result<List<Attachment>> {
        return try {
            val response = apiService.getAttachmentsByCard(cardId)
            
            if (response.success && response.data != null) {
                val attachments = response.data.map { it.toDomain() }
                Result.success(attachments)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteAttachment(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteAttachment(id)
            
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
