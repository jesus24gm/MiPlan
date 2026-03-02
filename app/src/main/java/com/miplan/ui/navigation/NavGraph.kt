package com.miplan.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.miplan.ui.screens.auth.LoginScreen
import com.miplan.ui.screens.auth.RegisterScreen
import com.miplan.ui.screens.home.HomeScreen
import com.miplan.ui.screens.calendar.CalendarScreen
import com.miplan.ui.screens.tasks.CreateTaskScreen
import com.miplan.ui.screens.tasks.EditTaskScreen
import com.miplan.ui.screens.tasks.TaskDetailScreen
import com.miplan.ui.screens.tasks.TaskListScreen
import com.miplan.ui.screens.kanban.BoardListScreen
import com.miplan.ui.screens.kanban.BoardDetailScreen
import com.miplan.ui.screens.settings.NotificationSettingsScreen
import com.miplan.ui.screens.profile.ProfileScreen
import com.miplan.ui.screens.profile.EditProfileScreen
import com.miplan.ui.screens.profile.ChangePasswordScreen
import com.miplan.ui.screens.admin.AdminScreen
import com.miplan.ui.screens.onboarding.OnboardingScreen
import com.miplan.data.preferences.NotificationPreferences
import com.miplan.data.local.OnboardingPreferences
import com.miplan.viewmodel.AuthViewModel
import com.miplan.viewmodel.KanbanViewModel
import androidx.compose.ui.platform.LocalContext

/**
 * Grafo de navegación principal de la aplicación
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel(),
    initialTaskId: Int? = null
) {
    val context = LocalContext.current
    val onboardingPreferences = remember { OnboardingPreferences(context) }
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val hasCompletedOnboarding = remember { onboardingPreferences.isOnboardingCompleted() }
    
    // Determinar destino inicial basado en autenticación, onboarding y notificación
    val startDestination = when {
        !isAuthenticated -> Screen.Login.route
        !hasCompletedOnboarding -> Screen.Onboarding.route
        initialTaskId != null -> Screen.TaskDetail.createRoute(initialTaskId)
        else -> Screen.Home.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ==================== AUTH ====================
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        // ==================== ONBOARDING ====================
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    onboardingPreferences.setOnboardingCompleted()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        // ==================== HOME ====================
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTasks = {
                    navController.navigate(Screen.TaskList.route)
                },
                onNavigateToBoards = {
                    navController.navigate(Screen.BoardList.route)
                },
                onNavigateToCalendar = {
                    navController.navigate(Screen.Calendar.route)
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.NotificationSettings.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.AdminDashboard.route)
                },
                onNavigateToCreateTask = {
                    navController.navigate(Screen.CreateTask.route)
                },
                onNavigateToCreateBoard = {
                    navController.navigate(Screen.BoardList.createRoute(showCreateDialog = true))
                },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        // ==================== TASKS ====================
        composable(Screen.TaskList.route) {
            TaskListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onNavigateToCreateTask = {
                    navController.navigate(Screen.CreateTask.route)
                },
                onNavigateToCreateBoard = {
                    navController.navigate(Screen.CreateBoard.route)
                }
            )
        }
        
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
            TaskDetailScreen(
                taskId = taskId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.EditTask.createRoute(id))
                }
            )
        }
        
        composable(Screen.CreateTask.route) {
            CreateTaskScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTaskCreated = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
            EditTaskScreen(
                taskId = taskId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTaskUpdated = {
                    navController.popBackStack()
                }
            )
        }
        
        // ==================== BOARDS ====================
        composable(
            route = Screen.BoardList.route,
            arguments = listOf(
                navArgument("showCreateDialog") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val showCreateDialog = backStackEntry.arguments?.getBoolean("showCreateDialog") ?: false
            
            BoardListScreen(
                onBoardClick = { board ->
                    navController.navigate(Screen.BoardDetail.createRoute(board.id))
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                initialShowCreateDialog = showCreateDialog
            )
        }
        
        composable(
            route = Screen.BoardDetail.route,
            arguments = listOf(navArgument("boardId") { type = NavType.IntType })
        ) { backStackEntry ->
            val boardId = backStackEntry.arguments?.getInt("boardId") ?: return@composable
            val kanbanViewModel: KanbanViewModel = hiltViewModel()
            val boardsState by kanbanViewModel.boardsState.collectAsState()
            
            // Cargar tableros si no están cargados
            LaunchedEffect(Unit) {
                if (boardsState !is com.miplan.domain.model.UiState.Success) {
                    kanbanViewModel.loadBoards()
                }
            }
            
            // Buscar el tablero en el estado
            when (val state = boardsState) {
                is com.miplan.domain.model.UiState.Success -> {
                    val board = state.data.find { it.id == boardId }
                    if (board != null) {
                        BoardDetailScreen(
                            board = board,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToTaskDetail = { taskId ->
                                navController.navigate(Screen.TaskDetail.createRoute(taskId))
                            }
                        )
                    } else {
                        // Tablero no encontrado
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text("Tablero no encontrado")
                                Button(
                                    onClick = { navController.popBackStack() }
                                ) {
                                    Text("Volver")
                                }
                            }
                        }
                    }
                }
                is com.miplan.domain.model.UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is com.miplan.domain.model.UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Error: ${state.message}")
                            Button(
                                onClick = { kanbanViewModel.loadBoards() }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                else -> {
                    // Estado Idle
                }
            }
        }
        
        // Crear tablero
        composable(Screen.CreateBoard.route) {
            com.miplan.ui.screens.kanban.CreateBoardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // ==================== CALENDAR ====================
        composable(Screen.Calendar.route) {
            CalendarScreen(
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onNavigateToCreateTask = {
                    navController.navigate(Screen.CreateTask.route)
                },
                onNavigateToCreateBoard = {
                    navController.navigate(Screen.CreateBoard.route)
                }
            )
        }
        
        // ==================== NOTIFICATION SETTINGS ====================
        composable(Screen.NotificationSettings.route) {
            val context = LocalContext.current
            val notificationPreferences = remember { NotificationPreferences(context) }
            
            NotificationSettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                notificationPreferences = notificationPreferences
            )
        }
        
        // ==================== PROFILE ====================
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToChangePassword = {
                    navController.navigate(Screen.ChangePassword.route)
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.NotificationSettings.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // ==================== ADMIN ====================
        composable(Screen.AdminDashboard.route) {
            AdminScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
