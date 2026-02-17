package com.miplan.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateBoardRequest(
    val name: String,
    val description: String? = null,
    val color: String = "#E3F2FD"
)

@Serializable
data class UpdateBoardRequest(
    val name: String,
    val description: String? = null,
    val color: String
)
