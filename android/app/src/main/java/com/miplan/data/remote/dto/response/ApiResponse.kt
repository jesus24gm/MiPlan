package com.miplan.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * Respuesta genérica de la API
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)
