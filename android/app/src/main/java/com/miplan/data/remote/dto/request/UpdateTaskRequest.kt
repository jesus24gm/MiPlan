package com.miplan.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTaskRequest(
    val title: String,
    val description: String? = null,
    val status: String,
    val priority: String,
    val dueDate: String? = null,
    val boardId: Int? = null
)
