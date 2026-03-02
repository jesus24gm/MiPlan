package com.miplan.ui.screens.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.miplan.domain.model.Task
import com.miplan.domain.model.TaskPriority
import com.miplan.domain.model.TaskStatus
import com.miplan.domain.model.UiState
import com.miplan.ui.components.SnackbarManager
import com.miplan.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla de lista de tareas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    onNavigateToCreateTask: () -> Unit = {},
    onNavigateToCreateBoard: () -> Unit = {},
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val tasksState by taskViewModel.tasksState.collectAsState()
    val updateTaskState by taskViewModel.updateTaskState.collectAsState()
    
    // Estado del FAB expandible (ya no se usa, se abre directamente el diálogo)
    // var fabExpanded by remember { mutableStateOf(false) }
    
    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Cargar tareas al iniciar
    LaunchedEffect(Unit) {
        taskViewModel.loadTasks()
    }
    
    // Mostrar mensaje al actualizar tarea (marcar como completada)
    val updateTask = (updateTaskState as? UiState.Success<Task>)?.data
    LaunchedEffect(updateTask) {
        updateTask?.let { task ->
            SnackbarManager.showSuccess(
                scope = scope,
                snackbarHostState = snackbarHostState,
                message = SnackbarManager.TaskMessages.updated(task.title)
            )
            taskViewModel.loadTasks()
            taskViewModel.resetUpdateTaskState()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tareas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTask
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear tarea")
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = tasksState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is UiState.Success -> {
                    val tasks = state.data
                    
                    if (tasks.isEmpty()) {
                        // Estado vacío
                        EmptyTasksView(modifier = Modifier.align(Alignment.Center))
                    } else {
                        // Lista de tareas
                        TaskList(
                            tasks = tasks,
                            onTaskClick = onNavigateToTaskDetail,
                            onToggleComplete = { task ->
                                val newStatus = if (task.status == TaskStatus.COMPLETED) {
                                    TaskStatus.PENDING
                                } else {
                                    TaskStatus.COMPLETED
                                }
                                taskViewModel.updateTaskStatus(task.id, newStatus.name)
                            }
                        )
                    }
                }
                
                is UiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { taskViewModel.loadTasks() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                else -> {}
            }
        }
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    onTaskClick: (Int) -> Unit,
    onToggleComplete: (Task) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Agrupar por estado
        val pendingTasks = tasks.filter { it.status != TaskStatus.COMPLETED }
        val completedTasks = tasks.filter { it.status == TaskStatus.COMPLETED }
        
        // Tareas pendientes
        if (pendingTasks.isNotEmpty()) {
            item {
                Text(
                    text = "Pendientes (${pendingTasks.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(pendingTasks, key = { it.id }) { task ->
                TaskItem(
                    task = task,
                    onTaskClick = onTaskClick,
                    onToggleComplete = onToggleComplete
                )
            }
        }
        
        // Tareas completadas
        if (completedTasks.isNotEmpty()) {
            item {
                Text(
                    text = "Completadas (${completedTasks.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            items(completedTasks, key = { it.id }) { task ->
                TaskItem(
                    task = task,
                    onTaskClick = onTaskClick,
                    onToggleComplete = onToggleComplete
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskItem(
    task: Task,
    onTaskClick: (Int) -> Unit,
    onToggleComplete: (Task) -> Unit
) {
    val isCompleted = task.status == TaskStatus.COMPLETED
    
    Card(
        onClick = { onTaskClick(task.id) },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox para completar
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleComplete(task) },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
            
            // Miniatura de imagen (si existe)
            if (!task.imageUrl.isNullOrBlank()) {
                Card(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(MaterialTheme.shapes.small)
                ) {
                    AsyncImage(
                        model = task.imageUrl,
                        contentDescription = "Imagen de la tarea",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            // Contenido de la tarea
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Título
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                // Descripción (si existe)
                if (!task.description.isNullOrBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                // Información adicional
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Prioridad
                    PriorityChip(priority = task.priority)
                    
                    // Fecha límite
                    task.dueDate?.let { date ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = formatDate(date),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
            
            // Icono de navegación
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Ver detalles",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun PriorityChip(priority: TaskPriority) {
    val (color, text) = when (priority) {
        TaskPriority.LOW -> MaterialTheme.colorScheme.tertiary to "Baja"
        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primary to "Media"
        TaskPriority.HIGH -> MaterialTheme.colorScheme.error to "Alta"
    }
    
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun EmptyTasksView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Default.TaskAlt,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Text(
            text = "No hay tareas",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = "Crea tu primera tarea usando el botón +",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
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
            text = "Error al cargar tareas",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        when {
            // Formato con espacio y hora específica: "2026-02-18 14:30:00"
            dateString.contains(" ") -> {
                val parts = dateString.split(" ")
                val datePart = parts[0]
                val timePart = parts.getOrNull(1) ?: "00:00:00"
                
                // Parsear fecha
                val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(datePart)
                val formattedDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(date!!)
                
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
                
                val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(datePart)
                val formattedDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(date!!)
                
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
                    java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).parse(dateString)
                } catch (e: Exception) {
                    java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(dateString)
                }
                java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(date!!)
            }
        }
    } catch (e: Exception) {
        dateString
    }
}

@Composable
private fun ExpandableFab(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCreateTask: () -> Unit,
    onCreateBoard: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Opciones expandidas
        if (expanded) {
            // Opción: Crear Tablero
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Crear Tablero",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                SmallFloatingActionButton(
                    onClick = onCreateBoard,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.Default.Dashboard, "Crear tablero")
                }
            }
            
            // Opción: Crear Tarea
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Crear Tarea",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                SmallFloatingActionButton(
                    onClick = onCreateTask,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(Icons.Default.CheckCircle, "Crear tarea")
                }
            }
        }
        
        // FAB principal
        FloatingActionButton(
            onClick = { onExpandedChange(!expanded) }
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = if (expanded) "Cerrar" else "Crear"
            )
        }
    }
}
