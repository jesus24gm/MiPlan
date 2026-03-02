package com.miplan.ui.screens.tasks

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.miplan.viewmodel.TaskViewModel

/**
 * Pantalla para editar una tarea existente (wrapper de TaskFormScreen)
 */
@Composable
fun EditTaskScreen(
    taskId: Int,
    onNavigateBack: () -> Unit,
    onTaskUpdated: () -> Unit,
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    TaskFormScreen(
        taskId = taskId, // taskId indica modo edición
        onNavigateBack = onNavigateBack,
        onTaskSaved = onTaskUpdated,
        taskViewModel = taskViewModel
    )
}
