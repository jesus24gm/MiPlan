package com.miplan.ui.screens.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.miplan.notifications.NotificationPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val preferences = remember { NotificationPreferences(context) }
    
    var notificationsEnabled by remember { mutableStateOf(preferences.notificationsEnabled) }
    var taskCreatedEnabled by remember { mutableStateOf(preferences.taskCreatedNotificationEnabled) }
    var advanceEnabled by remember { mutableStateOf(preferences.advanceNotificationsEnabled) }
    var reminderEnabled by remember { mutableStateOf(preferences.reminderEnabled) }
    var showAdvanceDialog by remember { mutableStateOf(false) }
    var selectedMinutes by remember { mutableStateOf(preferences.getAdvanceNotificationMinutesList()) }
    
    // Launcher para solicitar permisos de notificación
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // Mostrar diálogo para ir a configuración
        }
    }
    
    // Solicitar permisos en Android 13+
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración de Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Notificaciones generales
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Habilitar notificaciones",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Activa o desactiva todas las notificaciones",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                notificationsEnabled = it
                                preferences.notificationsEnabled = it
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Notificación de tarea creada
            Card(
                modifier = Modifier.fillMaxWidth(),
                enabled = notificationsEnabled
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notificación al crear tarea",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Recibe confirmación inmediata al crear una tarea",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = taskCreatedEnabled,
                            onCheckedChange = {
                                taskCreatedEnabled = it
                                preferences.taskCreatedNotificationEnabled = it
                            },
                            enabled = notificationsEnabled
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Notificaciones anticipadas
            Card(
                modifier = Modifier.fillMaxWidth(),
                enabled = notificationsEnabled
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notificaciones anticipadas",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Recibe avisos antes de la fecha límite",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = advanceEnabled,
                            onCheckedChange = {
                                advanceEnabled = it
                                preferences.advanceNotificationsEnabled = it
                            },
                            enabled = notificationsEnabled
                        )
                    }
                    
                    if (advanceEnabled && notificationsEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showAdvanceDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(preferences.getAdvanceNotificationTimesText())
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Recordatorios
            Card(
                modifier = Modifier.fillMaxWidth(),
                enabled = notificationsEnabled
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Recordatorios",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Recibe recordatorios después de la fecha límite",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = {
                                reminderEnabled = it
                                preferences.reminderEnabled = it
                            },
                            enabled = notificationsEnabled
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón para ir a configuración del sistema
            OutlinedButton(
                onClick = {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Configuración del sistema")
            }
        }
    }
    
    // Diálogo para seleccionar tiempos de anticipación
    if (showAdvanceDialog) {
        MultipleMinutesPickerDialog(
            selectedMinutes = selectedMinutes,
            onDismiss = { showAdvanceDialog = false },
            onConfirm = { newMinutes ->
                selectedMinutes = newMinutes
                preferences.setAdvanceNotificationMinutesList(newMinutes)
                showAdvanceDialog = false
            }
        )
    }
}

@Composable
fun MultipleMinutesPickerDialog(
    selectedMinutes: Set<Int>,
    onDismiss: () -> Unit,
    onConfirm: (Set<Int>) -> Unit
) {
    var tempSelection by remember { mutableStateOf(selectedMinutes.toMutableSet()) }
    
    val availableOptions = listOf(
        15 to "15 minutos",
        30 to "30 minutos",
        60 to "1 hora",
        120 to "2 horas",
        1440 to "1 día",
        2880 to "2 días",
        10080 to "1 semana"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona tiempos de anticipación") },
        text = {
            Column {
                Text(
                    text = "Puedes seleccionar múltiples opciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                availableOptions.forEach { (minutes, label) ->
                    val isChecked = tempSelection.contains(minutes)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                val newSelection = tempSelection.toMutableSet()
                                if (checked) {
                                    newSelection.add(minutes)
                                } else {
                                    newSelection.remove(minutes)
                                }
                                tempSelection = newSelection
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(tempSelection) },
                enabled = tempSelection.isNotEmpty()
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
