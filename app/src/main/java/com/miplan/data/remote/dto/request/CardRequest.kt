package com.miplan.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateCardRequest(
    val columnId: Int,
    val title: String,
    val description: String? = null,
    val position: Int? = null,
    val taskId: Int? = null,
    val dueDate: String? = null,
    val priority: String? = null,
    val labels: String? = null
)

@Serializable
data class UpdateCardRequest(
    val title: String? = null,
    val description: String? = null,
    val dueDate: String? = null,
    val priority: String? = null,
    val labels: String? = null
)

@Serializable
data class MoveCardRequest(
    val newColumnId: Int,
    val newPosition: Int
)

@Serializable
data class LinkTaskToCardRequest(
    val taskId: Int
)

@Serializable
data class CreateTaskFromCardRequest(
    val title: String,
    val description: String? = null,
    val priority: String = "MEDIUM",
    val dueDate: String? = null
)
