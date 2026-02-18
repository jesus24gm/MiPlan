package com.miplan.repositories

import com.miplan.database.CardAttachments
import com.miplan.models.CardAttachment
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * Repositorio para operaciones CRUD de archivos adjuntos
 */
class AttachmentRepository {
    
    /**
     * Crear un nuevo attachment
     */
    fun create(
        cardId: Int,
        fileUrl: String,
        fileName: String,
        fileType: String
    ): CardAttachment {
        return transaction {
            val id = CardAttachments.insertAndGetId {
                it[CardAttachments.cardId] = cardId
                it[CardAttachments.fileUrl] = fileUrl
                it[CardAttachments.fileName] = fileName
                it[CardAttachments.fileType] = fileType
                it[createdAt] = LocalDateTime.now()
            }
            
            findById(id.value)!!
        }
    }
    
    /**
     * Buscar attachment por ID
     */
    fun findById(id: Int): CardAttachment? {
        return transaction {
            CardAttachments.select { CardAttachments.id eq id }
                .mapNotNull { rowToAttachment(it) }
                .singleOrNull()
        }
    }
    
    /**
     * Obtener todos los attachments de una tarjeta
     */
    fun findByCardId(cardId: Int): List<CardAttachment> {
        return transaction {
            CardAttachments.select { CardAttachments.cardId eq cardId }
                .orderBy(CardAttachments.createdAt to SortOrder.DESC)
                .map { rowToAttachment(it) }
        }
    }
    
    /**
     * Eliminar attachment
     */
    fun delete(id: Int): Boolean {
        return transaction {
            CardAttachments.deleteWhere { CardAttachments.id eq id } > 0
        }
    }
    
    /**
     * Mapear ResultRow a CardAttachment
     */
    private fun rowToAttachment(row: ResultRow): CardAttachment {
        return CardAttachment(
            id = row[CardAttachments.id].value,
            cardId = row[CardAttachments.cardId],
            fileUrl = row[CardAttachments.fileUrl],
            fileName = row[CardAttachments.fileName],
            fileType = row[CardAttachments.fileType],
            createdAt = row[CardAttachments.createdAt]
        )
    }
}
