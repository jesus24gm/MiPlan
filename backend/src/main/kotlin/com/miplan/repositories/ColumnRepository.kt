package com.miplan.repositories

import com.miplan.database.Columns
import com.miplan.models.Column
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * Repositorio para operaciones CRUD de columnas
 */
class ColumnRepository {
    
    /**
     * Crear una nueva columna
     */
    fun create(boardId: Int, title: String, position: Int?): Column {
        return transaction {
            // Si no se especifica posición, obtener la siguiente
            val nextPosition = position ?: run {
                Columns.select { Columns.boardId eq boardId }
                    .count()
                    .toInt()
            }
            
            val id = Columns.insertAndGetId {
                it[Columns.boardId] = boardId
                it[Columns.title] = title
                it[Columns.position] = nextPosition
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
            
            findById(id.value)!!
        }
    }
    
    /**
     * Buscar columna por ID
     */
    fun findById(id: Int): Column? {
        return transaction {
            Columns.select { Columns.id eq id }
                .mapNotNull { rowToColumn(it) }
                .singleOrNull()
        }
    }
    
    /**
     * Obtener todas las columnas de un tablero
     */
    fun findByBoardId(boardId: Int): List<Column> {
        return transaction {
            Columns.select { Columns.boardId eq boardId }
                .orderBy(Columns.position to SortOrder.ASC)
                .map { rowToColumn(it) }
        }
    }
    
    /**
     * Actualizar columna
     */
    fun update(id: Int, title: String?, position: Int?): Column? {
        return transaction {
            val updateCount = Columns.update({ Columns.id eq id }) {
                title?.let { value -> it[Columns.title] = value }
                position?.let { value -> it[Columns.position] = value }
                it[updatedAt] = LocalDateTime.now()
            }
            
            if (updateCount > 0) findById(id) else null
        }
    }
    
    /**
     * Eliminar columna
     */
    fun delete(id: Int): Boolean {
        return transaction {
            Columns.deleteWhere { Columns.id eq id } > 0
        }
    }
    
    /**
     * Mover columna a nueva posición
     */
    fun moveColumn(id: Int, newPosition: Int): Column? {
        return transaction {
            val column = findById(id) ?: return@transaction null
            val boardId = column.boardId
            val oldPosition = column.position
            
            if (oldPosition == newPosition) return@transaction column
            
            // Reordenar otras columnas
            if (newPosition < oldPosition) {
                // Mover hacia arriba: incrementar posición de columnas entre newPosition y oldPosition
                Columns.update({ 
                    (Columns.boardId eq boardId) and 
                    (Columns.position greaterEq newPosition) and 
                    (Columns.position less oldPosition)
                }) {
                    it[position] = position + 1
                }
            } else {
                // Mover hacia abajo: decrementar posición de columnas entre oldPosition y newPosition
                Columns.update({ 
                    (Columns.boardId eq boardId) and 
                    (Columns.position greater oldPosition) and 
                    (Columns.position lessEq newPosition)
                }) {
                    it[position] = position - 1
                }
            }
            
            // Actualizar posición de la columna
            update(id, null, newPosition)
        }
    }
    
    /**
     * Mapear ResultRow a Column
     */
    private fun rowToColumn(row: ResultRow): Column {
        return Column(
            id = row[Columns.id].value,
            boardId = row[Columns.boardId],
            title = row[Columns.title],
            position = row[Columns.position],
            createdAt = row[Columns.createdAt],
            updatedAt = row[Columns.updatedAt]
        )
    }
}
