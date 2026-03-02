package com.miplan.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateColumnRequest(
    val boardId: Int,
    val title: String,
    val position: Int? = null
)

@Serializable
data class UpdateColumnRequest(
    val title: String? = null,
    val position: Int? = null
)

@Serializable
data class MoveColumnRequest(
    val newPosition: Int
)
