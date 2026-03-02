package com.miplan.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.miplan.domain.model.CollaboratorRole
import com.miplan.domain.model.TaskCollaborator

/**
 * Diálogo para cambiar el rol de un colaborador
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeCollaboratorRoleDialog(
    collaborator: TaskCollaborator,
    onDismiss: () -> Unit,
    onConfirm: (CollaboratorRole) -> Unit
) {
    var selectedRole by remember { mutableStateOf(collaborator.role) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text("Cambiar rol de colaborador")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info del colaborador
                Text(
                    text = "Cambiar rol de: ${collaborator.userName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                // Opciones de rol
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CollaboratorRole.values().forEach { role ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { selectedRole = role },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedRole == role)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                RadioButton(
                                    selected = selectedRole == role,
                                    onClick = { selectedRole = role }
                                )
                                
                                Icon(
                                    imageVector = when (role) {
                                        CollaboratorRole.OWNER -> Icons.Default.Star
                                        CollaboratorRole.EDITOR -> Icons.Default.Edit
                                        CollaboratorRole.VIEWER -> Icons.Default.Visibility
                                    },
                                    contentDescription = null,
                                    tint = if (selectedRole == role)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = role.displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (selectedRole == role) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = when (role) {
                                            CollaboratorRole.OWNER -> "Control total de la tarea y colaboradores"
                                            CollaboratorRole.EDITOR -> "Puede editar y modificar la tarea"
                                            CollaboratorRole.VIEWER -> "Solo puede visualizar la tarea"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedRole) },
                enabled = selectedRole != collaborator.role
            ) {
                Text("Cambiar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
