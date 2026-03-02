package com.miplan.ui.screens.kanban

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Diálogo para crear una tarjeta
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCardDialog(
    columnTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String?, dueDate: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Validar si la fecha seleccionada es pasada
    val isPastDate = remember(selectedDate) {
        if (selectedDate != null) {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val taskDate = dateFormat.parse(selectedDate!!.substringBefore("T"))
                val now = Calendar.getInstance().time
                taskDate != null && taskDate.before(now)
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "Crear Tarjeta",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "en $columnTitle",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    placeholder = { Text("Ej: Implementar login") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)") },
                    placeholder = { Text("Detalles de la tarjeta...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
                
                // Selector de fecha
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedDate?.let { "Fecha: ${it.substringBefore("T")}" } 
                            ?: "Agregar fecha límite"
                    )
                }
                
                if (selectedDate != null) {
                    TextButton(
                        onClick = { selectedDate = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Quitar fecha")
                    }
                }
                
                // Advertencia de fecha pasada
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
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "⚠️ Esta fecha ya pasó. No podrás crear la tarjeta.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                // Validar fecha pasada antes de crear
                                if (isPastDate) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "No puedes asignar una fecha pasada",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                    return@Button
                                }
                                
                                onConfirm(
                                    title.trim(),
                                    description.trim().ifBlank { null },
                                    selectedDate
                                )
                            }
                        },
                        enabled = title.isNotBlank() && !isPastDate
                    ) {
                        Text("Crear")
                    }
                }
                
                // Snackbar para mostrar mensajes de error
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.ofEpochMilli(millis)
                            // Usar mediodía para evitar problemas de zona horaria
                            val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                                .withHour(12).withMinute(0).withSecond(0)
                            selectedDate = date.toString()
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
            DatePicker(state = datePickerState)
        }
    }
}
