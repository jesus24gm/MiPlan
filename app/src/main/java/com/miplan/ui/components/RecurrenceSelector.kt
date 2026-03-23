package com.miplan.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miplan.domain.model.RecurrenceType
import com.miplan.domain.model.DayOfWeek

/**
 * Componente para seleccionar la recurrencia de una tarea
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceSelector(
    recurrenceType: RecurrenceType,
    recurrenceInterval: Int,
    selectedDays: List<DayOfWeek>,
    recurrenceEndDate: String?,
    onRecurrenceTypeChange: (RecurrenceType) -> Unit,
    onRecurrenceIntervalChange: (Int) -> Unit,
    onSelectedDaysChange: (List<DayOfWeek>) -> Unit,
    onRecurrenceEndDateChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRecurrenceDialog by remember { mutableStateOf(false) }
    
    // Texto que se muestra en el botón
    val recurrenceText = when (recurrenceType) {
        RecurrenceType.NONE -> "Sin repetir"
        RecurrenceType.DAILY -> if (recurrenceInterval == 1) "Todos los días" else "Cada $recurrenceInterval días"
        RecurrenceType.WEEKLY -> {
            if (selectedDays.isEmpty()) {
                if (recurrenceInterval == 1) "Todas las semanas" else "Cada $recurrenceInterval semanas"
            } else {
                val daysText = selectedDays.joinToString(", ") { it.displayName.take(3) }
                if (recurrenceInterval == 1) "Cada semana: $daysText" else "Cada $recurrenceInterval semanas: $daysText"
            }
        }
        RecurrenceType.MONTHLY -> if (recurrenceInterval == 1) "Todos los meses" else "Cada $recurrenceInterval meses"
        RecurrenceType.YEARLY -> if (recurrenceInterval == 1) "Todos los años" else "Cada $recurrenceInterval años"
    }
    
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { showRecurrenceDialog = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = "Repetir",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = recurrenceText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
    
    if (showRecurrenceDialog) {
        RecurrenceDialog(
            recurrenceType = recurrenceType,
            recurrenceInterval = recurrenceInterval,
            selectedDays = selectedDays,
            recurrenceEndDate = recurrenceEndDate,
            onDismiss = { showRecurrenceDialog = false },
            onConfirm = { type, interval, days, endDate ->
                onRecurrenceTypeChange(type)
                onRecurrenceIntervalChange(interval)
                onSelectedDaysChange(days)
                onRecurrenceEndDateChange(endDate)
                showRecurrenceDialog = false
            }
        )
    }
}

/**
 * Diálogo para configurar la recurrencia
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecurrenceDialog(
    recurrenceType: RecurrenceType,
    recurrenceInterval: Int,
    selectedDays: List<DayOfWeek>,
    recurrenceEndDate: String?,
    onDismiss: () -> Unit,
    onConfirm: (RecurrenceType, Int, List<DayOfWeek>, String?) -> Unit
) {
    var currentType by remember { mutableStateOf(recurrenceType) }
    var currentInterval by remember { mutableStateOf(recurrenceInterval) }
    var currentDays by remember { mutableStateOf(selectedDays) }
    var currentEndDate by remember { mutableStateOf(recurrenceEndDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar repetición") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector de tipo de recurrencia
                Text(
                    text = "Tipo de repetición",
                    style = MaterialTheme.typography.labelLarge
                )
                
                RecurrenceType.values().forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = currentType == type,
                                onClick = { currentType = type }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentType == type,
                            onClick = { currentType = type }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(type.displayName)
                    }
                }
                
                // Intervalo (solo si no es NONE)
                if (currentType != RecurrenceType.NONE) {
                    Divider()
                    
                    Text(
                        text = "Intervalo",
                        style = MaterialTheme.typography.labelLarge
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Cada")
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { if (currentInterval > 1) currentInterval-- },
                                enabled = currentInterval > 1
                            ) {
                                Icon(Icons.Default.Remove, "Disminuir")
                            }
                            
                            Text(
                                text = currentInterval.toString(),
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            IconButton(
                                onClick = { if (currentInterval < 30) currentInterval++ },
                                enabled = currentInterval < 30
                            ) {
                                Icon(Icons.Default.Add, "Aumentar")
                            }
                        }
                        
                        Text(
                            when (currentType) {
                                RecurrenceType.DAILY -> if (currentInterval == 1) "día" else "días"
                                RecurrenceType.WEEKLY -> if (currentInterval == 1) "semana" else "semanas"
                                RecurrenceType.MONTHLY -> if (currentInterval == 1) "mes" else "meses"
                                RecurrenceType.YEARLY -> if (currentInterval == 1) "año" else "años"
                                else -> ""
                            }
                        )
                    }
                }
                
                // Selector de días (solo para semanal)
                if (currentType == RecurrenceType.WEEKLY) {
                    Divider()
                    
                    Text(
                        text = "Días de la semana",
                        style = MaterialTheme.typography.labelLarge
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DayOfWeek.values().forEach { day ->
                            val isSelected = currentDays.contains(day)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    currentDays = if (isSelected) {
                                        currentDays - day
                                    } else {
                                        currentDays + day
                                    }
                                },
                                label = { Text(day.displayName.take(1)) }
                            )
                        }
                    }
                }
                
                // Fecha de finalización
                if (currentType != RecurrenceType.NONE) {
                    Divider()
                    
                    Text(
                        text = "Finalizar repetición",
                        style = MaterialTheme.typography.labelLarge
                    )
                    
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CalendarToday, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(currentEndDate ?: "Sin fecha límite")
                    }
                    
                    if (currentEndDate != null) {
                        TextButton(
                            onClick = { currentEndDate = null },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Quitar fecha límite")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(currentType, currentInterval, currentDays, currentEndDate)
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
    
    if (showDatePicker) {
        DateTimePickerDialog(
            onDismiss = { showDatePicker = false },
            onConfirm = { date, _ ->
                currentEndDate = date
                showDatePicker = false
            }
        )
    }
}
