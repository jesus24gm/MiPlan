package com.miplan.ui.screens.notifications

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.miplan.domain.model.Notification
import com.miplan.domain.model.NotificationType
import com.miplan.domain.model.UiState
import com.miplan.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de notificaciones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTask: (Int) -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notificationsState by viewModel.notificationsState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Más opciones")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Marcar todas como leídas") },
                            onClick = {
                                viewModel.markAllAsRead()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.DoneAll, null)
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = notificationsState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success -> {
                val notifications = state.data

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "No tienes notificaciones",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications, key = { it.id }) { notification ->
                            NotificationItem(
                                notification = notification,
                                onClick = {
                                    if (!notification.isRead) {
                                        viewModel.markAsRead(notification.id)
                                    }
                                    notification.taskId?.let { onNavigateToTask(it) }
                                },
                                onDelete = {
                                    viewModel.deleteNotification(notification.id)
                                }
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.loadNotifications() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icono según tipo
                Icon(
                    imageVector = when (notification.type) {
                        NotificationType.TASK_ASSIGNED -> Icons.Default.Task
                        NotificationType.TASK_COMPLETED -> Icons.Default.CheckCircle
                        NotificationType.TASK_REMINDER -> Icons.Default.Alarm
                        NotificationType.SYSTEM -> Icons.Default.Notifications
                    },
                    contentDescription = null,
                    tint = if (notification.isRead) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold
                    )

                    Text(
                        text = formatNotificationDate(notification.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Eliminar",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Indicador de no leída
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
            },
            title = { Text("Eliminar notificación") },
            text = { Text("¿Estás seguro de que quieres eliminar esta notificación?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Formatea la fecha de la notificación
 */
private fun formatNotificationDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: return dateString

        val now = Calendar.getInstance()
        val notificationDate = Calendar.getInstance().apply { time = date }

        val diffInMillis = now.timeInMillis - notificationDate.timeInMillis
        val diffInMinutes = diffInMillis / (1000 * 60)
        val diffInHours = diffInMillis / (1000 * 60 * 60)
        val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)

        when {
            diffInMinutes < 1 -> "Ahora"
            diffInMinutes < 60 -> "Hace ${diffInMinutes}m"
            diffInHours < 24 -> "Hace ${diffInHours}h"
            diffInDays < 7 -> "Hace ${diffInDays}d"
            else -> {
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                outputFormat.format(date)
            }
        }
    } catch (e: Exception) {
        dateString
    }
}
