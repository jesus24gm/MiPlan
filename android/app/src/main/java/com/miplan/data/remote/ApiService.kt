package com.miplan.data.remote

import com.miplan.data.remote.dto.request.*
import com.miplan.data.remote.dto.response.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio API que define todos los endpoints
 */
@Singleton
class ApiService @Inject constructor(
    private val client: HttpClient
) {
    
    companion object {
        private const val API_PREFIX = "/api"
    }
    
    // ==================== AUTH ====================
    
    suspend fun register(request: RegisterRequest): ApiResponse<String> {
        return client.post("$API_PREFIX/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun login(request: LoginRequest): ApiResponse<AuthResponse> {
        return client.post("$API_PREFIX/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun verifyEmail(token: String): ApiResponse<String> {
        return client.get("$API_PREFIX/auth/verify/$token").body()
    }
    
    suspend fun getCurrentUser(): ApiResponse<UserDto> {
        return client.get("$API_PREFIX/auth/me").body()
    }
    
    suspend fun logout(): ApiResponse<String> {
        return client.post("$API_PREFIX/auth/logout").body()
    }
    
    // ==================== TASKS ====================
    
    suspend fun getTasks(): ApiResponse<List<TaskResponse>> {
        return client.get("$API_PREFIX/tasks").body()
    }
    
    suspend fun getTaskById(id: Int): ApiResponse<TaskResponse> {
        return client.get("$API_PREFIX/tasks/$id").body()
    }
    
    suspend fun getTasksByBoard(boardId: Int): ApiResponse<List<TaskResponse>> {
        return client.get("$API_PREFIX/tasks/board/$boardId").body()
    }
    
    suspend fun getTasksByStatus(status: String): ApiResponse<List<TaskResponse>> {
        return client.get("$API_PREFIX/tasks/status/$status").body()
    }
    
    suspend fun getTasksByDate(date: String): ApiResponse<List<TaskResponse>> {
        return client.get("$API_PREFIX/tasks/date/$date").body()
    }
    
    suspend fun createTask(request: CreateTaskRequest): ApiResponse<TaskResponse> {
        return client.post("$API_PREFIX/tasks") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun updateTask(id: Int, request: UpdateTaskRequest): ApiResponse<TaskResponse> {
        return client.put("$API_PREFIX/tasks/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun deleteTask(id: Int): ApiResponse<String> {
        return client.delete("$API_PREFIX/tasks/$id").body()
    }
    
    suspend fun updateTaskStatus(id: Int, status: String): ApiResponse<TaskResponse> {
        return client.patch("$API_PREFIX/tasks/$id/status") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("status" to status))
        }.body()
    }
    
    // ==================== BOARDS ====================
    
    suspend fun getBoards(): ApiResponse<List<BoardResponse>> {
        return client.get("$API_PREFIX/boards").body()
    }
    
    suspend fun getBoardById(id: Int): ApiResponse<BoardResponse> {
        return client.get("$API_PREFIX/boards/$id").body()
    }
    
    suspend fun createBoard(request: CreateBoardRequest): ApiResponse<BoardResponse> {
        return client.post("$API_PREFIX/boards") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun updateBoard(id: Int, request: CreateBoardRequest): ApiResponse<BoardResponse> {
        return client.put("$API_PREFIX/boards/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun deleteBoard(id: Int): ApiResponse<String> {
        return client.delete("$API_PREFIX/boards/$id").body()
    }
    
    // ==================== NOTIFICATIONS ====================
    
    suspend fun getNotifications(): ApiResponse<List<NotificationResponse>> {
        return client.get("$API_PREFIX/notifications").body()
    }
    
    suspend fun getUnreadNotifications(): ApiResponse<List<NotificationResponse>> {
        return client.get("$API_PREFIX/notifications/unread").body()
    }
    
    suspend fun markNotificationAsRead(id: Int): ApiResponse<String> {
        return client.put("$API_PREFIX/notifications/$id/read").body()
    }
    
    suspend fun markAllNotificationsAsRead(): ApiResponse<String> {
        return client.put("$API_PREFIX/notifications/read-all").body()
    }
    
    suspend fun deleteNotification(id: Int): ApiResponse<String> {
        return client.delete("$API_PREFIX/notifications/$id").body()
    }
    
    // ==================== USERS ====================
    
    suspend fun getAllUsers(): ApiResponse<List<UserDto>> {
        return client.get("$API_PREFIX/admin/users").body()
    }
    
    suspend fun getUserById(id: Int): ApiResponse<UserDto> {
        return client.get("$API_PREFIX/users/$id").body()
    }
    
    suspend fun updateProfile(name: String, email: String): ApiResponse<UserDto> {
        return client.put("$API_PREFIX/users/profile") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to name, "email" to email))
        }.body()
    }
    
    suspend fun changeUserRole(userId: Int, role: String): ApiResponse<String> {
        return client.put("$API_PREFIX/admin/users/$userId/role") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("role" to role))
        }.body()
    }
    
    suspend fun deleteUser(userId: Int): ApiResponse<String> {
        return client.delete("$API_PREFIX/admin/users/$userId").body()
    }
}
