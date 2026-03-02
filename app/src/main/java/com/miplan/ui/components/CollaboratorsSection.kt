package com.miplan.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.miplan.domain.model.CollaboratorRole
import com.miplan.domain.model.TaskCollaborator
import com.miplan.domain.model.UiState

/**
 * Sección de colaboradores en la pantalla de detalles de tarea
 */
@Composable
fun CollaboratorsSection(
    collaboratorsState: UiState<List<TaskCollaborator>>,
    currentUserId: Int,
    isOwner: Boolean,
    onAddCollaborator: () -> Unit,
    onRemoveCollaborator: (TaskCollaborator) -> Unit,
    onChangeRole: (TaskCollaborator) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Colaboradores",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (isOwner) {
                    IconButton(onClick = onAddCollaborator) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Agregar colaborador",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Lista de colaboradores
            when (collaboratorsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                is UiState.Success -> {
                    val collaborators = collaboratorsState.data
                    
                    if (collaborators.isEmpty()) {
                        Text(
                            text = "No hay colaboradores en esta tarea",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            collaborators.forEach { collaborator ->
                                CollaboratorItem(
                                    collaborator = collaborator,
                                    isCurrentUser = collaborator.userId == currentUserId,
                                    canManage = isOwner,
                                    onRemove = { onRemoveCollaborator(collaborator) },
                                    onChangeRole = { onChangeRole(collaborator) }
                                )
                            }
                        }
                    }
                }
                
                is UiState.Error -> {
                    Text(
                        text = collaboratorsState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                else -> {}
            }
        }
    }
}

/**
 * Item individual de colaborador
 */
@Composable
private fun CollaboratorItem(
    collaborator: TaskCollaborator,
    isCurrentUser: Boolean,
    canManage: Boolean,
    onRemove: () -> Unit,
    onChangeRole: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = collaborator.userName.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = collaborator.userName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (isCurrentUser) {
                            Text(
                                text = "(Tú)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = collaborator.userEmail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge de rol
                AssistChip(
                    onClick = { if (canManage && !isCurrentUser) onChangeRole() },
                    label = { Text(collaborator.role.displayName) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (collaborator.role) {
                                CollaboratorRole.OWNER -> Icons.Default.Star
                                CollaboratorRole.EDITOR -> Icons.Default.Edit
                                CollaboratorRole.VIEWER -> Icons.Default.Visibility
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (collaborator.role) {
                            CollaboratorRole.OWNER -> MaterialTheme.colorScheme.primaryContainer
                            CollaboratorRole.EDITOR -> MaterialTheme.colorScheme.secondaryContainer
                            CollaboratorRole.VIEWER -> MaterialTheme.colorScheme.tertiaryContainer
                        }
                    )
                )
                
                // Menú de opciones (solo para el propietario)
                if (canManage && !isCurrentUser) {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Más opciones"
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Cambiar rol") },
                                onClick = {
                                    showMenu = false
                                    onChangeRole()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar") },
                                onClick = {
                                    showMenu = false
                                    onRemove()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.error
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
