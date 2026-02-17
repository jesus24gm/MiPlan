package com.miplan.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miplan.viewmodel.AuthViewModel
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
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
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
                TopAppBar(
                    title = { Text("MiPlan") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToNotifications) {
                            Badge {
                                Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onNavigateToTasks) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva tarea")
                }
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
                
                item {
                    Text(
                        text = "Resumen del día",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                
                // Cards de resumen
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryCard(
                            title = "Tareas Pendientes",
                            value = "0",
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToTasks
                        )
                        
                        SummaryCard(
                            title = "Tableros",
                            value = "0",
                            icon = Icons.Default.Dashboard,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToBoards
                        )
                    }
                }
                
                item {
                    Text(
                        text = "Accesos Rápidos",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                
                item {
                    QuickActionCard(
                        title = "Ver Tareas",
                        description = "Gestiona todas tus tareas",
                        icon = Icons.Default.CheckCircle,
                        onClick = onNavigateToTasks
                    )
                }
                
                item {
                    QuickActionCard(
                        title = "Ver Calendario",
                        description = "Visualiza tus tareas por fecha",
                        icon = Icons.Default.CalendarToday,
                        onClick = onNavigateToCalendar
                    )
                }
                
                item {
                    QuickActionCard(
                        title = "Tableros",
                        description = "Organiza tus proyectos",
                        icon = Icons.Default.Dashboard,
                        onClick = onNavigateToBoards
                    )
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
    onClick: () -> Unit
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
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium
            )
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
fun QuickActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
