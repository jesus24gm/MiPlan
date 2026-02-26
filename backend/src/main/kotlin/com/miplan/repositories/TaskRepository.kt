package com.miplan.repositories

import com.miplan.database.Boards
import com.miplan.database.DatabaseFactory.dbQuery
import com.miplan.database.Tasks
import com.miplan.models.entities.Task
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

/**
 * Repositorio para operaciones de tareas en la base de datos
 */
class TaskRepository {
    
    /**
     * Crea una nueva tarea
     */
    suspend fun create(
        title: String,
        description: String?,
        priority: String,
        dueDate: LocalDateTime?,
        imageUrl: String?,
        boardId: Int?,
        createdBy: Int
    ): Task? = dbQuery {
        val insertStatement = Tasks.insert {
            it[Tasks.title] = title
            it[Tasks.description] = description
            it[Tasks.status] = "PENDING"
            it[Tasks.priority] = priority
            it[Tasks.dueDate] = dueDate
            it[Tasks.imageUrl] = imageUrl
            it[Tasks.boardId] = boardId
            it[Tasks.createdBy] = createdBy
            it[Tasks.createdAt] = LocalDateTime.now()
            it[Tasks.updatedAt] = LocalDateTime.now()
        }
        
        insertStatement.resultedValues?.singleOrNull()?.let(::rowToTask)
    }
    
    /**
     * Busca una tarea por ID
     */
    suspend fun findById(id: Int): Task? = dbQuery {
        Tasks.select { Tasks.id eq id }
            .map(::rowToTask)
            .singleOrNull()
    }
    
    /**
     * Obtiene una tarea por ID (síncrono para uso en servicios)
     */
    fun getTaskById(id: Int): Task? {
        return org.jetbrains.exposed.sql.transactions.transaction {
            Tasks.select { Tasks.id eq id }
                .map(::rowToTask)
                .singleOrNull()
        }
    }
    
    /**
     * Obtiene todas las tareas de un usuario
     */
    suspend fun findByUserId(userId: Int): List<Task> = dbQuery {
        Tasks.select { Tasks.createdBy eq userId }
            .orderBy(Tasks.createdAt to SortOrder.DESC)
            .map(::rowToTask)
    }
    
    /**
     * Obtiene tareas por tablero
     */
    suspend fun findByBoardId(boardId: Int): List<Task> = dbQuery {
        Tasks.select { Tasks.boardId eq boardId }
            .orderBy(Tasks.createdAt to SortOrder.DESC)
            .map(::rowToTask)
    }
    
    /**
     * Obtiene tareas por estado
     */
    suspend fun findByStatus(userId: Int, status: String): List<Task> = dbQuery {
        Tasks.select { (Tasks.createdBy eq userId) and (Tasks.status eq status) }
            .orderBy(Tasks.createdAt to SortOrder.DESC)
            .map(::rowToTask)
    }
    
    /**
     * Obtiene tareas por fecha
     */
    suspend fun findByDate(userId: Int, date: LocalDateTime): List<Task> = dbQuery {
        val startOfDay = date.withHour(0).withMinute(0).withSecond(0)
        val endOfDay = date.withHour(23).withMinute(59).withSecond(59)
        
        Tasks.select {
            (Tasks.createdBy eq userId) and
            (Tasks.dueDate.isNotNull()) and
            (Tasks.dueDate greaterEq startOfDay) and
            (Tasks.dueDate lessEq endOfDay)
        }
            .orderBy(Tasks.dueDate to SortOrder.ASC)
            .map(::rowToTask)
    }
    
    /**
     * Actualiza una tarea
     */
    suspend fun update(
        id: Int,
        title: String,
        description: String?,
        status: String,
        priority: String,
        dueDate: LocalDateTime?,
        imageUrl: String?,
        boardId: Int?
    ): Boolean = dbQuery {
        Tasks.update({ Tasks.id eq id }) {
            it[Tasks.title] = title
            it[Tasks.description] = description
            it[Tasks.status] = status
            it[Tasks.priority] = priority
            it[Tasks.dueDate] = dueDate
            it[Tasks.imageUrl] = imageUrl
            it[Tasks.boardId] = boardId
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }
    
    /**
     * Actualiza solo el estado de una tarea
     */
    suspend fun updateStatus(id: Int, status: String): Boolean = dbQuery {
        Tasks.update({ Tasks.id eq id }) {
            it[Tasks.status] = status
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }
    
    /**
     * Elimina una tarea
     */
    suspend fun delete(id: Int): Boolean = dbQuery {
        Tasks.deleteWhere { Tasks.id eq id } > 0
    }
    
    /**
     * Verifica si una tarea pertenece a un usuario
     */
    suspend fun isTaskOwnedByUser(taskId: Int, userId: Int): Boolean = dbQuery {
        Tasks.select { (Tasks.id eq taskId) and (Tasks.createdBy eq userId) }
            .count() > 0
    }
    
    /**
     * Obtiene el nombre del tablero asociado a una tarea
     */
    suspend fun getBoardName(boardId: Int?): String? = dbQuery {
        if (boardId == null) return@dbQuery null
        
        Boards.select { Boards.id eq boardId }
            .map { it[Boards.name] }
            .singleOrNull()
    }
    
    /**
     * Cuenta el total de tareas
     */
    suspend fun countTasks(): Int = dbQuery {
        Tasks.selectAll().count().toInt()
    }
    
    /**
     * Cuenta las tareas completadas
     */
    suspend fun countCompletedTasks(): Int = dbQuery {
        Tasks.select { Tasks.status eq "COMPLETED" }.count().toInt()
    }
    
    /**
     * Cuenta las tareas pendientes (PENDING o IN_PROGRESS)
     */
    suspend fun countPendingTasks(): Int = dbQuery {
        Tasks.select { 
            (Tasks.status eq "PENDING") or (Tasks.status eq "IN_PROGRESS")
        }.count().toInt()
    }
    
    /**
     * Convierte una fila de la base de datos a un objeto Task
     */
    private fun rowToTask(row: ResultRow): Task {
        return Task(
            id = row[Tasks.id],
            title = row[Tasks.title],
            description = row[Tasks.description],
            status = row[Tasks.status],
            priority = row[Tasks.priority],
            dueDate = row[Tasks.dueDate],
            imageUrl = row[Tasks.imageUrl],
            boardId = row[Tasks.boardId],
            createdBy = row[Tasks.createdBy],
            createdAt = row[Tasks.createdAt],
            updatedAt = row[Tasks.updatedAt]
        )
    }
}
