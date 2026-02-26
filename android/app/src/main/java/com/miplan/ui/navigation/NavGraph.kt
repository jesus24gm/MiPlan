package com.miplan.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.miplan.ui.screens.tasks.TaskListScreen
import com.miplan.ui.screens.settings.NotificationSettingsScreen
import com.miplan.viewmodel.AuthViewModel

/**
 * Grafo de navegación principal de la aplicación
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    
    val startDestination = if (isAuthenticated) {
        Screen.Home.route
    } else {
        Screen.Login.route
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
                    navController.navigate(Screen.Notifications.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.AdminDashboard.route)
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
                }
            )
        }
        
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) {
            // TaskDetailScreen - Se implementará después
        }
        
        composable(Screen.CreateTask.route) {
            // CreateTaskScreen - Se implementará después
        }
        
        // ==================== BOARDS ====================
        composable(Screen.BoardList.route) {
            // BoardListScreen - Se implementará después
        }
        
        composable(
            route = Screen.BoardDetail.route,
            arguments = listOf(navArgument("boardId") { type = NavType.IntType })
        ) {
            // BoardDetailScreen - Se implementará después
        }
        
        // ==================== CALENDAR ====================
        composable(Screen.Calendar.route) {
            // CalendarScreen - Se implementará después
        }
        
        // ==================== NOTIFICATIONS ====================
        composable(Screen.Notifications.route) {
            // NotificationListScreen - Se implementará después
            // Por ahora, redirigir a configuración de notificaciones
            NotificationSettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.NotificationSettings.route) {
            NotificationSettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // ==================== PROFILE ====================
        composable(Screen.Profile.route) {
            // ProfileScreen - Se implementará después
        }
        
        // ==================== ADMIN ====================
        composable(Screen.AdminDashboard.route) {
            // AdminDashboardScreen - Se implementará después
        }
    }
}
