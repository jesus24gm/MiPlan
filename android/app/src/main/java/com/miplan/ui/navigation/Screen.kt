package com.miplan.ui.navigation

/**
 * Sealed class que define todas las rutas de navegación
 */
sealed class Screen(val route: String) {
    // Auth
    object Login : Screen("login")
    object Register : Screen("register")
    object VerifyEmail : Screen("verify_email/{token}") {
        fun createRoute(token: String) = "verify_email/$token"
    }
    
    // Main
    object Home : Screen("home")
    
    // Tasks
    object TaskList : Screen("tasks")
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: Int) = "task_detail/$taskId"
    }
    object CreateTask : Screen("create_task")
    object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: Int) = "edit_task/$taskId"
    }
    
    // Boards
    object BoardList : Screen("boards")
    object BoardDetail : Screen("board_detail/{boardId}") {
        fun createRoute(boardId: Int) = "board_detail/$boardId"
    }
    object CreateBoard : Screen("create_board")
    object EditBoard : Screen("edit_board/{boardId}") {
        fun createRoute(boardId: Int) = "edit_board/$boardId"
    }
    
    // Calendar
    object Calendar : Screen("calendar")
    
    // Notifications
    object Notifications : Screen("notifications")
    object NotificationSettings : Screen("notification_settings")
    
    // Profile
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    
    // Admin
    object AdminDashboard : Screen("admin_dashboard")
    object UserManagement : Screen("user_management")
    object UserDetail : Screen("user_detail/{userId}") {
        fun createRoute(userId: Int) = "user_detail/$userId"
    }
}
