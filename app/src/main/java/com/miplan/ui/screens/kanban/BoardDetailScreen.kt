package com.miplan.ui.screens.kanban

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.miplan.domain.model.Board
import com.miplan.domain.model.Card
import com.miplan.domain.model.Column
import com.miplan.domain.model.UiState
import com.miplan.ui.components.SnackbarManager
import com.miplan.viewmodel.KanbanViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla de detalle de un tablero Kanban
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardDetailScreen(
    board: Board,
    onNavigateBack: () -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit = {},
    viewModel: KanbanViewModel = hiltViewModel()
) {
    val columnsState by viewModel.columnsState.collectAsStateWithLifecycle()
    var showCreateColumnDialog by remember { mutableStateOf(false) }
    var showCreateCardDialog by remember { mutableStateOf<Column?>(null) }
    var showCardDetail by remember { mutableStateOf(false) }
    var showBoardMenu by remember { mutableStateOf(false) }
    var showEditBoardDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showCreateTaskDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Tablero", "Tareas")
    
    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(board.id) {
        viewModel.selectBoard(board)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = board.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showBoardMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Más opciones"
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showBoardMenu,
                            onDismissRequest = { showBoardMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Editar tablero") },
                                onClick = {
                                    showBoardMenu = false
                                    showEditBoardDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar tablero") },
                                onClick = {
                                    showBoardMenu = false
                                    showDeleteConfirmDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = parseColor(board.color),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    if (selectedTab == 0) {
                        showCreateColumnDialog = true
                    } else {
                        showCreateTaskDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (selectedTab == 0) "Agregar columna" else "Agregar tarea"
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Pestañas
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = parseColor(board.color),
                contentColor = Color.White
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Contenido según pestaña seleccionada
            when (selectedTab) {
                0 -> {
                    // Pestaña de Tablero (Kanban)
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (val state = columnsState) {
                            is UiState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            
                            is UiState.Success -> {
                                if (state.data.isEmpty()) {
                                    EmptyColumnsState(
                                        onCreateClick = { showCreateColumnDialog = true },
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                } else {
                                    ColumnsRow(
                                        columns = state.data,
                                        onCardClick = { card -> 
                                            viewModel.setSelectedCard(card)
                                            showCardDetail = true
                                        },
                                        onAddCardClick = { column -> showCreateCardDialog = column },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            
                            is UiState.Error -> {
                                ErrorState(
                                    message = state.message,
                                    onRetry = { viewModel.loadColumns(board.id) },
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            
                            else -> {
                                // Estado Idle - no hacer nada
                            }
                        }
                    }
                }
                1 -> {
                    // Pestaña de Tareas
                    BoardTasksTab(
                        boardId = board.id,
                        viewModel = viewModel,
                        onNavigateToTaskDetail = onNavigateToTaskDetail
                    )
                }
            }
        }
    }
    
    if (showCreateColumnDialog) {
        CreateColumnDialog(
            onDismiss = { showCreateColumnDialog = false },
            onConfirm = { title ->
                viewModel.createColumn(board.id, title)
                showCreateColumnDialog = false
            }
        )
    }
    
    showCreateCardDialog?.let { column ->
        CreateCardDialog(
            columnTitle = column.title,
            onDismiss = { showCreateCardDialog = null },
            onConfirm = { title, description, dueDate ->
                viewModel.createCard(column.id, title, description, dueDate, taskId = null, boardId = board.id)
                showCreateCardDialog = null
                SnackbarManager.showSuccess(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = SnackbarManager.CardMessages.created(title)
                )
            }
        )
    }
    
    if (showCardDetail) {
        val selectedCard by viewModel.selectedCard.collectAsStateWithLifecycle()
        selectedCard?.let { card ->
            CardDetailSheet(
                card = card,
                boardId = board.id,
                onDismiss = { 
                    showCardDetail = false
                    viewModel.clearSelectedCard()
                },
                viewModel = viewModel,
                snackbarHostState = snackbarHostState
            )
        }
    }
    
    // Diálogo para editar tablero
    if (showEditBoardDialog) {
        EditBoardDialog(
            board = board,
            onDismiss = { showEditBoardDialog = false },
            onConfirm = { name, description, color ->
                viewModel.updateBoard(board.id, name, description, color)
                showEditBoardDialog = false
            }
        )
    }
    
    // Diálogo de confirmación para eliminar tablero
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Eliminar tablero") },
            text = { Text("¿Estás seguro de que deseas eliminar este tablero? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBoard(board.id)
                        showDeleteConfirmDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo para crear tarea rápida
    if (showCreateTaskDialog) {
        val taskViewModel: com.miplan.viewmodel.TaskViewModel = hiltViewModel()
        
        QuickCreateTaskDialog(
            boardId = board.id,
            onDismiss = { showCreateTaskDialog = false },
            onConfirm = { title, priority, dueDate ->
                taskViewModel.createTask(
                    title = title,
                    description = null,
                    priority = priority,
                    dueDate = dueDate,
                    imageUrl = null,
                    boardId = board.id
                )
                showCreateTaskDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickCreateTaskDialog(
    boardId: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, com.miplan.domain.model.TaskPriority, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(com.miplan.domain.model.TaskPriority.MEDIUM) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Tarea Rápida") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Prioridad:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    com.miplan.domain.model.TaskPriority.values().forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.displayName) }
                        )
                    }
                }
                
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(selectedDate?.let { "Fecha: ${it.substringBefore("T")}" } ?: "Agregar fecha")
                }
                
                if (selectedDate != null) {
                    TextButton(
                        onClick = { selectedDate = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Quitar fecha")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), priority, selectedDate)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Crear")
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
                            // Usar mediodía para evitar problemas de zona horaria
                            val date = java.time.LocalDateTime.ofInstant(
                                instant,
                                java.time.ZoneId.systemDefault()
                            ).withHour(12).withMinute(0).withSecond(0)
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

@Composable
private fun ColumnsRow(
    columns: List<Column>,
    onCardClick: (Card) -> Unit,
    onAddCardClick: (Column) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(columns) { column ->
            ColumnItem(
                column = column,
                onCardClick = onCardClick,
                onAddCardClick = { onAddCardClick(column) }
            )
        }
    }
}

@Composable
private fun ColumnItem(
    column: Column,
    onCardClick: (Card) -> Unit,
    onAddCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(300.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Header de la columna
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = column.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${column.cards.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Lista de tarjetas
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(column.cards) { card ->
                    CardItem(
                        card = card,
                        onClick = { onCardClick(card) }
                    )
                }
            }
            
            // Botón para agregar tarjeta
            OutlinedButton(
                onClick = onAddCardClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar tarjeta")
            }
        }
    }
}

@Composable
private fun CardItem(
    card: Card,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Calcular color de fondo según fecha límite
    val backgroundColor = if (card.dueDate != null) {
        val daysUntilDue = calculateDaysUntilDue(card.dueDate)
        when {
            daysUntilDue < 0 -> MaterialTheme.colorScheme.errorContainer
            daysUntilDue <= 1 -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
            daysUntilDue <= 3 -> androidx.compose.ui.graphics.Color(0xFFFFE0B2) // Naranja claro
            else -> MaterialTheme.colorScheme.surface
        }
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = card.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            if (card.description != null) {
                Text(
                    text = card.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Indicadores de checklist, adjuntos, etc.
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (card.checklists.isNotEmpty()) {
                    val totalItems = card.checklists.sumOf { it.items.size }
                    val completedItems = card.checklists.sumOf { checklist ->
                        checklist.items.count { it.isCompleted }
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (completedItems == totalItems && totalItems > 0) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            }
                        )
                        Text(
                            text = "$completedItems/$totalItems",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                
                if (card.attachments.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "${card.attachments.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Indicador de fecha límite
                if (card.dueDate != null) {
                    val daysUntilDue = calculateDaysUntilDue(card.dueDate)
                    val dueDateColor = when {
                        daysUntilDue < 0 -> MaterialTheme.colorScheme.error
                        daysUntilDue <= 1 -> MaterialTheme.colorScheme.error
                        daysUntilDue <= 3 -> Color(0xFFFF9800)
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                    val dueDateIcon = if (daysUntilDue < 0) Icons.Default.Warning else Icons.Default.CalendarToday
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = dueDateIcon,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = dueDateColor
                        )
                        Text(
                            text = formatDueDate(card.dueDate),
                            style = MaterialTheme.typography.labelSmall,
                            color = dueDateColor
                        )
                    }
                }
            }
        }
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
        android.util.Log.e("BoardDetailScreen", "Error parsing date: $dueDate", e)
        999 // Valor por defecto para fechas inválidas
    }
}

private fun formatDueDate(dueDate: String): String {
    return try {
        // Normalizar formato: reemplazar espacio por T si es necesario
        val normalized = dueDate.replace(" ", "T").substringBefore(".")
        val date = java.time.LocalDateTime.parse(normalized)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM")
        date.format(formatter)
    } catch (e: Exception) {
        android.util.Log.e("BoardDetailScreen", "Error formatting date: $dueDate", e)
        dueDate.replace(" ", "T").substringBefore("T")
    }
}

@Composable
private fun EmptyColumnsState(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ViewColumn,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        
        Text(
            text = "No hay columnas",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        
        Text(
            text = "Crea columnas para organizar tus tarjetas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        
        Button(
            onClick = onCreateClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Crear Columna")
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

@Composable
private fun BoardTasksTab(
    boardId: Int,
    viewModel: KanbanViewModel,
    onNavigateToTaskDetail: (Int) -> Unit = {},
    taskViewModel: com.miplan.viewmodel.TaskViewModel = hiltViewModel()
) {
    val tasksState by taskViewModel.boardTasksState.collectAsStateWithLifecycle()
    val createTaskState by taskViewModel.createTaskState.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("ALL") }
    val filters = listOf("ALL" to "Todas", "PENDING" to "Pendientes", "IN_PROGRESS" to "En progreso", "COMPLETED" to "Completadas")
    
    LaunchedEffect(boardId) {
        taskViewModel.loadTasksByBoard(boardId)
    }
    
    // Recargar cuando se crea una tarea
    LaunchedEffect(createTaskState) {
        if (createTaskState is UiState.Success) {
            taskViewModel.loadTasksByBoard(boardId)
            taskViewModel.resetCreateState()
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Filtros
        ScrollableTabRow(
            selectedTabIndex = filters.indexOfFirst { it.first == selectedFilter },
            containerColor = MaterialTheme.colorScheme.surface,
            edgePadding = 16.dp
        ) {
            filters.forEach { (value, label) ->
                Tab(
                    selected = selectedFilter == value,
                    onClick = { selectedFilter = value },
                    text = { Text(label) }
                )
            }
        }
        
        // Lista de tareas
        when (val state = tasksState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is UiState.Success -> {
                val filteredTasks = when (selectedFilter) {
                    "ALL" -> state.data
                    else -> state.data.filter { it.status.name == selectedFilter }
                }
                
                if (filteredTasks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Task,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                            Text(
                                text = "No hay tareas",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredTasks, key = { it.id }) { task ->
                            BoardTaskItem(
                                task = task,
                                onToggleComplete = {
                                    val newStatus = if (task.status == com.miplan.domain.model.TaskStatus.COMPLETED) {
                                        com.miplan.domain.model.TaskStatus.PENDING
                                    } else {
                                        com.miplan.domain.model.TaskStatus.COMPLETED
                                    }
                                    taskViewModel.updateTaskStatus(task.id, newStatus)
                                    // Recargar inmediatamente después de actualizar
                                    taskViewModel.loadTasksByBoard(boardId)
                                },
                                onClick = { onNavigateToTaskDetail(task.id) }
                            )
                        }
                    }
                }
            }
            
            is UiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = { taskViewModel.loadTasksByBoard(boardId) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoardTaskItem(
    task: com.miplan.domain.model.Task,
    onToggleComplete: () -> Unit,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.status == com.miplan.domain.model.TaskStatus.COMPLETED,
                    onCheckedChange = { onToggleComplete() }
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (task.description != null) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        // Prioridad
                        AssistChip(
                            onClick = {},
                            label = { Text(task.priority.displayName) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = when (task.priority) {
                                    com.miplan.domain.model.TaskPriority.HIGH -> MaterialTheme.colorScheme.errorContainer
                                    com.miplan.domain.model.TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primaryContainer
                                    com.miplan.domain.model.TaskPriority.LOW -> MaterialTheme.colorScheme.tertiaryContainer
                                }
                            )
                        )
                        
                        // Fecha
                        if (task.dueDate != null) {
                            AssistChip(
                                onClick = {},
                                label = { 
                                    Text(
                                        task.dueDate.substringBefore("T"),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

