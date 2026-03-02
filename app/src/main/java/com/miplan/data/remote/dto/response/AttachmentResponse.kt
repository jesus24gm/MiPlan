package com.miplan.data.remote.dto.response

import com.miplan.domain.model.Attachment
import kotlinx.serialization.Serializable

@Serializable
data class AttachmentResponse(
    val id: Int,
    val cardId: Int,
    val fileName: String,
    val fileUrl: String,
    val fileType: String,
    val fileSize: Long,
    val createdAt: String,
    val updatedAt: String
) {
    fun toDomain(): Attachment {
        return Attachment(
            id = id,
            cardId = cardId,
            fileName = fileName,
            fileUrl = fileUrl,
            fileType = fileType,
            fileSize = fileSize,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
