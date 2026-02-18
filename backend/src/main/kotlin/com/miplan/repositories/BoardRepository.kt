package com.miplan.repositories

import com.miplan.database.Boards
import com.miplan.database.DatabaseFactory.dbQuery
import com.miplan.database.Tasks
import com.miplan.models.entities.Board
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

/**
 * Repositorio para operaciones de tableros en la base de datos
 */
class BoardRepository {
    
    /**
     * Crea un nuevo tablero
     */
    suspend fun create(
        name: String,
        description: String?,
        color: String,
        backgroundImageUrl: String?,
        userId: Int
    ): Board? = dbQuery {
        val insertStatement = Boards.insert {
            it[Boards.name] = name
            it[Boards.description] = description
            it[Boards.color] = color
            it[Boards.backgroundImageUrl] = backgroundImageUrl
            it[Boards.userId] = userId
            it[Boards.createdAt] = LocalDateTime.now()
            it[Boards.updatedAt] = LocalDateTime.now()
        }
        
        insertStatement.resultedValues?.singleOrNull()?.let(::rowToBoard)
    }
    
    /**
     * Busca un tablero por ID
     */
    suspend fun findById(id: Int): Board? = dbQuery {
        Boards.select { Boards.id eq id }
            .map(::rowToBoard)
            .singleOrNull()
    }
    
    /**
     * Obtiene todos los tableros de un usuario
     */
    suspend fun findByUserId(userId: Int): List<Board> = dbQuery {
        Boards.select { Boards.userId eq userId }
            .orderBy(Boards.createdAt to SortOrder.DESC)
            .map(::rowToBoard)
    }
    
    /**
     * Actualiza un tablero
     */
    suspend fun update(
        id: Int,
        name: String?,
        description: String?,
        color: String?,
        backgroundImageUrl: String?
    ): Boolean = dbQuery {
        Boards.update({ Boards.id eq id }) {
            name?.let { value -> it[Boards.name] = value }
            if (description != null) it[Boards.description] = description
            color?.let { value -> it[Boards.color] = value }
            if (backgroundImageUrl != null) it[Boards.backgroundImageUrl] = backgroundImageUrl
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }
    
    /**
     * Elimina un tablero
     */
    suspend fun delete(id: Int): Boolean = dbQuery {
        Boards.deleteWhere { Boards.id eq id } > 0
    }
    
    /**
     * Verifica si un tablero pertenece a un usuario
     */
    suspend fun isBoardOwnedByUser(boardId: Int, userId: Int): Boolean = dbQuery {
        Boards.select { (Boards.id eq boardId) and (Boards.userId eq userId) }
            .count() > 0
    }
    
    /**
     * Cuenta las tareas de un tablero
     */
    suspend fun countTasks(boardId: Int): Int = dbQuery {
        Tasks.select { Tasks.boardId eq boardId }
            .count()
            .toInt()
    }
    
    /**
     * Convierte una fila de la base de datos a un objeto Board
     */
    private fun rowToBoard(row: ResultRow): Board {
        return Board(
            id = row[Boards.id],
            name = row[Boards.name],
            description = row[Boards.description],
            color = row[Boards.color],
            backgroundImageUrl = row[Boards.backgroundImageUrl],
            userId = row[Boards.userId],
            createdAt = row[Boards.createdAt],
            updatedAt = row[Boards.updatedAt]
        )
    }
}
