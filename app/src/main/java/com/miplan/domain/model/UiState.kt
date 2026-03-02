package com.miplan.domain.model

/**
 * Clase sellada para representar estados de UI
 */
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

/**
 * Extensión para verificar si el estado es exitoso
 */
fun <T> UiState<T>.isSuccess(): Boolean = this is UiState.Success

/**
 * Extensión para verificar si el estado es de error
 */
fun <T> UiState<T>.isError(): Boolean = this is UiState.Error

/**
 * Extensión para verificar si el estado está cargando
 */
fun <T> UiState<T>.isLoading(): Boolean = this is UiState.Loading

/**
 * Extensión para obtener los datos si el estado es exitoso
 */
fun <T> UiState<T>.getDataOrNull(): T? {
    return if (this is UiState.Success) this.data else null
}
