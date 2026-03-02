package com.miplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplan.domain.model.Notification
import com.miplan.domain.model.UiState
import com.miplan.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para gestión de notificaciones
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _notificationsState = MutableStateFlow<UiState<List<Notification>>>(UiState.Idle)
    val notificationsState: StateFlow<UiState<List<Notification>>> = _notificationsState.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _markAsReadState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val markAsReadState: StateFlow<UiState<Unit>> = _markAsReadState.asStateFlow()

    /**
     * Carga las notificaciones del usuario
     */
    fun loadNotifications() {
        viewModelScope.launch {
            _notificationsState.value = UiState.Loading

            val result = notificationRepository.getNotifications()

            _notificationsState.value = if (result.isSuccess) {
                val notifications = result.getOrNull() ?: emptyList()
                _unreadCount.value = notifications.count { !it.isRead }
                UiState.Success(notifications)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al cargar notificaciones")
            }
        }
    }

    /**
     * Marca una notificación como leída
     */
    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            _markAsReadState.value = UiState.Loading

            val result = notificationRepository.markAsRead(notificationId)

            _markAsReadState.value = if (result.isSuccess) {
                // Actualizar lista local
                val currentState = _notificationsState.value
                if (currentState is UiState.Success) {
                    val updatedList = currentState.data.map { notification ->
                        if (notification.id == notificationId) {
                            notification.copy(isRead = true)
                        } else {
                            notification
                        }
                    }
                    _notificationsState.value = UiState.Success(updatedList)
                    _unreadCount.value = updatedList.count { !it.isRead }
                }
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al marcar como leída")
            }
        }
    }

    /**
     * Marca todas las notificaciones como leídas
     */
    fun markAllAsRead() {
        viewModelScope.launch {
            _markAsReadState.value = UiState.Loading

            val result = notificationRepository.markAllAsRead()

            _markAsReadState.value = if (result.isSuccess) {
                // Actualizar lista local
                val currentState = _notificationsState.value
                if (currentState is UiState.Success) {
                    val updatedList = currentState.data.map { it.copy(isRead = true) }
                    _notificationsState.value = UiState.Success(updatedList)
                    _unreadCount.value = 0
                }
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al marcar todas como leídas")
            }
        }
    }

    /**
     * Elimina una notificación
     */
    fun deleteNotification(notificationId: Int) {
        viewModelScope.launch {
            val result = notificationRepository.deleteNotification(notificationId)

            if (result.isSuccess) {
                // Actualizar lista local
                val currentState = _notificationsState.value
                if (currentState is UiState.Success) {
                    val updatedList = currentState.data.filter { it.id != notificationId }
                    _notificationsState.value = UiState.Success(updatedList)
                    _unreadCount.value = updatedList.count { !it.isRead }
                }
            }
        }
    }

    /**
     * Resetea el estado de marcar como leída
     */
    fun resetMarkAsReadState() {
        _markAsReadState.value = UiState.Idle
    }
}
