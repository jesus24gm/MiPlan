package com.miplan.repositories

import com.miplan.database.Cards
import com.miplan.models.Card
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * Repositorio para operaciones CRUD de tarjetas
 */
class CardRepository {
    
    /**
     * Crear una nueva tarjeta
     */
    fun create(
        columnId: Int,
        title: String,
        description: String?,
        coverImageUrl: String?,
        position: Int?,
        taskId: Int?
    ): Card {
        return transaction {
            // Si no se especifica posición, obtener la siguiente
            val nextPosition = position ?: run {
                Cards.select { Cards.columnId eq columnId }
                    .count()
                    .toInt()
            }
            
            val id = Cards.insertAndGetId {
                it[Cards.columnId] = columnId
                it[Cards.title] = title
                it[Cards.description] = description
                it[Cards.coverImageUrl] = coverImageUrl
                it[Cards.position] = nextPosition
                it[Cards.taskId] = taskId
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
            
            findById(id.value)!!
        }
    }
    
    /**
     * Buscar tarjeta por ID
     */
    fun findById(id: Int): Card? {
        return transaction {
            Cards.select { Cards.id eq id }
                .mapNotNull { rowToCard(it) }
                .singleOrNull()
        }
    }
    
    /**
     * Obtener todas las tarjetas de una columna
     */
    fun findByColumnId(columnId: Int): List<Card> {
        return transaction {
            Cards.select { Cards.columnId eq columnId }
                .orderBy(Cards.position to SortOrder.ASC)
                .map { rowToCard(it) }
        }
    }
    
    /**
     * Actualizar tarjeta
     */
    fun update(
        id: Int,
        title: String?,
        description: String?,
        coverImageUrl: String?,
        position: Int?,
        taskId: Int?
    ): Card? {
        return transaction {
            val updateCount = Cards.update({ Cards.id eq id }) {
                title?.let { value -> it[Cards.title] = value }
                if (description != null) it[Cards.description] = description
                if (coverImageUrl != null) it[Cards.coverImageUrl] = coverImageUrl
                position?.let { value -> it[Cards.position] = value }
                if (taskId != null) it[Cards.taskId] = taskId
                it[updatedAt] = LocalDateTime.now()
            }
            
            if (updateCount > 0) findById(id) else null
        }
    }
    
    /**
     * Eliminar tarjeta
     */
    fun delete(id: Int): Boolean {
        return transaction {
            Cards.deleteWhere { Cards.id eq id } > 0
        }
    }
    
    /**
     * Mover tarjeta a otra columna y/o posición
     */
    fun moveCard(id: Int, newColumnId: Int, newPosition: Int): Card? {
        return transaction {
            val card = findById(id) ?: return@transaction null
            val oldColumnId = card.columnId
            val oldPosition = card.position
            
            if (oldColumnId == newColumnId && oldPosition == newPosition) {
                return@transaction card
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
                        it[position] = position + 1
                    }
                } else {
                    // Mover hacia abajo
                    Cards.update({ 
                        (Cards.columnId eq oldColumnId) and 
                        (Cards.position greater oldPosition) and 
                        (Cards.position lessEq newPosition)
                    }) {
                        it[position] = position - 1
                    }
                }
            } else {
                // Mover a otra columna
                // Decrementar posiciones en columna origen
                Cards.update({ 
                    (Cards.columnId eq oldColumnId) and 
                    (Cards.position greater oldPosition)
                }) {
                    it[position] = position - 1
                }
                
                // Incrementar posiciones en columna destino
                Cards.update({ 
                    (Cards.columnId eq newColumnId) and 
                    (Cards.position greaterEq newPosition)
                }) {
                    it[position] = position + 1
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
    }
    
    /**
     * Copiar tarjeta a otra columna
     */
    fun copyCard(id: Int, targetColumnId: Int): Card? {
        return transaction {
            val originalCard = findById(id) ?: return@transaction null
            
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
    }
    
    /**
     * Mapear ResultRow a Card
     */
    private fun rowToCard(row: ResultRow): Card {
        return Card(
            id = row[Cards.id].value,
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
