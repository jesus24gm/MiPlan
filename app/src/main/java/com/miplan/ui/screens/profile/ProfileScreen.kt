package com.miplan.ui.screens.profile

import android.Manifest
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.miplan.domain.model.TaskStatus
import com.miplan.domain.model.UiState
import com.miplan.ui.components.ImageSourceDialog
import com.miplan.ui.components.SnackbarManager
import com.miplan.ui.components.UnsplashSearchDialog
import com.miplan.data.remote.UnsplashService
import com.miplan.utils.CloudinaryManager
import com.miplan.utils.rememberImagePickerLaunchers
import com.miplan.viewmodel.AuthViewModel
import com.miplan.viewmodel.KanbanViewModel
import com.miplan.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de perfil del usuario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel(),
    kanbanViewModel: KanbanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val cloudinaryManager = remember { CloudinaryManager(context) }
    val unsplashService = remember { UnsplashService() }
    
    val user by authViewModel.currentUser.collectAsState()
    val tasksState by taskViewModel.tasksState.collectAsState()
    val boardsState by kanbanViewModel.boardsState.collectAsState()
    val deleteAccountState by authViewModel.deleteAccountState.collectAsState()
    val updateAvatarState by authViewModel.updateAvatarState.collectAsState()
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showUnsplashDialog by remember { mutableStateOf(false) }
    var isUploadingAvatar by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Image picker launchers
    val imagePickers = rememberImagePickerLaunchers(
        onImageSelected = { uri ->
            selectedImageUri = uri
            isUploadingAvatar = true
            scope.launch {
                val result = cloudinaryManager.uploadImage(uri)
                result.fold(
                    onSuccess = { imageUrl ->
                        authViewModel.updateAvatar(imageUrl)
                    },
                    onFailure = { error ->
                        SnackbarManager.showError(
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            message = error.message ?: "Error al subir la imagen"
                        )
                        isUploadingAvatar = false
                    }
                )
            }
        }
    )
    
    // Cargar datos
    LaunchedEffect(Unit) {
        taskViewModel.loadTasks()
        kanbanViewModel.loadBoards()
    }
    
    // Manejar actualización de avatar
    LaunchedEffect(updateAvatarState) {
        when (updateAvatarState) {
            is UiState.Success -> {
                SnackbarManager.showSuccess(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = "Foto de perfil actualizada"
                )
                isUploadingAvatar = false
                authViewModel.resetUpdateAvatarState()
            }
            is UiState.Error -> {
                SnackbarManager.showError(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = (updateAvatarState as UiState.Error).message
                )
                isUploadingAvatar = false
                authViewModel.resetUpdateAvatarState()
            }
            else -> {}
        }
    }
    
    // Manejar eliminación de cuenta
    LaunchedEffect(deleteAccountState) {
        when (deleteAccountState) {
            is UiState.Success -> {
                SnackbarManager.showSuccess(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = "Cuenta eliminada correctamente"
                )
                authViewModel.resetDeleteAccountState()
                onLogout()
            }
            is UiState.Error -> {
                SnackbarManager.showError(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = (deleteAccountState as UiState.Error).message
                )
                authViewModel.resetDeleteAccountState()
            }
            else -> {}
        }
    }
    
    // Calcular estadísticas
    val totalTasks = when (val state = tasksState) {
        is UiState.Success -> state.data.size
        else -> 0
    }
    
    val completedTasks = when (val state = tasksState) {
        is UiState.Success -> state.data.count { it.status == TaskStatus.COMPLETED }
        else -> 0
    }
    
    val totalBoards = when (val state = boardsState) {
        is UiState.Success -> state.data.size
        else -> 0
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToEditProfile) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar perfil")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
            // Header con avatar y nombre
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable { showImageSourceDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (user?.avatarUrl != null) {
                                val context = LocalContext.current
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(user?.avatarUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = user?.name?.firstOrNull()?.uppercase() ?: "U",
                                    style = MaterialTheme.typography.displayMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        // Indicador de carga
                        if (isUploadingAvatar) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        
                        // Botón de cámara
                        if (!isUploadingAvatar) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(32.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondary,
                                shadowElevation = 4.dp
                            ) {
                                IconButton(
                                    onClick = { showImageSourceDialog = true },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        contentDescription = "Cambiar foto",
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Nombre
                    Text(
                        text = user?.name ?: "Usuario",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    // Email
                    Text(
                        text = user?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    
                    // Fecha de registro
                    user?.createdAt?.let { createdAt ->
                        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("es"))
                        Text(
                            text = "Miembro desde ${dateFormat.format(Date())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            // Estadísticas
            Text(
                text = "Estadísticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total de tareas
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Task,
                    title = "Tareas",
                    value = totalTasks.toString(),
                    color = MaterialTheme.colorScheme.tertiary
                )
                
                // Tareas completadas
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CheckCircle,
                    title = "Completadas",
                    value = completedTasks.toString(),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total de tableros
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Dashboard,
                    title = "Tableros",
                    value = totalBoards.toString(),
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Tasa de completado
                val completionRate = if (totalTasks > 0) {
                    (completedTasks * 100) / totalTasks
                } else 0
                
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.TrendingUp,
                    title = "Completado",
                    value = "$completionRate%",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            
            // Configuración de cuenta
            Text(
                text = "Configuración",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column {
                    SettingItem(
                        icon = Icons.Default.Person,
                        title = "Editar perfil",
                        subtitle = "Cambiar nombre y datos personales",
                        onClick = onNavigateToEditProfile
                    )
                    
                    Divider()
                    
                    SettingItem(
                        icon = Icons.Default.Lock,
                        title = "Cambiar contraseña",
                        subtitle = "Actualizar tu contraseña de acceso",
                        onClick = onNavigateToChangePassword
                    )
                    
                    Divider()
                    
                    SettingItem(
                        icon = Icons.Default.Notifications,
                        title = "Notificaciones",
                        subtitle = "Configurar preferencias de notificaciones",
                        onClick = onNavigateToNotifications
                    )
                }
            }
            
            // Acciones
            Text(
                text = "Cuenta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column {
                    SettingItem(
                        icon = Icons.Default.Logout,
                        title = "Cerrar sesión",
                        subtitle = "Salir de tu cuenta",
                        onClick = { showLogoutDialog = true },
                        iconTint = MaterialTheme.colorScheme.error
                    )
                    
                    Divider()
                    
                    SettingItem(
                        icon = Icons.Default.DeleteForever,
                        title = "Eliminar cuenta",
                        subtitle = "Eliminar permanentemente tu cuenta y datos",
                        onClick = { showDeleteAccountDialog = true },
                        iconTint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Diálogo de confirmación de cierre de sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout()
                        onLogout()
                    }
                ) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de confirmación de eliminación de cuenta
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Eliminar cuenta") },
            text = { 
                Text("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer y perderás todos tus datos.") 
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        authViewModel.deleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    enabled = deleteAccountState !is UiState.Loading
                ) {
                    if (deleteAccountState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onError,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
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
                isUploadingAvatar = true
                authViewModel.updateAvatar(url)
                showUnsplashDialog = false
            }
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
