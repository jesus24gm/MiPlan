package com.miplan.repositories

import com.miplan.database.CardAttachments
import com.miplan.database.DatabaseFactory.dbQuery
import com.miplan.models.CardAttachment
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

/**
 * Repositorio para operaciones CRUD de archivos adjuntos
 */
class AttachmentRepository {
    
    /**
     * Crear un nuevo attachment
     */
    suspend fun create(
        cardId: Int,
        fileUrl: String,
        fileName: String,
        fileType: String
    ): CardAttachment? = dbQuery {
        val insertStatement = CardAttachments.insert {
            it[CardAttachments.cardId] = cardId
            it[CardAttachments.fileUrl] = fileUrl
            it[CardAttachments.fileName] = fileName
            it[CardAttachments.fileType] = fileType
            it[createdAt] = LocalDateTime.now()
        }
        
        val id = insertStatement[CardAttachments.id]
        findById(id)
    }
    
    /**
     * Buscar attachment por ID
     */
    suspend fun findById(id: Int): CardAttachment? = dbQuery {
        CardAttachments.select { CardAttachments.id eq id }
            .mapNotNull { rowToAttachment(it) }
            .singleOrNull()
    }
    
    /**
     * Obtener todos los attachments de una tarjeta
     */
    suspend fun findByCardId(cardId: Int): List<CardAttachment> = dbQuery {
        CardAttachments.select { CardAttachments.cardId eq cardId }
            .orderBy(CardAttachments.createdAt to SortOrder.DESC)
            .map { rowToAttachment(it) }
    }
    
    /**
     * Eliminar attachment
     */
    suspend fun delete(id: Int): Boolean = dbQuery {
        CardAttachments.deleteWhere { CardAttachments.id eq id } > 0
    }
    
    /**
     * Mapear ResultRow a CardAttachment
     */
    private fun rowToAttachment(row: ResultRow): CardAttachment {
        return CardAttachment(
            id = row[CardAttachments.id],
            cardId = row[CardAttachments.cardId],
            fileUrl = row[CardAttachments.fileUrl],
            fileName = row[CardAttachments.fileName],
            fileType = row[CardAttachments.fileType],
            createdAt = row[CardAttachments.createdAt]
        )
    }
}
