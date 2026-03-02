package com.miplan.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.miplan.domain.model.Role
import com.miplan.domain.model.UiState
import com.miplan.domain.model.User
import com.miplan.viewmodel.AdminViewModel

/**
 * Pantalla de administración del sistema
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Usuarios", "Sistema")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administración") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Contenido según tab seleccionado
            when (selectedTab) {
                0 -> UsersTab(viewModel)
                1 -> SystemTab(viewModel)
            }
        }
    }
}

/**
 * Tab de gestión de usuarios
 */
@Composable
private fun UsersTab(viewModel: AdminViewModel) {
    val usersState by viewModel.usersState.collectAsStateWithLifecycle()
    val updateRoleState by viewModel.updateRoleState.collectAsStateWithLifecycle()
    
    var showRoleDialog by remember { mutableStateOf<User?>(null) }
    
    LaunchedEffect(Unit) {
        viewModel.loadAllUsers()
    }
    
    // Mostrar mensaje cuando se actualiza el rol
    LaunchedEffect(updateRoleState) {
        if (updateRoleState is UiState.Success) {
            viewModel.resetUpdateRoleState()
        }
    }
    
    when (usersState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        is UiState.Success -> {
            val users = (usersState as UiState.Success<List<User>>).data
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Total: ${users.size} usuarios",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(users) { user ->
                    UserCard(
                        user = user,
                        onChangeRole = { showRoleDialog = user }
                    )
                }
            }
        }
        
        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = (usersState as UiState.Error).message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { viewModel.loadAllUsers() }) {
                        Text("Reintentar")
                    }
                }
            }
        }
        
        else -> {}
    }
    
    // Diálogo para cambiar rol
    showRoleDialog?.let { user ->
        ChangeRoleDialog(
            user = user,
            onDismiss = { showRoleDialog = null },
            onConfirm = { newRole ->
                viewModel.updateUserRole(user.id, newRole)
                showRoleDialog = null
            }
        )
    }
}

/**
 * Tarjeta de usuario
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UserCard(
    user: User,
    onChangeRole: () -> Unit
) {
    Card(
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Badge de rol
                    AssistChip(
                        onClick = {},
                        label = { Text(user.role.displayName) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (user.role == Role.ADMIN) 
                                    Icons.Default.AdminPanelSettings 
                                else 
                                    Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (user.role == Role.ADMIN)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    
                    // Badge de verificación
                    if (user.isVerified) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Verificado") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        )
                    }
                }
            }
            
            // Botón para cambiar rol
            IconButton(onClick = onChangeRole) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Cambiar rol"
                )
            }
        }
    }
}

/**
 * Diálogo para cambiar el rol de un usuario
 */
@Composable
private fun ChangeRoleDialog(
    user: User,
    onDismiss: () -> Unit,
    onConfirm: (Role) -> Unit
) {
    var selectedRole by remember { mutableStateOf(user.role) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar rol de ${user.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Selecciona el nuevo rol para este usuario:",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Role.values().forEach { role ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedRole == role,
                            onClick = { selectedRole = role }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = role.displayName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = when (role) {
                                    Role.ADMIN -> "Acceso completo al sistema"
                                    Role.USER -> "Acceso estándar"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedRole) },
                enabled = selectedRole != user.role
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Tab de estadísticas del sistema
 */
@Composable
private fun SystemTab(viewModel: AdminViewModel) {
    val statsState by viewModel.statsState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadStats()
    }
    
    when (statsState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        is UiState.Success -> {
            val stats = (statsState as UiState.Success).data
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Estadísticas del Sistema",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatRow(
                                icon = Icons.Default.People,
                                label = "Total Usuarios",
                                value = stats.totalUsers.toString()
                            )
                            StatRow(
                                icon = Icons.Default.PersonAdd,
                                label = "Usuarios Activos",
                                value = stats.activeUsers.toString()
                            )
                        }
                    }
                }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatRow(
                                icon = Icons.Default.Task,
                                label = "Total Tareas",
                                value = stats.totalTasks.toString()
                            )
                            StatRow(
                                icon = Icons.Default.CheckCircle,
                                label = "Tareas Completadas",
                                value = stats.completedTasks.toString()
                            )
                            StatRow(
                                icon = Icons.Default.PendingActions,
                                label = "Tareas Pendientes",
                                value = stats.pendingTasks.toString()
                            )
                        }
                    }
                }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatRow(
                                icon = Icons.Default.Dashboard,
                                label = "Total Tableros",
                                value = stats.totalBoards.toString()
                            )
                        }
                    }
                }
            }
        }
        
        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = (statsState as UiState.Error).message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { viewModel.loadStats() }) {
                        Text("Reintentar")
                    }
                }
            }
        }
        
        else -> {}
    }
}

/**
 * Fila de estadística
 */
@Composable
private fun StatRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

