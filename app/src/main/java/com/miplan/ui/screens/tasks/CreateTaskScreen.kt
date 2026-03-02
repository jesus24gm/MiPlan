package com.miplan.ui.screens.tasks

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.miplan.viewmodel.TaskViewModel

/**
 * Pantalla para crear una nueva tarea (wrapper de TaskFormScreen)
 */
@Composable
fun CreateTaskScreen(
    onNavigateBack: () -> Unit,
    onTaskCreated: () -> Unit,
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    TaskFormScreen(
        taskId = null, // null indica modo creación
        onNavigateBack = onNavigateBack,
        onTaskSaved = onTaskCreated,
        taskViewModel = taskViewModel
    )
}
