package com.miplan.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.miplan.domain.model.TaskCollaborator

/**
 * Diálogo de confirmación para eliminar un colaborador
 */
@Composable
fun RemoveCollaboratorDialog(
    collaborator: TaskCollaborator,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text("Eliminar colaborador")
        },
        text = {
            Text(
                "¿Estás seguro de que deseas eliminar a ${collaborator.userName} de esta tarea?\n\n" +
                "Esta persona ya no tendrá acceso a la tarea."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
