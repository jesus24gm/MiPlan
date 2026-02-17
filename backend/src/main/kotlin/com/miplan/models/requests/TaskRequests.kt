package com.miplan.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    val priority: String,
    val dueDate: String? = null,
    val boardId: Int? = null
)

@Serializable
data class UpdateTaskRequest(
    val title: String,
    val description: String? = null,
    val status: String,
    val priority: String,
    val dueDate: String? = null,
    val boardId: Int? = null
)

@Serializable
data class UpdateTaskStatusRequest(
    val status: String
)
