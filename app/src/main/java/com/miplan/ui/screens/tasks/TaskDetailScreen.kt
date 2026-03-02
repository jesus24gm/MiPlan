package com.miplan.ui.screens.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.miplan.domain.model.TaskPriority
import com.miplan.domain.model.TaskStatus
import com.miplan.domain.model.UiState
import com.miplan.viewmodel.TaskViewModel
import com.miplan.viewmodel.CollaboratorViewModel
import com.miplan.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de detalle de tarea con opciones de editar, eliminar y marcar como completada
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit = {},
    taskViewModel: TaskViewModel = hiltViewModel(),
    collaboratorViewModel: CollaboratorViewModel = hiltViewModel(),
    authViewModel: com.miplan.viewmodel.AuthViewModel = hiltViewModel()
) {
    val taskDetailState by taskViewModel.taskDetailState.collectAsState()
    val updateTaskState by taskViewModel.updateTaskState.collectAsState()
    val deleteTaskState by taskViewModel.deleteTaskState.collectAsState()
    
    // Estados de colaboradores
    val collaboratorsState by collaboratorViewModel.collaboratorsState.collectAsState()
    val searchUserState by collaboratorViewModel.searchUserState.collectAsState()
    val addCollaboratorState by collaboratorViewModel.addCollaboratorState.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddCollaboratorDialog by remember { mutableStateOf(false) }
    var showChangeRoleDialog by remember { mutableStateOf<com.miplan.domain.model.TaskCollaborator?>(null) }
    var showRemoveCollaboratorDialog by remember { mutableStateOf<com.miplan.domain.model.TaskCollaborator?>(null) }
    
    // Obtener ID del usuario autenticado
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserId = currentUser?.id ?: 0
    
    // Cargar tarea y colaboradores al iniciar
    LaunchedEffect(taskId) {
        taskViewModel.loadTaskById(taskId)
        collaboratorViewModel.loadCollaborators(taskId)
    }
    
    // Manejar eliminación exitosa
    LaunchedEffect(deleteTaskState) {
        if (deleteTaskState is UiState.Success) {
            onNavigateBack()
        }
    }
    
    // Manejar actualización exitosa
    LaunchedEffect(updateTaskState) {
        if (updateTaskState is UiState.Success) {
            taskViewModel.loadTaskById(taskId)
            taskViewModel.resetUpdateTaskState()
        }
    }
    
    // Manejar agregar colaborador exitoso
    LaunchedEffect(addCollaboratorState) {
        if (addCollaboratorState is UiState.Success) {
            showAddCollaboratorDialog = false
            collaboratorViewModel.resetAddCollaboratorState()
            collaboratorViewModel.resetSearchUserState()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Tarea") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón eliminar
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = taskDetailState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is UiState.Success -> {
                    val task = state.data
                    val isCompleted = task.status == TaskStatus.COMPLETED
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Estado de completado
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCompleted) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
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
                                        if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isCompleted) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                    Text(
                                        text = if (isCompleted) "Completada" else "Pendiente",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                
                                // Switch para cambiar estado
                                Switch(
                                    checked = isCompleted,
                                    onCheckedChange = {
                                        val newStatus = if (it) TaskStatus.COMPLETED else TaskStatus.PENDING
                                        taskViewModel.updateTaskStatus(task.id, newStatus.name)
                                    }
                                )
                            }
                        }
                        
                        // Título
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Título",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                                )
                            }
                        }
                        
                        // Imagen
                        if (!task.imageUrl.isNullOrBlank()) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Imagen",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
                                    ) {
                                        AsyncImage(
                                            model = task.imageUrl,
                                            contentDescription = "Imagen de la tarea",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Descripción
                        if (!task.description.isNullOrBlank()) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Descripción",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = task.description,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                        
                        // Prioridad
                        Card(modifier = Modifier.fillMaxWidth()) {
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
                                        Icons.Default.Flag,
                                        contentDescription = null,
                                        tint = getPriorityColor(task.priority)
                                    )
                                    Text(
                                        text = "Prioridad",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                                
                                Surface(
                                    color = getPriorityColor(task.priority).copy(alpha = 0.15f),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text(
                                        text = getPriorityText(task.priority),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = getPriorityColor(task.priority),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                        
                        // Fecha límite
                        task.dueDate?.let { date ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Column {
                                        Text(
                                            text = "Fecha límite",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = formatDateForDisplay(date),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Sección de colaboradores
                        CollaboratorsSection(
                            collaboratorsState = collaboratorsState,
                            currentUserId = currentUserId,
                            isOwner = task.createdBy == currentUserId,
                            onAddCollaborator = { showAddCollaboratorDialog = true },
                            onRemoveCollaborator = { collaborator ->
                                showRemoveCollaboratorDialog = collaborator
                            },
                            onChangeRole = { collaborator ->
                                showChangeRoleDialog = collaborator
                            }
                        )
                        
                        // Botón editar
                        OutlinedButton(
                            onClick = { onNavigateToEdit(task.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editar Tarea")
                        }
                    }
                }
                
                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Error al cargar tarea",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = { taskViewModel.loadTaskById(taskId) }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar")
                        }
                    }
                }
                
                else -> {}
            }
            
            // Indicador de carga para actualización/eliminación
            if (updateTaskState is UiState.Loading || deleteTaskState is UiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
    
    // Diálogo para agregar colaborador
    if (showAddCollaboratorDialog) {
        AddCollaboratorDialog(
            searchUserState = searchUserState,
            onDismiss = {
                showAddCollaboratorDialog = false
                collaboratorViewModel.resetSearchUserState()
            },
            onSearch = { email ->
                collaboratorViewModel.searchUserByEmail(email)
            },
            onAdd = { email, role ->
                collaboratorViewModel.addCollaborator(taskId, email, role)
            }
        )
    }
    
    // Diálogo para cambiar rol
    showChangeRoleDialog?.let { collaborator ->
        ChangeCollaboratorRoleDialog(
            collaborator = collaborator,
            onDismiss = { showChangeRoleDialog = null },
            onConfirm = { newRole ->
                collaboratorViewModel.updateCollaboratorRole(taskId, collaborator.userId, newRole)
                showChangeRoleDialog = null
            }
        )
    }
    
    // Diálogo para eliminar colaborador
    showRemoveCollaboratorDialog?.let { collaborator ->
        RemoveCollaboratorDialog(
            collaborator = collaborator,
            onDismiss = { showRemoveCollaboratorDialog = null },
            onConfirm = {
                collaboratorViewModel.removeCollaborator(taskId, collaborator.userId)
                showRemoveCollaboratorDialog = null
            }
        )
    }
    
    // Diálogo de confirmación de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Estás seguro de que quieres eliminar esta tarea? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        taskViewModel.deleteTask(taskId)
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

@Composable
private fun getPriorityColor(priority: TaskPriority): androidx.compose.ui.graphics.Color {
    return when (priority) {
        TaskPriority.LOW -> MaterialTheme.colorScheme.tertiary
        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primary
        TaskPriority.HIGH -> MaterialTheme.colorScheme.error
    }
}

private fun getPriorityText(priority: TaskPriority): String {
    return when (priority) {
        TaskPriority.LOW -> "Baja"
        TaskPriority.MEDIUM -> "Media"
        TaskPriority.HIGH -> "Alta"
    }
}

private fun formatDateForDisplay(dateString: String): String {
    return try {
        when {
            // Formato con espacio y hora específica: "2026-02-18 14:30:00"
            dateString.contains(" ") -> {
                val parts = dateString.split(" ")
                val datePart = parts[0]
                val timePart = parts.getOrNull(1) ?: "00:00:00"
                
                // Parsear fecha
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(datePart)
                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date!!)
                
                // Si la hora no es 00:00:00, mostrarla
                if (timePart != "00:00:00") {
                    val timeFormatted = timePart.substring(0, 5) // HH:mm
                    "$formattedDate antes de las $timeFormatted"
                } else {
                    formattedDate
                }
            }
            // Formato ISO con T: "2026-02-18T14:30:00"
            dateString.contains("T") -> {
                val parts = dateString.split("T")
                val datePart = parts[0]
                val timePart = parts.getOrNull(1) ?: "00:00:00"
                
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(datePart)
                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date!!)
                
                if (timePart != "00:00:00") {
                    val timeFormatted = timePart.substring(0, 5)
                    "$formattedDate antes de las $timeFormatted"
                } else {
                    formattedDate
                }
            }
            // Solo fecha
            else -> {
                val date = try {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateString)
                } catch (e: Exception) {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
                }
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date!!)
            }
        }
    } catch (e: Exception) {
        dateString
    }
}
