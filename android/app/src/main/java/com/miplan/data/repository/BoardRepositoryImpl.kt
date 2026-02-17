package com.miplan.data.repository

import com.miplan.data.remote.ApiService
import com.miplan.data.remote.dto.request.CreateBoardRequest
import com.miplan.domain.model.Board
import com.miplan.domain.repository.BoardRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de tableros
 */
@Singleton
class BoardRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : BoardRepository {
    
    override suspend fun getBoards(): Result<List<Board>> {
        return try {
            val response = apiService.getBoards()
            
            if (response.success && response.data != null) {
                val boards = response.data.map { it.toDomain() }
                Result.success(boards)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getBoardById(id: Int): Result<Board> {
        return try {
            val response = apiService.getBoardById(id)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createBoard(
        name: String,
        description: String?,
        color: String
    ): Result<Board> {
        return try {
            val request = CreateBoardRequest(
                name = name,
                description = description,
                color = color
            )
            
            val response = apiService.createBoard(request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateBoard(
        id: Int,
        name: String,
        description: String?,
        color: String
    ): Result<Board> {
        return try {
            val request = CreateBoardRequest(
                name = name,
                description = description,
                color = color
            )
            
            val response = apiService.updateBoard(id, request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteBoard(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteBoard(id)
            
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
