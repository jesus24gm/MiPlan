package com.miplan.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Manager para mostrar Snackbars de forma consistente en la app
 */
object SnackbarManager {
    
    /**
     * Mostrar Snackbar con mensaje de éxito
     */
    fun showSuccess(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        message: String,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "✅ $message",
                actionLabel = actionLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed && onAction != null) {
                onAction()
            }
        }
    }
    
    /**
     * Mostrar Snackbar con mensaje de error
     */
    fun showError(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        message: String,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "❌ $message",
                actionLabel = actionLabel,
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed && onAction != null) {
                onAction()
            }
        }
    }
    
    /**
     * Mostrar Snackbar con mensaje de información
     */
    fun showInfo(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        message: String,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    ) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "ℹ️ $message",
                actionLabel = actionLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed && onAction != null) {
                onAction()
            }
        }
    }
    
    /**
     * Mensajes específicos para tareas
     */
    object TaskMessages {
        fun created(taskTitle: String) = "La tarea \"$taskTitle\" está lista"
        fun updated(taskTitle: String) = "La tarea \"$taskTitle\" ha cambiado"
        fun deleted(taskTitle: String) = "La tarea \"$taskTitle\" ha sido eliminada"
        fun completed(taskTitle: String) = "La tarea \"$taskTitle\" se ha completado"
        fun statusChanged(taskTitle: String, newStatus: String) = 
            "La tarea \"$taskTitle\" ahora está en $newStatus"
    }
    
    /**
     * Mensajes específicos para tarjetas
     */
    object CardMessages {
        fun created(cardTitle: String) = "La tarjeta \"$cardTitle\" está lista"
        fun updated(cardTitle: String) = "La tarjeta \"$cardTitle\" ha cambiado"
        fun deleted(cardTitle: String) = "La tarjeta \"$cardTitle\" ha sido eliminada"
        fun moved(cardTitle: String, columnName: String) = 
            "La tarjeta \"$cardTitle\" se movió a $columnName"
    }
    
    /**
     * Mensajes específicos para tableros
     */
    object BoardMessages {
        fun created(boardTitle: String) = "El tablero \"$boardTitle\" está listo"
        fun updated(boardTitle: String) = "El tablero \"$boardTitle\" ha cambiado"
        fun deleted(boardTitle: String) = "El tablero \"$boardTitle\" ha sido eliminado"
    }
}
