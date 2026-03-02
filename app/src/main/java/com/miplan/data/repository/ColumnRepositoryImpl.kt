package com.miplan.data.repository

import com.miplan.data.remote.ApiService
import com.miplan.data.remote.dto.request.CreateColumnRequest
import com.miplan.data.remote.dto.request.MoveColumnRequest
import com.miplan.data.remote.dto.request.UpdateColumnRequest
import com.miplan.domain.model.Column
import com.miplan.domain.repository.ColumnRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColumnRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ColumnRepository {
    
    override suspend fun getColumnsByBoard(boardId: Int): Result<List<Column>> {
        return try {
            val response = apiService.getColumnsByBoard(boardId)
            
            if (response.success && response.data != null) {
                val columns = response.data.map { it.toDomain() }
                Result.success(columns)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al cargar columnas: ${e.message}"))
        }
    }
    
    override suspend fun createColumn(
        boardId: Int,
        title: String,
        position: Int?
    ): Result<Column> {
        return try {
            val request = CreateColumnRequest(
                boardId = boardId,
                title = title,
                position = position
            )
            
            val response = apiService.createColumn(request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateColumn(
        id: Int,
        title: String?,
        position: Int?
    ): Result<Column> {
        return try {
            val request = UpdateColumnRequest(
                title = title,
                position = position
            )
            
            val response = apiService.updateColumn(id, request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteColumn(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteColumn(id)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun moveColumn(id: Int, newPosition: Int): Result<Column> {
        return try {
            val request = MoveColumnRequest(newPosition = newPosition)
            val response = apiService.moveColumn(id, request)
            
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
