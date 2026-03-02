package com.miplan.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.miplan.domain.model.CollaboratorRole
import com.miplan.domain.model.User
import com.miplan.domain.model.UiState

/**
 * Diálogo para agregar un colaborador a una tarea
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCollaboratorDialog(
    searchUserState: UiState<User?>,
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit,
    onAdd: (String, CollaboratorRole) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(CollaboratorRole.VIEWER) }
    var showRoleMenu by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text("Agregar colaborador")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo de email con búsqueda
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email del usuario") },
                    placeholder = { Text("usuario@ejemplo.com") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    trailingIcon = {
                        if (email.isNotEmpty()) {
                            IconButton(onClick = { onSearch(email) }) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Resultado de búsqueda
                when (searchUserState) {
                    is UiState.Loading -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buscando usuario...")
                        }
                    }
                    
                    is UiState.Success -> {
                        val user = searchUserState.data
                        if (user != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Column {
                                        Text(
                                            text = user.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = user.email,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        } else if (email.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Usuario no encontrado",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    
                    is UiState.Error -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = searchUserState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    
                    else -> {}
                }
                
                // Selector de rol
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Rol del colaborador",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = showRoleMenu,
                        onExpandedChange = { showRoleMenu = it }
                    ) {
                        OutlinedTextField(
                            value = selectedRole.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar rol") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRoleMenu)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            CollaboratorRole.values().forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = role.displayName,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = when (role) {
                                                    CollaboratorRole.OWNER -> "Control total de la tarea"
                                                    CollaboratorRole.EDITOR -> "Puede editar la tarea"
                                                    CollaboratorRole.VIEWER -> "Solo puede ver la tarea"
                                                },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedRole = role
                                        showRoleMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = when (role) {
                                                CollaboratorRole.OWNER -> Icons.Default.Star
                                                CollaboratorRole.EDITOR -> Icons.Default.Edit
                                                CollaboratorRole.VIEWER -> Icons.Default.Visibility
                                            },
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isNotEmpty() && searchUserState is UiState.Success && searchUserState.data != null) {
                        onAdd(email, selectedRole)
                    }
                },
                enabled = email.isNotEmpty() && searchUserState is UiState.Success && searchUserState.data != null
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
