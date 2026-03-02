package com.miplan.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Diálogo combinado para seleccionar fecha y hora
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    initialDate: String? = null,
    initialTime: String? = null,
    onDismiss: () -> Unit,
    onConfirm: (date: String?, time: String?) -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var selectedTime by remember { mutableStateOf(initialTime) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Fecha y hora límite")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector de fecha
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showDatePicker = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Fecha",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = "Fecha",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = selectedDate?.let {
                                        formatDateForDisplay(it)
                                    } ?: "Sin fecha",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        
                        if (selectedDate != null) {
                            IconButton(
                                onClick = { selectedDate = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = "Quitar fecha",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
                
                // Selector de hora (solo si hay fecha)
                if (selectedDate != null) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showTimePicker = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = "Hora",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column {
                                    Text(
                                        text = "Hora",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = selectedTime?.let {
                                            formatTimeForDisplay(it)
                                        } ?: "Sin hora específica",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                            
                            if (selectedTime != null) {
                                IconButton(
                                    onClick = { selectedTime = null },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.AccessTime,
                                        contentDescription = "Quitar hora",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Mensaje informativo y advertencia de fecha pasada
                val isPastDate = remember(selectedDate, selectedTime) {
                    if (selectedDate != null) {
                        try {
                            val now = Calendar.getInstance()
                            val taskCalendar = Calendar.getInstance()
                            
                            if (selectedTime != null) {
                                // Si tiene hora, comparar fecha y hora completa
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                val taskDate = dateFormat.parse("$selectedDate $selectedTime")
                                taskDate != null && taskDate.before(now.time)
                            } else {
                                // Si NO tiene hora, solo comparar la fecha (día)
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val taskDate = dateFormat.parse(selectedDate!!)
                                if (taskDate != null) {
                                    taskCalendar.time = taskDate
                                    // Comparar solo año, mes y día
                                    val taskYear = taskCalendar.get(Calendar.YEAR)
                                    val taskMonth = taskCalendar.get(Calendar.MONTH)
                                    val taskDay = taskCalendar.get(Calendar.DAY_OF_MONTH)
                                    
                                    val nowYear = now.get(Calendar.YEAR)
                                    val nowMonth = now.get(Calendar.MONTH)
                                    val nowDay = now.get(Calendar.DAY_OF_MONTH)
                                    
                                    // Solo es pasada si es ANTES de hoy (no incluye hoy)
                                    when {
                                        taskYear < nowYear -> true
                                        taskYear > nowYear -> false
                                        taskMonth < nowMonth -> true
                                        taskMonth > nowMonth -> false
                                        taskDay < nowDay -> true
                                        else -> false // Hoy o futuro
                                    }
                                } else {
                                    false
                                }
                            }
                        } catch (e: Exception) {
                            false
                        }
                    } else {
                        false
                    }
                }
                
                if (isPastDate) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "⚠️ Esta fecha ya pasó. No podrás guardar la tarea.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                } else {
                    Text(
                        text = if (selectedDate == null) {
                            "Selecciona una fecha para establecer una fecha límite"
                        } else if (selectedTime == null) {
                            "Opcionalmente, selecciona una hora específica"
                        } else {
                            "Fecha y hora configuradas correctamente"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedDate, selectedTime)
                    onDismiss()
                }
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
    
    // DatePicker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.let {
                try {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)?.time
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
            } ?: System.currentTimeMillis()
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = millis
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            selectedDate = sdf.format(calendar.time)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Seleccionar fecha límite",
                        modifier = Modifier.padding(16.dp)
                    )
                },
                headline = {
                    Text(
                        text = datePickerState.selectedDateMillis?.let {
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = it
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                        } ?: "Selecciona una fecha",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                },
                showModeToggle = true
            )
        }
    }
    
    // TimePicker
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime?.split(":")?.get(0)?.toIntOrNull() ?: 12,
            initialMinute = selectedTime?.split(":")?.get(1)?.toIntOrNull() ?: 0,
            is24Hour = true
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Seleccionar hora") },
            text = {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.padding(16.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val hour = String.format("%02d", timePickerState.hour)
                        val minute = String.format("%02d", timePickerState.minute)
                        selectedTime = "$hour:$minute:00"
                        showTimePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Formatea una fecha para mostrar
 */
private fun formatDateForDisplay(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString
    }
}

/**
 * Formatea una hora para mostrar
 */
private fun formatTimeForDisplay(timeString: String): String {
    return try {
        val parts = timeString.split(":")
        "${parts[0]}:${parts[1]}"
    } catch (e: Exception) {
        timeString
    }
}
