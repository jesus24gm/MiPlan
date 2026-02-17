package com.miplan.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class BoardResponse(
    val id: Int,
    val name: String,
    val description: String? = null,
    val color: String,
    val userId: Int,
    val createdAt: String,
    val updatedAt: String,
    val taskCount: Int = 0
)
