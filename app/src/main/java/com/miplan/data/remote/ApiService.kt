package com.miplan.data.remote

import com.miplan.data.remote.dto.request.*
import com.miplan.data.remote.dto.response.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
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
    
    // ==================== COLUMNS ====================
    
    suspend fun getColumnsByBoard(boardId: Int): ApiResponse<List<ColumnResponse>> {
        return client.get("$API_PREFIX/boards/$boardId/columns").body()
    }
    
    suspend fun createColumn(request: CreateColumnRequest): ApiResponse<ColumnResponse> {
        return client.post("$API_PREFIX/columns") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun updateColumn(id: Int, request: UpdateColumnRequest): ApiResponse<ColumnResponse> {
        return client.put("$API_PREFIX/columns/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun deleteColumn(id: Int): ApiResponse<String> {
        return client.delete("$API_PREFIX/columns/$id").body()
    }
    
    suspend fun moveColumn(id: Int, request: MoveColumnRequest): ApiResponse<ColumnResponse> {
        return client.put("$API_PREFIX/columns/$id/move") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    // ==================== CARDS ====================
    
    suspend fun getCardsByColumn(columnId: Int): ApiResponse<List<CardResponse>> {
        return client.get("$API_PREFIX/columns/$columnId/cards").body()
    }
    
    suspend fun getCardById(id: Int): ApiResponse<CardResponse> {
        return client.get("$API_PREFIX/cards/$id").body()
    }
    
    suspend fun createCard(request: CreateCardRequest): ApiResponse<CardResponse> {
        return client.post("$API_PREFIX/cards") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun updateCard(id: Int, request: UpdateCardRequest): ApiResponse<CardResponse> {
        return client.put("$API_PREFIX/cards/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun deleteCard(id: Int): ApiResponse<String> {
        return client.delete("$API_PREFIX/cards/$id").body()
    }
    
    suspend fun moveCard(id: Int, request: MoveCardRequest): ApiResponse<CardResponse> {
        return client.put("$API_PREFIX/cards/$id/move") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun linkTaskToCard(cardId: Int, request: LinkTaskToCardRequest): ApiResponse<CardResponse> {
        return client.post("$API_PREFIX/cards/$cardId/link-task") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun unlinkTaskFromCard(cardId: Int): ApiResponse<CardResponse> {
        return client.delete("$API_PREFIX/cards/$cardId/unlink-task").body()
    }
    
    suspend fun createTaskFromCard(cardId: Int, request: CreateTaskFromCardRequest): ApiResponse<CardResponse> {
        return client.post("$API_PREFIX/cards/$cardId/create-task") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    // ==================== CHECKLISTS ====================
    
    suspend fun getChecklistsByCard(cardId: Int): ApiResponse<List<ChecklistResponse>> {
        return client.get("$API_PREFIX/cards/$cardId/checklists").body()
    }
    
    suspend fun createChecklist(request: CreateChecklistRequest): ApiResponse<ChecklistResponse> {
        return client.post("$API_PREFIX/checklists") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun updateChecklist(id: Int, request: UpdateChecklistRequest): ApiResponse<ChecklistResponse> {
        return client.put("$API_PREFIX/checklists/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun deleteChecklist(id: Int): ApiResponse<String> {
        return client.delete("$API_PREFIX/checklists/$id").body()
    }
    
    suspend fun createChecklistItem(request: CreateChecklistItemRequest): ApiResponse<ChecklistItemResponse> {
        return client.post("$API_PREFIX/checklist-items") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun updateChecklistItem(id: Int, request: UpdateChecklistItemRequest): ApiResponse<ChecklistItemResponse> {
        return client.put("$API_PREFIX/checklist-items/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun deleteChecklistItem(id: Int): ApiResponse<String> {
        return client.delete("$API_PREFIX/checklist-items/$id").body()
    }
    
    // ==================== ATTACHMENTS ====================
    
    suspend fun getAttachmentsByCard(cardId: Int): ApiResponse<List<AttachmentResponse>> {
        return client.get("$API_PREFIX/cards/$cardId/attachments").body()
    }
    
    suspend fun deleteAttachment(id: Int): ApiResponse<String> {
        return client.delete("$API_PREFIX/attachments/$id").body()
    }
    
    // ==================== NOTIFICATIONS ====================
    
    suspend fun getNotifications(): ApiResponse<List<NotificationResponse>> {
        val response = client.get("$API_PREFIX/notifications")
        return if (response.status.isSuccess()) {
            response.body()
        } else {
            ApiResponse(
                success = false,
                message = "Error ${response.status.value}: ${response.status.description}",
                data = null
            )
        }
    }
    
    suspend fun getUnreadNotifications(): ApiResponse<List<NotificationResponse>> {
        val response = client.get("$API_PREFIX/notifications/unread")
        return if (response.status.isSuccess()) {
            response.body()
        } else {
            ApiResponse(
                success = false,
                message = "Error ${response.status.value}: ${response.status.description}",
                data = null
            )
        }
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
    
    suspend fun getUserById(id: Int): ApiResponse<UserDto> {
        return client.get("$API_PREFIX/users/$id").body()
    }
    
    suspend fun updateProfile(name: String, email: String): ApiResponse<UserDto> {
        return client.put("$API_PREFIX/users/profile") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to name, "email" to email))
        }.body()
    }
    
    suspend fun updateAvatar(avatarUrl: String): ApiResponse<UserDto> {
        return client.put("$API_PREFIX/users/avatar") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("avatarUrl" to avatarUrl))
        }.body()
    }
    
    suspend fun changePassword(currentPassword: String, newPassword: String): ApiResponse<String> {
        return client.put("$API_PREFIX/users/password") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("currentPassword" to currentPassword, "newPassword" to newPassword))
        }.body()
    }
    
    suspend fun deleteAccount(): ApiResponse<String> {
        return client.delete("$API_PREFIX/users/account").body()
    }
    
    suspend fun updateFcmToken(token: String): ApiResponse<String> {
        return client.put("$API_PREFIX/users/fcm-token") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("fcmToken" to token))
        }.body()
    }
    
    suspend fun getUserStats(): ApiResponse<com.miplan.domain.repository.UserStats> {
        return client.get("$API_PREFIX/users/stats").body()
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
    
    // ==================== ADMIN ====================
    
    suspend fun getAllUsers(): ApiResponse<List<UserDto>> {
        return client.get("$API_PREFIX/admin/users").body()
    }
    
    suspend fun updateUserRole(userId: Int, role: String): ApiResponse<UserDto> {
        return client.patch("$API_PREFIX/admin/users/$userId/role") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("role" to role))
        }.body()
    }
    
    suspend fun getAdminStats(): ApiResponse<AdminStatsResponse> {
        return client.get("$API_PREFIX/admin/stats").body()
    }
    
    suspend fun toggleUserStatus(userId: Int, isActive: Boolean): ApiResponse<UserDto> {
        return client.patch("$API_PREFIX/admin/users/$userId/status") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("isActive" to isActive))
        }.body()
    }
    
    // ==================== COLLABORATORS ====================
    
    suspend fun getTaskCollaborators(taskId: Int): ApiResponse<List<com.miplan.data.remote.dto.response.CollaboratorDto>> {
        return client.get("$API_PREFIX/tasks/$taskId/collaborators").body()
    }
    
    suspend fun addCollaborator(taskId: Int, request: com.miplan.data.remote.dto.response.AddCollaboratorRequest): ApiResponse<com.miplan.data.remote.dto.response.CollaboratorDto> {
        return client.post("$API_PREFIX/tasks/$taskId/collaborators") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun updateCollaboratorRole(taskId: Int, userId: Int, request: com.miplan.data.remote.dto.response.UpdateCollaboratorRoleRequest): ApiResponse<String> {
        return client.put("$API_PREFIX/tasks/$taskId/collaborators/$userId/role") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun removeCollaborator(taskId: Int, userId: Int): ApiResponse<String> {
        return client.delete("$API_PREFIX/tasks/$taskId/collaborators/$userId").body()
    }
    
    suspend fun getSharedTasks(): ApiResponse<List<Int>> {
        return client.get("$API_PREFIX/tasks/shared").body()
    }
    
    suspend fun searchUserByEmail(email: String): ApiResponse<UserDto> {
        return client.get("$API_PREFIX/users/search?email=$email").body()
    }
}
