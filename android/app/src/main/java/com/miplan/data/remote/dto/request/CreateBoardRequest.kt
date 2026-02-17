package com.miplan.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateBoardRequest(
    val name: String,
    val description: String? = null,
    val color: String = "#E3F2FD"
)
