package com.miplan.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    val priority: String,
    val dueDate: String? = null,
    val imageUrl: String? = null,
    val boardId: Int? = null
)
