package com.miplan.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miplan.data.preferences.NotificationPreferences

/**
 * Pantalla de configuración de notificaciones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    notificationPreferences: NotificationPreferences
) {
    // Estados de configuración
    var tasksEnabled by remember { mutableStateOf(notificationPreferences.taskNotificationsEnabled) }
    var cardsEnabled by remember { mutableStateOf(notificationPreferences.cardNotificationsEnabled) }
    var advanceEnabled by remember { mutableStateOf(notificationPreferences.advanceNotificationEnabled) }
    var reminderEnabled by remember { mutableStateOf(notificationPreferences.reminderNotificationsEnabled) }
    
    var defaultHour by remember { mutableStateOf(notificationPreferences.defaultNotificationHour) }
    var defaultMinute by remember { mutableStateOf(notificationPreferences.defaultNotificationMinute) }
    var advanceMinutesList by remember { mutableStateOf(notificationPreferences.getAdvanceNotificationMinutesList()) }
    var reminderMinutes by remember { mutableStateOf(notificationPreferences.reminderDelayHours * 60) }
    
    var showHourPicker by remember { mutableStateOf(false) }
    var showAdvancePicker by remember { mutableStateOf(false) }
    var showReminderPicker by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración de Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección: Notificaciones de Tareas
            Text(
                text = "Tareas",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Toggle: Activar notificaciones de tareas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notificaciones de tareas",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Recibir notificaciones para tareas con fecha límite",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = tasksEnabled,
                            onCheckedChange = { 
                                tasksEnabled = it
                                notificationPreferences.taskNotificationsEnabled = it
                            }
                        )
                    }
                }
            }
            
            // Sección: Notificaciones de Tarjetas
            Text(
                text = "Tarjetas (Kanban)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Toggle: Activar notificaciones de tarjetas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notificaciones de tarjetas",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Recibir notificaciones para tarjetas con fecha límite",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = cardsEnabled,
                            onCheckedChange = { 
                                cardsEnabled = it
                                notificationPreferences.cardNotificationsEnabled = it
                            }
                        )
                    }
                }
            }
            
            // Sección: Configuración de Horarios
            Text(
                text = "Horarios",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Hora por defecto
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Hora por defecto",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Hora de notificación cuando no hay hora específica",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { showHourPicker = true },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%02d:%02d", defaultHour, defaultMinute),
                                maxLines = 1
                            )
                        }
                    }
                    
                    Divider()
                    
                    // Notificación anticipada
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Notificación anticipada",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Notificar antes de la hora límite",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = advanceEnabled,
                                onCheckedChange = { 
                                    advanceEnabled = it
                                    notificationPreferences.advanceNotificationEnabled = it
                                }
                            )
                        }
                        
                        if (advanceEnabled) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tiempo de anticipación",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = { showAdvancePicker = true },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Timer,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (advanceMinutesList.isEmpty()) "Ninguno" else "${advanceMinutesList.size} seleccionados",
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                    
                    Divider()
                    
                    // Recordatorio
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Recordatorio",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Notificar si no se completó después de la fecha límite",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = reminderEnabled,
                                onCheckedChange = { 
                                    reminderEnabled = it
                                    notificationPreferences.reminderNotificationsEnabled = it
                                }
                            )
                        }
                        
                        if (reminderEnabled) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tiempo después de la fecha límite",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = { showReminderPicker = true },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Timer,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = formatMinutes(reminderMinutes),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "Las notificaciones se programan automáticamente cuando creas o editas tareas y tarjetas con fecha límite. Los cambios se aplicarán a las nuevas notificaciones.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
    
    // Diálogo para seleccionar hora por defecto
    if (showHourPicker) {
        HourPickerDialog(
            currentHour = defaultHour,
            currentMinute = defaultMinute,
            onDismiss = { showHourPicker = false },
            onConfirm = { hour, minute ->
                defaultHour = hour
                defaultMinute = minute
                notificationPreferences.defaultNotificationHour = hour
                notificationPreferences.defaultNotificationMinute = minute
                showHourPicker = false
            }
        )
    }
    
    // Diálogo para seleccionar tiempo de anticipación
    if (showAdvancePicker) {
        MultipleMinutesPickerDialog(
            title = "Tiempos de anticipación",
            currentMinutes = advanceMinutesList,
            options = listOf(15, 30, 60, 120, 180, 360, 720, 1440), // 15min a 24h
            onDismiss = { showAdvancePicker = false },
            onConfirm = { minutes ->
                advanceMinutesList = minutes
                notificationPreferences.setAdvanceNotificationMinutesList(minutes)
                showAdvancePicker = false
            }
        )
    }
    
    // Diálogo para seleccionar tiempo de recordatorio
    if (showReminderPicker) {
        MinutesPickerDialog(
            title = "Tiempo de recordatorio",
            currentMinutes = reminderMinutes,
            options = listOf(15, 30, 60, 120, 180, 360, 720, 1440), // 15min a 24h
            onDismiss = { showReminderPicker = false },
            onConfirm = { minutes ->
                reminderMinutes = minutes
                notificationPreferences.reminderDelayHours = minutes / 60
                showReminderPicker = false
            }
        )
    }
}

@Composable
private fun HourPickerDialog(
    currentHour: Int,
    currentMinute: Int = 0,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var selectedHour by remember { mutableStateOf(currentHour) }
    var selectedMinute by remember { mutableStateOf(currentMinute) }
    var timeText by remember { mutableStateOf(String.format("%02d:%02d", currentHour, currentMinute)) }
    var isError by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Schedule, contentDescription = null)
        },
        title = {
            Text("Seleccionar hora")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo de texto para escribir la hora
                OutlinedTextField(
                    value = timeText,
                    onValueChange = { newValue ->
                        // Permitir números y dos puntos
                        if (newValue.all { it.isDigit() || it == ':' }) {
                            timeText = newValue
                            
                            // Intentar parsear el formato HH:MM o HH
                            val parts = newValue.split(":")
                            when {
                                // Formato HH:MM
                                parts.size == 2 -> {
                                    val hour = parts[0].toIntOrNull()
                                    val minute = parts[1].toIntOrNull()
                                    if (hour != null && hour in 0..23 && minute != null && minute in 0..59) {
                                        selectedHour = hour
                                        selectedMinute = minute
                                        isError = false
                                    } else {
                                        isError = true
                                    }
                                }
                                // Solo HH
                                parts.size == 1 && newValue.isNotEmpty() -> {
                                    val hour = newValue.toIntOrNull()
                                    if (hour != null && hour in 0..23) {
                                        selectedHour = hour
                                        selectedMinute = 0
                                        isError = false
                                    } else {
                                        isError = true
                                    }
                                }
                                // Vacío
                                newValue.isEmpty() -> {
                                    isError = false
                                }
                                else -> {
                                    isError = true
                                }
                            }
                        }
                    },
                    label = { Text("Hora (HH:MM)") },
                    placeholder = { Text("13:30") },
                    supportingText = {
                        if (isError) {
                            Text("Formato: HH:MM (ej: 13:30)")
                        } else {
                            Text("Puedes escribir solo la hora o incluir minutos")
                        }
                    },
                    isError = isError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    text = "o desliza para seleccionar solo la hora",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Visualización de la hora seleccionada
                Text(
                    text = String.format("%02d:%02d", selectedHour, selectedMinute),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Slider solo para horas
                Slider(
                    value = selectedHour.toFloat(),
                    onValueChange = { 
                        selectedHour = it.toInt()
                        selectedMinute = 0
                        timeText = String.format("%02d:00", selectedHour)
                        isError = false
                    },
                    valueRange = 0f..23f,
                    steps = 22
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("00:00", style = MaterialTheme.typography.bodySmall)
                    Text("23:00", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedHour, selectedMinute) },
                enabled = !isError && timeText.isNotEmpty()
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun MultipleMinutesPickerDialog(
    title: String,
    currentMinutes: Set<Int>,
    options: List<Int>,
    onDismiss: () -> Unit,
    onConfirm: (Set<Int>) -> Unit
) {
    var selectedMinutes by remember { mutableStateOf(currentMinutes.toMutableSet()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Timer, contentDescription = null)
        },
        title = {
            Text(title)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Selecciona uno o más tiempos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                options.forEach { minutes ->
                    val label = formatMinutes(minutes)
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedMinutes.contains(minutes),
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectedMinutes.add(minutes)
                                } else {
                                    selectedMinutes.remove(minutes)
                                }
                            }
                        )
                        Text(
                            text = label,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedMinutes) }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun MinutesPickerDialog(
    title: String,
    currentMinutes: Int,
    options: List<Int>,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedMinutes by remember { mutableStateOf(currentMinutes) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Timer, contentDescription = null)
        },
        title = {
            Text(title)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { minutes ->
                    val label = formatMinutes(minutes)
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMinutes == minutes,
                            onClick = { selectedMinutes = minutes }
                        )
                        Text(
                            text = label,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedMinutes) }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Formatea minutos a un string legible
 * Menos de 60 min: "X min"
 * 1 hora o más: "Xh Ymin" o solo "Xh" si no hay minutos
 */
private fun formatMinutes(totalMinutes: Int): String {
    return when {
        totalMinutes < 60 -> "$totalMinutes min"
        totalMinutes % 60 == 0 -> "${totalMinutes / 60}h"
        else -> {
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            "${hours}h ${minutes}min"
        }
    }
}
