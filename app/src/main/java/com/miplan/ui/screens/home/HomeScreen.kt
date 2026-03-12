package com.miplan.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miplan.domain.model.TaskStatus
import com.miplan.domain.model.UiState
import com.miplan.viewmodel.AuthViewModel
import com.miplan.viewmodel.TaskViewModel
import com.miplan.viewmodel.NotificationViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla principal (Home/Dashboard)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToBoards: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToCreateTask: () -> Unit = {},
    onNavigateToCreateBoard: () -> Unit = {},
    onNavigateToTaskDetail: (Int) -> Unit = {},
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel(),
    kanbanViewModel: com.miplan.viewmodel.KanbanViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val tasksState by taskViewModel.tasksState.collectAsState()
    val boardsState by kanbanViewModel.boardsState.collectAsState()
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    
    // Estado del FAB expandible
    var fabExpanded by remember { mutableStateOf(false) }
    
    // Cargar tareas, tableros y notificaciones al iniciar
    LaunchedEffect(Unit) {
        taskViewModel.loadTasks()
        notificationViewModel.loadNotifications()
        kanbanViewModel.loadBoards()
    }
    
    // Calcular contador de tareas pendientes
    val pendingTasksCount = when (val state = tasksState) {
        is UiState.Success -> state.data.count { it.status != TaskStatus.COMPLETED }
        else -> 0
    }
    
    // Calcular contador de tableros
    val boardsCount = when (val state = boardsState) {
        is UiState.Success -> state.data.size
        else -> 0
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "MiPlan",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                
                Divider()
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                    label = { Text("Tareas") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToTasks()
                    }
                )
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text("Tableros") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToBoards()
                    }
                )
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    label = { Text("Calendario") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToCalendar()
                    }
                )
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    label = { Text("Notificaciones") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToNotifications()
                    }
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToProfile()
                    }
                )
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                    label = { Text("Administración") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToAdmin()
                    }
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = {
                        authViewModel.logout()
                        onLogout()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                // Degradado morado oscuro
                val gradientBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6B4FA0),  // Morado oscuro
                        Color(0xFF8B6FC7),  // Morado medio
                        Color(0xFF9B7FD8)   // Morado principal
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, 0f)
                )
                
                TopAppBar(
                    title = { Text("MiPlan") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge {
                                        Text(
                                            text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            IconButton(onClick = onNavigateToNotifications) {
                                Icon(
                                    Icons.Default.Notifications, 
                                    contentDescription = "Notificaciones"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    modifier = Modifier.background(gradientBrush)
                )
            },
            floatingActionButton = {
                ExpandableFab(
                    expanded = fabExpanded,
                    onExpandedChange = { fabExpanded = it },
                    onCreateTask = {
                        fabExpanded = false
                        onNavigateToCreateTask()
                    },
                    onCreateBoard = {
                        fabExpanded = false
                        onNavigateToCreateBoard()
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Bienvenido a MiPlan",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                
                // SECCIÓN: ACCESOS RÁPIDOS
                item {
                    Text(
                        text = "Accesos Rápidos",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                
                // Cards de accesos rápidos (3 tarjetas pequeñas)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryCard(
                            title = "Tareas Pendientes",
                            value = pendingTasksCount.toString(),
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToTasks
                        )
                        
                        SummaryCard(
                            title = "Tableros",
                            value = boardsCount.toString(),
                            icon = Icons.Default.Dashboard,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToBoards
                        )
                    }
                }
                
                item {
                    SummaryCard(
                        title = "Calendario",
                        value = "",
                        icon = Icons.Default.CalendarToday,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onNavigateToCalendar,
                        showValue = false
                    )
                }
                
                // SECCIÓN: RESUMEN DEL DÍA
                item {
                    Text(
                        text = "Resumen del Día",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                
                // Lista de tareas pendientes ordenadas
                when (val state = tasksState) {
                    is UiState.Success -> {
                        val pendingTasks = state.data
                            .filter { it.status != TaskStatus.COMPLETED && it.status != TaskStatus.CANCELLED }
                            .sortedWith(
                                compareByDescending<com.miplan.domain.model.Task> { 
                                    // Primero por prioridad (HIGH > MEDIUM > LOW)
                                    when (it.priority) {
                                        com.miplan.domain.model.TaskPriority.HIGH -> 3
                                        com.miplan.domain.model.TaskPriority.MEDIUM -> 2
                                        com.miplan.domain.model.TaskPriority.LOW -> 1
                                    }
                                }.thenBy { task ->
                                    // Luego por proximidad de fecha (más cercanas primero)
                                    task.dueDate ?: "9999-12-31" // Tareas sin fecha al final
                                }
                            )
                        
                        if (pendingTasks.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "¡No tienes tareas pendientes!",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Disfruta tu día",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            items(pendingTasks.size) { index ->
                                TaskSummaryCard(
                                    task = pendingTasks[index],
                                    onTaskClick = { onNavigateToTaskDetail(it.id) },
                                    onCompleteTask = { task ->
                                        taskViewModel.updateTaskStatus(
                                            task.id,
                                            TaskStatus.COMPLETED
                                        )
                                    }
                                )
                            }
                        }
                    }
                    is UiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is UiState.Error -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    text = state.message,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    showValue: Boolean = true
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (showValue) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSummaryCard(
    task: com.miplan.domain.model.Task,
    onTaskClick: (com.miplan.domain.model.Task) -> Unit,
    onCompleteTask: (com.miplan.domain.model.Task) -> Unit
) {
    Card(
        onClick = { onTaskClick(task) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox para marcar como completada
            Checkbox(
                checked = false,
                onCheckedChange = { 
                    if (it) {
                        onCompleteTask(task)
                    }
                }
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Contenido de la tarea
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Título
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Fecha y prioridad
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Prioridad
                    val priorityColor = when (task.priority) {
                        com.miplan.domain.model.TaskPriority.HIGH -> MaterialTheme.colorScheme.error
                        com.miplan.domain.model.TaskPriority.MEDIUM -> MaterialTheme.colorScheme.tertiary
                        com.miplan.domain.model.TaskPriority.LOW -> MaterialTheme.colorScheme.primary
                    }
                    
                    Surface(
                        color = priorityColor.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = task.priority.displayName,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = priorityColor
                        )
                    }
                    
                    // Fecha
                    if (task.dueDate != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatDueDate(task.dueDate),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Icono de navegación
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver detalle",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Formatea la fecha para mostrar de forma amigable
 * Formato de salida: dd/MM/yyyy HH:mm
 */
private fun formatDueDate(dueDate: String): String {
    return try {
        // Manejar diferentes formatos de entrada
        when {
            // Formato ISO: "2026-03-02T19:00:00"
            dueDate.contains("T") -> {
                val parts = dueDate.split("T")
                val datePart = parts[0] // "2026-03-02"
                val timePart = if (parts.size > 1) parts[1].substringBefore(".") else "00:00:00" // "19:00:00"
                
                val dateParts = datePart.split("-")
                val timeParts = timePart.split(":")
                
                if (dateParts.size == 3 && timeParts.size >= 2) {
                    "${dateParts[2]}/${dateParts[1]}/${dateParts[0]} ${timeParts[0]}:${timeParts[1]}"
                } else {
                    dueDate
                }
            }
            // Formato con espacio: "2026-03-02 19:00:00"
            dueDate.contains(" ") -> {
                val parts = dueDate.split(" ")
                val datePart = parts[0] // "2026-03-02"
                val timePart = if (parts.size > 1) parts[1] else "00:00:00" // "19:00:00"
                
                val dateParts = datePart.split("-")
                val timeParts = timePart.split(":")
                
                if (dateParts.size == 3 && timeParts.size >= 2) {
                    "${dateParts[2]}/${dateParts[1]}/${dateParts[0]} ${timeParts[0]}:${timeParts[1]}"
                } else {
                    dueDate
                }
            }
            // Solo fecha: "2026-03-02"
            else -> {
                val dateParts = dueDate.split("-")
                if (dateParts.size == 3) {
                    "${dateParts[2]}/${dateParts[1]}/${dateParts[0]} 00:00"
                } else {
                    dueDate
                }
            }
        }
    } catch (e: Exception) {
        dueDate
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
