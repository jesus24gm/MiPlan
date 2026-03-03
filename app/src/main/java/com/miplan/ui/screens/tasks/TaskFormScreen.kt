package com.miplan.ui.screens.tasks

import android.Manifest
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.miplan.data.remote.UnsplashService
import com.miplan.domain.model.Task
import com.miplan.domain.model.TaskPriority
import com.miplan.domain.model.UiState
import com.miplan.domain.model.CollaboratorRole
import com.miplan.ui.components.ImageSourceDialog
import com.miplan.ui.components.SnackbarManager
import com.miplan.ui.components.UnsplashSearchDialog
import com.miplan.ui.components.AddCollaboratorDialog
import com.miplan.ui.components.DateTimePickerDialog
import com.miplan.utils.CloudinaryManager
import com.miplan.utils.rememberImagePickerLaunchers
import com.miplan.viewmodel.TaskViewModel
import com.miplan.viewmodel.CollaboratorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla unificada para crear y editar tareas con soporte de imágenes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    taskId: Int? = null,
    onNavigateBack: () -> Unit,
    onTaskSaved: () -> Unit,
    taskViewModel: TaskViewModel = hiltViewModel(),
    kanbanViewModel: com.miplan.viewmodel.KanbanViewModel = hiltViewModel(),
    collaboratorViewModel: CollaboratorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val cloudinaryManager = remember { CloudinaryManager(context) }
    val unsplashService = remember { UnsplashService() }
    val isEditMode = taskId != null
    
    // Estados del formulario
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var dueDate by remember { mutableStateOf<String?>(null) }
    var dueTime by remember { mutableStateOf<String?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var showDateTimeDialog by remember { mutableStateOf(false) }
    var showPriorityMenu by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }
    var selectedBoardId by remember { mutableStateOf<Int?>(null) }
    var showBoardSelector by remember { mutableStateOf(false) }
    
    // Estados para imágenes
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showUnsplashDialog by remember { mutableStateOf(false) }
    var isUploadingImage by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Estados para colaboradores
    var showAddCollaboratorDialog by remember { mutableStateOf(false) }
    var selectedCollaboratorEmail by remember { mutableStateOf("") }
    var selectedCollaboratorRole by remember { mutableStateOf(CollaboratorRole.VIEWER) }
    val pendingCollaborators = remember { mutableStateListOf<Pair<String, CollaboratorRole>>() }
    
    val createTaskState by taskViewModel.createTaskState.collectAsState()
    val updateTaskState by taskViewModel.updateTaskState.collectAsState()
    val taskDetailState by taskViewModel.taskDetailState.collectAsState()
    val boardsState by kanbanViewModel.boardsState.collectAsState()
    val searchUserState by collaboratorViewModel.searchUserState.collectAsState()
    val collaboratorsState by collaboratorViewModel.collaboratorsState.collectAsState()
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Cargar tableros
    LaunchedEffect(Unit) {
        kanbanViewModel.loadBoards()
    }
    
    // Image picker launchers
    val imagePickers = rememberImagePickerLaunchers(
        onImageSelected = { uri ->
            selectedImageUri = uri
            scope.launch {
                isUploadingImage = true
                uploadError = null
                
                val result = cloudinaryManager.uploadImage(uri)
                isUploadingImage = false
                
                result.onSuccess { url ->
                    imageUrl = url
                }
                result.onFailure { error ->
                    uploadError = error.message
                }
            }
        }
    )
    
    // Cargar tarea si estamos en modo edición
    LaunchedEffect(taskId) {
        if (taskId != null) {
            taskViewModel.loadTaskById(taskId)
        } else {
            isLoaded = true
        }
    }
    
    // Rellenar campos cuando se carga la tarea
    LaunchedEffect(taskDetailState) {
        if (isEditMode && taskDetailState is UiState.Success && !isLoaded) {
            val task = (taskDetailState as UiState.Success<Task>).data
            title = task.title
            description = task.description ?: ""
            selectedPriority = task.priority
            imageUrl = task.imageUrl
            selectedBoardId = task.boardId
            
            task.dueDate?.let { dateStr ->
                when {
                    dateStr.contains(" ") -> {
                        val parts = dateStr.split(" ")
                        dueDate = parts[0]
                        dueTime = parts.getOrNull(1)
                    }
                    dateStr.contains("T") -> {
                        val parts = dateStr.split("T")
                        dueDate = parts[0]
                        val time = parts.getOrNull(1)
                        if (time != null && time != "00:00:00") {
                            dueTime = time
                        }
                    }
                    else -> {
                        dueDate = dateStr
                    }
                }
            }
            isLoaded = true
        }
    }
    
    // Cargar colaboradores si estamos en modo edición
    LaunchedEffect(taskId) {
        if (taskId != null) {
            collaboratorViewModel.loadCollaborators(taskId)
        }
    }
    
    // Manejar estado de creación/actualización
    LaunchedEffect(createTaskState, updateTaskState) {
        when {
            createTaskState is UiState.Success -> {
                val task = (createTaskState as UiState.Success<Task>).data
                
                // Agregar colaboradores pendientes si los hay
                if (pendingCollaborators.isNotEmpty()) {
                    pendingCollaborators.forEach { (email, role) ->
                        collaboratorViewModel.searchUserByEmail(email)
                        // Esperar un poco para que se busque el usuario
                        delay(500)
                        if (searchUserState is UiState.Success) {
                            val user = (searchUserState as UiState.Success).data
                            if (user != null) {
                                collaboratorViewModel.addCollaborator(task.id, user.email, role)
                            }
                        }
                    }
                }
                
                SnackbarManager.showSuccess(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = SnackbarManager.TaskMessages.created(task.title)
                )
                delay(800) // Esperar a que se vea el Snackbar
                taskViewModel.resetCreateState()
                onTaskSaved()
            }
            updateTaskState is UiState.Success -> {
                val task = (updateTaskState as UiState.Success<Task>).data
                SnackbarManager.showSuccess(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = SnackbarManager.TaskMessages.updated(task.title)
                )
                delay(800) // Esperar a que se vea el Snackbar
                taskViewModel.resetUpdateState()
                onTaskSaved()
            }
            else -> {}
        }
    }
    
    val isLoading = createTaskState is UiState.Loading || 
                    updateTaskState is UiState.Loading ||
                    (isEditMode && taskDetailState is UiState.Loading) ||
                    isUploadingImage
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Tarea" else "Nueva Tarea") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                val finalDueDate = when {
                                    dueDate != null && dueTime != null -> "$dueDate $dueTime"
                                    dueDate != null -> dueDate
                                    else -> null
                                }
                                
                                // Validar que la fecha no sea pasada
                                if (finalDueDate != null) {
                                    try {
                                        val now = Calendar.getInstance()
                                        val taskCalendar = Calendar.getInstance()
                                        val isPastDate = if (dueTime != null) {
                                            // CON HORA: Comparar fecha y hora completa
                                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                            val taskDate = dateFormat.parse(finalDueDate)
                                            taskDate != null && taskDate.before(now.time)
                                        } else {
                                            // SIN HORA: Solo comparar el día (año, mes, día)
                                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                            val taskDate = dateFormat.parse(finalDueDate)
                                            if (taskDate != null) {
                                                taskCalendar.time = taskDate
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
                                        
                                        if (isPastDate) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "No puedes asignar una fecha pasada",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                            return@IconButton
                                        }
                                    } catch (e: Exception) {
                                        // Si hay error al parsear, continuar
                                    }
                                }
                                
                                if (isEditMode) {
                                    val task = (taskDetailState as? UiState.Success<Task>)?.data
                                    taskViewModel.updateTask(
                                        id = taskId!!,
                                        title = title,
                                        description = description.ifBlank { null },
                                        status = task?.status ?: com.miplan.domain.model.TaskStatus.PENDING,
                                        priority = selectedPriority,
                                        dueDate = finalDueDate,
                                        imageUrl = imageUrl,
                                        boardId = selectedBoardId
                                    )
                                } else {
                                    taskViewModel.createTask(
                                        title = title,
                                        description = description.ifBlank { null },
                                        priority = selectedPriority,
                                        dueDate = finalDueDate,
                                        imageUrl = imageUrl,
                                        boardId = selectedBoardId
                                    )
                                }
                            }
                        },
                        enabled = title.isNotBlank() && !isLoading
                    ) {
                        Icon(Icons.Default.Check, "Guardar")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        if (isEditMode && !isLoaded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Título
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título *") },
                    placeholder = { Text("¿Qué necesitas hacer?") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Title, "Título")
                    }
                )
                
                // Sección de Imagen
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Image, "Imagen")
                            Text(
                                "Imagen",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        
                        // Vista previa de imagen
                        if (imageUrl != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Vista previa",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            
                            // Botones para cambiar/quitar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showImageSourceDialog = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cambiar")
                                }
                                
                                OutlinedButton(
                                    onClick = { imageUrl = null },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Quitar")
                                }
                            }
                        } else {
                            // Botón para agregar imagen
                            OutlinedButton(
                                onClick = { showImageSourceDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isUploadingImage
                            ) {
                                if (isUploadingImage) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Subiendo...")
                                } else {
                                    Icon(Icons.Default.AddPhotoAlternate, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Agregar Imagen")
                                }
                            }
                            
                            // Advertencia sin conexión
                            if (!com.miplan.utils.NetworkUtils.isNetworkAvailable(context)) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "Sin conexión: No se pueden subir imágenes offline",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Error de subida
                        if (uploadError != null) {
                            Text(
                                text = "Error: $uploadError",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                
                // Descripción
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    placeholder = { Text("Añade detalles...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    leadingIcon = {
                        Icon(Icons.Default.Description, "Descripción")
                    }
                )
                
                // Prioridad
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showPriorityMenu = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Flag,
                                "Prioridad",
                                tint = getPriorityColor(selectedPriority)
                            )
                            Text("Prioridad: ${getPriorityText(selectedPriority)}")
                        }
                        Icon(Icons.Default.KeyboardArrowDown, "Seleccionar")
                    }
                }
                
                DropdownMenu(
                    expanded = showPriorityMenu,
                    onDismissRequest = { showPriorityMenu = false }
                ) {
                    TaskPriority.values().forEach { priority ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Star,
                                        null,
                                        tint = getPriorityColor(priority)
                                    )
                                    Text(getPriorityText(priority))
                                }
                            },
                            onClick = {
                                selectedPriority = priority
                                showPriorityMenu = false
                            }
                        )
                    }
                }
                
                // Selector de tablero
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showBoardSelector = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Dashboard, "Tablero")
                            Text(
                                if (selectedBoardId != null) {
                                    val boards = (boardsState as? UiState.Success)?.data
                                    val boardName = boards?.find { it.id == selectedBoardId }?.name ?: "Tablero seleccionado"
                                    "Tablero: $boardName"
                                } else {
                                    "Sin tablero asignado"
                                }
                            )
                        }
                        if (selectedBoardId != null) {
                            IconButton(
                                onClick = { selectedBoardId = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, "Quitar tablero", modifier = Modifier.size(18.dp))
                            }
                        } else {
                            Icon(Icons.Default.Add, "Agregar tablero")
                        }
                    }
                }
                
                // Fecha y hora límite
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showDateTimeDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.DateRange, "Fecha y hora")
                                Text(
                                    text = if (dueDate != null) {
                                        "Fecha: ${formatDateForDisplay(dueDate!!)}"
                                    } else {
                                        "Sin fecha límite"
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            if (dueTime != null) {
                                Row(
                                    modifier = Modifier.padding(start = 32.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.AccessTime, 
                                        "Hora",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Hora: ${formatTimeForDisplay(dueTime!!)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        if (dueDate != null) {
                            IconButton(
                                onClick = { 
                                    dueDate = null
                                    dueTime = null
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, "Quitar", modifier = Modifier.size(18.dp))
                            }
                        } else {
                            Icon(Icons.Default.Add, "Agregar")
                        }
                    }
                }
                
                // Colaboradores
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.People, "Colaboradores")
                                Text(
                                    "Colaboradores",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            
                            IconButton(
                                onClick = { showAddCollaboratorDialog = true }
                            ) {
                                Icon(Icons.Default.Add, "Agregar colaborador")
                            }
                        }
                        
                        // Mostrar colaboradores existentes (modo edición)
                        if (isEditMode && collaboratorsState is UiState.Success) {
                            val collaborators = (collaboratorsState as UiState.Success).data
                            if (collaborators.isNotEmpty()) {
                                collaborators.forEach { collaborator ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = collaborator.userName,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = collaborator.userEmail,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                        AssistChip(
                                            onClick = {},
                                            label = { Text(collaborator.role.name) }
                                        )
                                    }
                                    if (collaborator != collaborators.last()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            } else {
                                Text(
                                    text = "Sin colaboradores",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        
                        // Mostrar colaboradores pendientes (modo creación)
                        if (!isEditMode && pendingCollaborators.isNotEmpty()) {
                            pendingCollaborators.forEach { (email, role) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = email,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text(role.name) }
                                        )
                                        IconButton(
                                            onClick = { pendingCollaborators.remove(email to role) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                "Quitar",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                                if (pendingCollaborators.indexOf(email to role) != pendingCollaborators.lastIndex) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                        
                        if (!isEditMode && pendingCollaborators.isEmpty()) {
                            Text(
                                text = "Sin colaboradores. Agrega colaboradores para trabajar en equipo.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                
                // Estado de carga
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Error
                val errorState = if (isEditMode) updateTaskState else createTaskState
                if (errorState is UiState.Error) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = errorState.message,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Diálogo de selección de fuente de imagen
    if (showImageSourceDialog) {
        ImageSourceDialog(
            onDismiss = { showImageSourceDialog = false },
            onGalleryClick = {
                showImageSourceDialog = false
                imagePickers.galleryLauncher.launch("image/*")
            },
            onCameraClick = {
                showImageSourceDialog = false
                imagePickers.permissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onUnsplashClick = {
                showImageSourceDialog = false
                showUnsplashDialog = true
            }
        )
    }
    
    // Diálogo de búsqueda en Unsplash
    if (showUnsplashDialog) {
        UnsplashSearchDialog(
            unsplashService = unsplashService,
            onDismiss = { showUnsplashDialog = false },
            onImageSelected = { url ->
                imageUrl = url
                showUnsplashDialog = false
            }
        )
    }
    
    // Diálogo para agregar colaborador
    if (showAddCollaboratorDialog) {
        AddCollaboratorDialog(
            searchUserState = searchUserState,
            onDismiss = {
                showAddCollaboratorDialog = false
                collaboratorViewModel.resetSearchUserState()
                selectedCollaboratorEmail = ""
                selectedCollaboratorRole = CollaboratorRole.VIEWER
            },
            onSearch = { email ->
                selectedCollaboratorEmail = email
                collaboratorViewModel.searchUserByEmail(email)
            },
            onAdd = { email, role ->
                if (isEditMode) {
                    // En modo edición, agregar directamente
                    collaboratorViewModel.addCollaborator(taskId!!, email, role)
                } else {
                    // En modo creación, agregar a la lista pendiente
                    if (!pendingCollaborators.any { it.first == email }) {
                        pendingCollaborators.add(email to role)
                    }
                }
                showAddCollaboratorDialog = false
                collaboratorViewModel.resetSearchUserState()
                selectedCollaboratorEmail = ""
                selectedCollaboratorRole = CollaboratorRole.VIEWER
            }
        )
    }
    
    // Diálogo de fecha y hora combinado
    if (showDateTimeDialog) {
        DateTimePickerDialog(
            initialDate = dueDate,
            initialTime = dueTime,
            onDismiss = { showDateTimeDialog = false },
            onConfirm = { date, time ->
                dueDate = date
                dueTime = time
            }
        )
    }
    
    // Selector de tablero
    if (showBoardSelector) {
        val boards = (boardsState as? UiState.Success)?.data ?: emptyList()
        
        AlertDialog(
            onDismissRequest = { showBoardSelector = false },
            title = { Text("Seleccionar tablero") },
            text = {
                if (boards.isEmpty()) {
                    Text("No hay tableros disponibles")
                } else {
                    Column {
                        boards.forEach { board ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                onClick = {
                                    selectedBoardId = board.id
                                    showBoardSelector = false
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = board.name,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        if (board.description != null) {
                                            Text(
                                                text = board.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    if (selectedBoardId == board.id) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Seleccionado",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBoardSelector = false }) {
                    Text("Cerrar")
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
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString)
        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        displayFormat.format(date!!)
    } catch (e: Exception) {
        dateString
    }
}

private fun formatTimeForDisplay(timeString: String): String {
    return try {
        val parts = timeString.split(":")
        "${parts[0]}:${parts[1]}"
    } catch (e: Exception) {
        timeString
    }
}
