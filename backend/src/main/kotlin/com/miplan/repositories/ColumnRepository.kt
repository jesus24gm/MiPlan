package com.miplan.repositories

import com.miplan.database.Columns
import com.miplan.database.DatabaseFactory.dbQuery
import com.miplan.models.Column
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

/**
 * Repositorio para operaciones CRUD de columnas
 */
class ColumnRepository {
    
    /**
     * Crear una nueva columna
     */
    suspend fun create(boardId: Int, title: String, position: Int?): Column? = dbQuery {
        try {
            println("DEBUG ColumnRepo: Iniciando creación - boardId=$boardId, title=$title, position=$position")
            
            // Si no se especifica posición, obtener la siguiente
            val nextPosition = position ?: Columns.select { Columns.boardId eq boardId }
                .count()
                .toInt()
            
            println("DEBUG ColumnRepo: nextPosition=$nextPosition")
            
            val insertStatement = Columns.insert {
                it[Columns.boardId] = boardId
                it[Columns.title] = title
                it[Columns.position] = nextPosition
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
            
            val id = insertStatement[Columns.id]
            println("DEBUG ColumnRepo: Columna insertada con id=$id")
            
            val result = findById(id)
            println("DEBUG ColumnRepo: findById devolvió: ${if (result != null) "Column(id=$id)" else "null"}")
            
            result
        } catch (e: Exception) {
            println("ERROR ColumnRepo: ${e.message}")
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Buscar columna por ID
     */
    suspend fun findById(id: Int): Column? = dbQuery {
        Columns.select { Columns.id eq id }
            .mapNotNull { rowToColumn(it) }
            .singleOrNull()
    }
    
    /**
     * Obtener todas las columnas de un tablero
     */
    suspend fun findByBoardId(boardId: Int): List<Column> = dbQuery {
        Columns.select { Columns.boardId eq boardId }
            .orderBy(Columns.position to SortOrder.ASC)
            .map { rowToColumn(it) }
    }
    
    /**
     * Actualizar columna
     */
    suspend fun update(id: Int, title: String?, position: Int?): Boolean = dbQuery {
        Columns.update({ Columns.id eq id }) {
            title?.let { value -> it[Columns.title] = value }
            position?.let { value -> it[Columns.position] = value }
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }
    
    /**
     * Eliminar columna
     */
    suspend fun delete(id: Int): Boolean = dbQuery {
        Columns.deleteWhere { Columns.id eq id } > 0
    }
    
    /**
     * Mover columna a nueva posición
     */
    suspend fun moveColumn(id: Int, newPosition: Int): Column? = dbQuery {
        // Simplificado: solo actualiza la posición sin reordenar otras columnas
        update(id, null, newPosition)
        findById(id)
    }
    
    /**
     * Mapear ResultRow a Column
     */
    private fun rowToColumn(row: ResultRow): Column {
        return Column(
            id = row[Columns.id],
            boardId = row[Columns.boardId],
            title = row[Columns.title],
            position = row[Columns.position],
            createdAt = row[Columns.createdAt],
            updatedAt = row[Columns.updatedAt]
        )
    }
}
