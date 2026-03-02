package com.miplan.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateChecklistRequest(
    val cardId: Int,
    val title: String
)

@Serializable
data class UpdateChecklistRequest(
    val title: String
)

@Serializable
data class CreateChecklistItemRequest(
    val checklistId: Int,
    val title: String,
    val position: Int? = null
)

@Serializable
data class UpdateChecklistItemRequest(
    val title: String? = null,
    val isCompleted: Boolean? = null
)
