package com.miplan.services

import com.miplan.models.entities.Board
import com.miplan.models.requests.CreateBoardRequest
import com.miplan.models.requests.UpdateBoardRequest
import com.miplan.models.responses.BoardDetailResponse
import com.miplan.models.responses.BoardResponse
import com.miplan.models.responses.ColumnWithCardsResponse
import com.miplan.repositories.BoardRepository
import com.miplan.repositories.ColumnRepository
import com.miplan.repositories.CardRepository
import java.time.format.DateTimeFormatter

class BoardService(
    private val boardRepository: BoardRepository,
    private val columnRepository: ColumnRepository,
    private val cardRepository: CardRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    suspend fun createBoard(request: CreateBoardRequest, userId: Int): BoardResponse? {
        val board = boardRepository.create(
            name = request.name,
            description = request.description,
            color = request.color,
            backgroundImageUrl = request.backgroundImageUrl,
            userId = userId
        ) ?: return null
        
        return boardToResponse(board)
    }
    
    suspend fun getBoardById(id: Int): BoardResponse? {
        val board = boardRepository.findById(id) ?: return null
        return boardToResponse(board)
    }
    
    suspend fun getBoardDetail(id: Int): BoardDetailResponse? {
        val board = boardRepository.findById(id) ?: return null
        
        val columns = columnRepository.findByBoardId(id).map { column ->
            val cards = cardRepository.findByColumnId(column.id)
            
            ColumnWithCardsResponse(
                id = column.id,
                boardId = column.boardId,
                title = column.title,
                position = column.position,
                cards = cards.map { card ->
                    com.miplan.models.responses.CardResponse(
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
                },
                createdAt = column.createdAt.format(dateFormatter),
                updatedAt = column.updatedAt.format(dateFormatter)
            )
        }
        
        return BoardDetailResponse(
            id = board.id,
            name = board.name,
            description = board.description,
            color = board.color,
            backgroundImageUrl = board.backgroundImageUrl,
            userId = board.userId,
            columns = columns,
            createdAt = board.createdAt.format(dateFormatter),
            updatedAt = board.updatedAt.format(dateFormatter)
        )
    }
    
    suspend fun getBoardsByUserId(userId: Int): List<BoardResponse> {
        val boards = boardRepository.findByUserId(userId)
        return boards.map { boardToResponse(it) }
    }
    
    suspend fun updateBoard(id: Int, request: UpdateBoardRequest): Boolean {
        return boardRepository.update(
            id = id,
            name = request.name,
            description = request.description,
            color = request.color,
            backgroundImageUrl = request.backgroundImageUrl
        )
    }
    
    suspend fun deleteBoard(id: Int): Boolean {
        return boardRepository.delete(id)
    }
    
    suspend fun isBoardOwnedByUser(boardId: Int, userId: Int): Boolean {
        return boardRepository.isBoardOwnedByUser(boardId, userId)
    }
    
    private suspend fun boardToResponse(board: Board): BoardResponse {
        val taskCount = boardRepository.countTasks(board.id)
        
        return BoardResponse(
            id = board.id,
            name = board.name,
            description = board.description,
            color = board.color,
            backgroundImageUrl = board.backgroundImageUrl,
            userId = board.userId,
            createdAt = board.createdAt.format(dateFormatter),
            updatedAt = board.updatedAt.format(dateFormatter),
            taskCount = taskCount
        )
    }
}
