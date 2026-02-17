package com.miplan.models.responses

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

/**
 * Funciones helper para crear respuestas
 */
fun <T> successResponse(message: String, data: T? = null) = ApiResponse(
    success = true,
    message = message,
    data = data
)

fun errorResponse(message: String) = ApiResponse<Nothing>(
    success = false,
    message = message,
    data = null
)
