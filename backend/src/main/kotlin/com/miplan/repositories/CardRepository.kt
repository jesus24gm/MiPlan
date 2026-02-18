package com.miplan.repositories

import com.miplan.database.Cards
import com.miplan.database.DatabaseFactory.dbQuery
import com.miplan.models.Card
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

/**
 * Repositorio para operaciones CRUD de tarjetas
 */
class CardRepository {
    
    /**
     * Crear una nueva tarjeta
     */
    suspend fun create(
        columnId: Int,
        title: String,
        description: String?,
        coverImageUrl: String?,
        position: Int?,
        taskId: Int?
    ): Card? = dbQuery {
        // Si no se especifica posición, obtener la siguiente
        val nextPosition = position ?: Cards.select { Cards.columnId eq columnId }
            .count()
            .toInt()
        
        val insertStatement = Cards.insert {
            it[Cards.columnId] = columnId
            it[Cards.title] = title
            it[Cards.description] = description
            it[Cards.coverImageUrl] = coverImageUrl
            it[Cards.position] = nextPosition
            it[Cards.taskId] = taskId
            it[createdAt] = LocalDateTime.now()
            it[updatedAt] = LocalDateTime.now()
        }
        
        val id = insertStatement[Cards.id]
        findById(id)
    }
    
    /**
     * Buscar tarjeta por ID
     */
    suspend fun findById(id: Int): Card? = dbQuery {
        Cards.select { Cards.id eq id }
            .mapNotNull { rowToCard(it) }
            .singleOrNull()
    }
    
    /**
     * Obtener todas las tarjetas de una columna
     */
    suspend fun findByColumnId(columnId: Int): List<Card> = dbQuery {
        Cards.select { Cards.columnId eq columnId }
            .orderBy(Cards.position to SortOrder.ASC)
            .map { rowToCard(it) }
    }
    
    /**
     * Actualizar tarjeta
     */
    suspend fun update(
        id: Int,
        title: String?,
        description: String?,
        coverImageUrl: String?,
        position: Int?,
        taskId: Int?
    ): Boolean = dbQuery {
        Cards.update({ Cards.id eq id }) {
            title?.let { value -> it[Cards.title] = value }
            if (description != null) it[Cards.description] = description
            if (coverImageUrl != null) it[Cards.coverImageUrl] = coverImageUrl
            position?.let { value -> it[Cards.position] = value }
            if (taskId != null) it[Cards.taskId] = taskId
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }
    
    /**
     * Eliminar tarjeta
     */
    suspend fun delete(id: Int): Boolean = dbQuery {
        Cards.deleteWhere { Cards.id eq id } > 0
    }
    
    /**
     * Mover tarjeta a otra columna y/o posición
     */
    suspend fun moveCard(id: Int, newColumnId: Int, newPosition: Int): Card? = dbQuery {
        val card = findById(id) ?: return@dbQuery null
        val oldColumnId = card.columnId
        val oldPosition = card.position
        
        if (oldColumnId == newColumnId && oldPosition == newPosition) {
            return@dbQuery card
        }
        
        if (oldColumnId == newColumnId) {
            // Mover dentro de la misma columna
            if (newPosition < oldPosition) {
                // Mover hacia arriba
                Cards.update({ 
                    (Cards.columnId eq oldColumnId) and 
                    (Cards.position greaterEq newPosition) and 
                    (Cards.position less oldPosition)
                }) {
                    it.update(position, position.plus(1))
                }
            } else {
                // Mover hacia abajo
                Cards.update({ 
                    (Cards.columnId eq oldColumnId) and 
                    (Cards.position greater oldPosition) and 
                    (Cards.position lessEq newPosition)
                }) {
                    it.update(position, position.minus(1))
                }
            }
        } else {
            // Mover a otra columna
            // Decrementar posiciones en columna origen
            Cards.update({ 
                (Cards.columnId eq oldColumnId) and 
                (Cards.position greater oldPosition)
            }) {
                it.update(position, position.minus(1))
            }
            
            // Incrementar posiciones en columna destino
            Cards.update({ 
                (Cards.columnId eq newColumnId) and 
                (Cards.position greaterEq newPosition)
            }) {
                it.update(position, position.plus(1))
            }
        }
        
        // Actualizar la tarjeta
        Cards.update({ Cards.id eq id }) {
            it[columnId] = newColumnId
            it[position] = newPosition
            it[updatedAt] = LocalDateTime.now()
        }
        
        findById(id)
    }
    
    /**
     * Copiar tarjeta a otra columna
     */
    suspend fun copyCard(id: Int, targetColumnId: Int): Card? = dbQuery {
        val originalCard = findById(id) ?: return@dbQuery null
        
        // Obtener la siguiente posición en la columna destino
        val nextPosition = Cards.select { Cards.columnId eq targetColumnId }
            .count()
            .toInt()
        
        // Crear nueva tarjeta con los mismos datos
        create(
            columnId = targetColumnId,
            title = originalCard.title,
            description = originalCard.description,
            coverImageUrl = originalCard.coverImageUrl,
            position = nextPosition,
            taskId = originalCard.taskId
        )
    }
    
    /**
     * Mapear ResultRow a Card
     */
    private fun rowToCard(row: ResultRow): Card {
        return Card(
            id = row[Cards.id],
            columnId = row[Cards.columnId],
            title = row[Cards.title],
            description = row[Cards.description],
            coverImageUrl = row[Cards.coverImageUrl],
            position = row[Cards.position],
            taskId = row[Cards.taskId],
            createdAt = row[Cards.createdAt],
            updatedAt = row[Cards.updatedAt]
        )
    }
}
