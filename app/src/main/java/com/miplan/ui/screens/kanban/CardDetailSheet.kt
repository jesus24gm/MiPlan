package com.miplan.ui.screens.kanban

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.miplan.domain.model.Card
import com.miplan.domain.model.Checklist
import com.miplan.domain.model.ChecklistItem
import com.miplan.ui.components.SnackbarManager
import com.miplan.viewmodel.KanbanViewModel
import kotlinx.coroutines.launch

/**
 * Bottom Sheet para mostrar y editar detalles de una tarjeta
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailSheet(
    card: Card,
    boardId: Int,
    onDismiss: () -> Unit,
    viewModel: KanbanViewModel,
    snackbarHostState: SnackbarHostState
) {
    var showAddItemDialog by remember { mutableStateOf(false) }
    var showDeleteCardDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Int?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    // Observar la tarjeta actualizada desde el ViewModel
    val selectedCard by viewModel.selectedCard.collectAsState()
    val currentCard = selectedCard ?: card
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título de la tarjeta
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentCard.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                }
            }
            
            // Descripción
            if (currentCard.description != null) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Descripción",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        Text(
                            text = currentCard.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            // Botones de edición
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar tarjeta")
                    }
                }
            }
            
            // Fecha límite
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Fecha límite",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        if (currentCard.dueDate != null) {
                            IconButton(onClick = { 
                                viewModel.updateCard(
                                    id = currentCard.id,
                                    title = null,
                                    description = null,
                                    dueDate = "",
                                    priority = null,
                                    labels = null,
                                    boardId = boardId
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar fecha",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    
                    if (currentCard.dueDate != null) {
                        val daysUntilDue = calculateDaysUntilDue(currentCard.dueDate)
                        val dueDateColor = when {
                            daysUntilDue < 0 -> MaterialTheme.colorScheme.error
                            daysUntilDue <= 1 -> MaterialTheme.colorScheme.error
                            daysUntilDue <= 3 -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                        val dueDateIcon = if (daysUntilDue < 0) Icons.Default.Warning else Icons.Default.CalendarToday
                        val dueDateText = when {
                            daysUntilDue < 0 -> "Vencida"
                            daysUntilDue == 0L -> "Vence hoy"
                            daysUntilDue == 1L -> "Vence mañana"
                            else -> "Vence en $daysUntilDue días"
                        }
                        
                        OutlinedCard(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = dueDateIcon,
                                    contentDescription = null,
                                    tint = dueDateColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = formatDueDate(currentCard.dueDate),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = dueDateText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = dueDateColor
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agregar fecha límite")
                        }
                    }
                }
            }
            
            // Items
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Items",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    TextButton(onClick = { showAddItemDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar")
                    }
                }
            }
            
            // Barra de progreso global
            val allItems = currentCard.checklists.flatMap { it.items }
            if (allItems.isNotEmpty()) {
                item {
                    val completed = allItems.count { it.isCompleted }
                    val total = allItems.size
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progreso",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "$completed/$total",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        LinearProgressIndicator(
                            progress = completed.toFloat() / total,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            
            // Lista de items
            items(allItems) { item ->
                ChecklistItemRow(
                    item = item,
                    onToggle = { viewModel.toggleChecklistItem(item.id, !item.isCompleted, boardId) },
                    onDelete = { itemToDelete = item.id },
                    onEdit = { newTitle ->
                        viewModel.updateChecklistItem(item.id, newTitle, boardId)
                    }
                )
            }
            
            // Adjuntos
            if (currentCard.attachments.isNotEmpty()) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Adjuntos (${card.attachments.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                items(card.attachments) { attachment ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = attachment.fileName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = formatFileSize(attachment.fileSize),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Descargar",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            
            // Botón de eliminar tarjeta
            item {
                OutlinedButton(
                    onClick = { showDeleteCardDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar tarjeta")
                }
            }
        }
    }
    
    if (showAddItemDialog) {
        AddChecklistItemDialog(
            checklistTitle = "Items",
            onDismiss = { showAddItemDialog = false },
            onConfirm = { title ->
                // Si no hay checklists, crear uno por defecto con el item
                if (currentCard.checklists.isEmpty()) {
                    viewModel.createChecklistWithItem(currentCard.id, "Items", title, boardId)
                } else {
                    // Usar el primer checklist existente
                    viewModel.createChecklistItem(currentCard.checklists.first().id, title, boardId)
                }
                showAddItemDialog = false
            }
        )
    }
    
    if (showDeleteCardDialog) {
        ConfirmDeleteDialog(
            title = "Eliminar tarjeta",
            message = "¿Estás seguro de que deseas eliminar esta tarjeta? Esta acción no se puede deshacer.",
            onDismiss = { showDeleteCardDialog = false },
            onConfirm = {
                viewModel.deleteCard(card.id, boardId)
                showDeleteCardDialog = false
                SnackbarManager.showSuccess(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = SnackbarManager.CardMessages.deleted(currentCard.title)
                )
                onDismiss()
            }
        )
    }
    
    itemToDelete?.let { itemId ->
        ConfirmDeleteDialog(
            title = "Eliminar item",
            message = "¿Estás seguro de que deseas eliminar este item?",
            onDismiss = { itemToDelete = null },
            onConfirm = {
                viewModel.deleteChecklistItem(itemId, boardId)
                itemToDelete = null
            }
        )
    }
    
    if (showEditDialog) {
        EditCardDialog(
            card = currentCard,
            onDismiss = { showEditDialog = false },
            onConfirm = { title, description, dueDate ->
                viewModel.updateCard(
                    id = currentCard.id,
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    priority = null,
                    labels = null,
                    boardId = boardId
                )
                showEditDialog = false
                SnackbarManager.showSuccess(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = SnackbarManager.CardMessages.updated(title)
                )
            }
        )
    }
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = java.time.Instant.ofEpochMilli(millis)
                            // Usar mediodía para evitar problemas de zona horaria
                            val date = java.time.LocalDateTime.ofInstant(
                                instant,
                                java.time.ZoneId.systemDefault()
                            ).withHour(12).withMinute(0).withSecond(0)
                            viewModel.updateCard(
                                id = currentCard.id,
                                title = null,
                                description = null,
                                dueDate = date.toString(),
                                priority = null,
                                labels = null,
                                boardId = boardId
                            )
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

@Composable
private fun ChecklistSection(
    checklist: Checklist,
    boardId: Int,
    onAddItem: () -> Unit,
    onToggleItem: (Int, Boolean) -> Unit,
    onDeleteItem: (Int) -> Unit,
    onEditItem: ((Int, String) -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = checklist.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                val completed = checklist.items.count { it.isCompleted }
                val total = checklist.items.size
                Text(
                    text = "$completed/$total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            // Progress bar
            if (checklist.items.isNotEmpty()) {
                LinearProgressIndicator(
                    progress = checklist.items.count { it.isCompleted }.toFloat() / checklist.items.size,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            
            // Items
            checklist.items.forEach { item ->
                ChecklistItemRow(
                    item = item,
                    onToggle = { onToggleItem(item.id, !item.isCompleted) },
                    onDelete = { onDeleteItem(item.id) },
                    onEdit = if (onEditItem != null) {
                        { newTitle -> onEditItem(item.id, newTitle) }
                    } else null
                )
            }
            
            // Agregar item
            TextButton(
                onClick = onAddItem,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar item")
            }
        }
    }
}

@Composable
private fun ChecklistItemRow(
    item: ChecklistItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: ((String) -> Unit)? = null
) {
    var showEditDialog by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = { onToggle() }
            )
            
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (item.isCompleted) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
        
        Row {
            if (onEdit != null) {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
    
    if (showEditDialog && onEdit != null) {
        EditChecklistItemDialog(
            currentTitle = item.title,
            onDismiss = { showEditDialog = false },
            onConfirm = { newTitle ->
                onEdit(newTitle)
                showEditDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditCardDialog(
    card: Card,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, String?) -> Unit
) {
    var title by remember { mutableStateOf(card.title) }
    var description by remember { mutableStateOf(card.description ?: "") }
    var dueDate by remember { mutableStateOf(card.dueDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Tarjeta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
                
                // Fecha límite
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (dueDate != null) formatDueDate(dueDate!!) else "Agregar fecha"
                        )
                    }
                    
                    if (dueDate != null) {
                        IconButton(onClick = { dueDate = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Quitar fecha",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            title.trim(),
                            description.trim().ifBlank { null },
                            dueDate
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = java.time.Instant.ofEpochMilli(millis)
                            val date = java.time.LocalDateTime.ofInstant(
                                instant,
                                java.time.ZoneId.systemDefault()
                            ).withHour(12).withMinute(0).withSecond(0)
                            dueDate = date.toString()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
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

@Composable
private fun AddChecklistDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Checklist") },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                placeholder = { Text("Ej: Tareas pendientes") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim())
                    }
                },
                enabled = title.isNotBlank()
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

@Composable
private fun AddChecklistItemDialog(
    checklistTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Item") },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                placeholder = { Text("Ej: Revisar código") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim())
                    }
                },
                enabled = title.isNotBlank()
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

@Composable
private fun EditChecklistItemDialog(
    currentTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf(currentTitle) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Item") },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título del item") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim())
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Guardar")
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
private fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
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

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}

private fun calculateDaysUntilDue(dueDate: String): Long {
    return try {
        // Normalizar formato: reemplazar espacio por T si es necesario
        val normalized = dueDate.replace(" ", "T").substringBefore(".")
        val dueDateParsed = java.time.LocalDateTime.parse(normalized)
        val now = java.time.LocalDateTime.now()
        java.time.Duration.between(now, dueDateParsed).toDays()
    } catch (e: Exception) {
        999
    }
}

private fun formatDueDate(dueDate: String): String {
    return try {
        // Normalizar formato: reemplazar espacio por T si es necesario
        val normalized = dueDate.replace(" ", "T").substringBefore(".")
        val date = java.time.LocalDateTime.parse(normalized)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")
        date.format(formatter)
    } catch (e: Exception) {
        dueDate.replace(" ", "T").substringBefore("T")
    }
}
