package com.miplan.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplan.domain.model.Board
import com.miplan.domain.model.Card
import com.miplan.domain.model.Checklist
import com.miplan.domain.model.ChecklistItem
import com.miplan.domain.model.Column
import com.miplan.domain.model.UiState
import com.miplan.domain.repository.AttachmentRepository
import com.miplan.domain.repository.BoardRepository
import com.miplan.domain.repository.CardRepository
import com.miplan.domain.repository.ChecklistRepository
import com.miplan.domain.repository.ColumnRepository
import com.miplan.notifications.NotificationScheduler
import com.miplan.data.preferences.NotificationPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel para la gestión de tableros Kanban
 */
@HiltViewModel
class KanbanViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val columnRepository: ColumnRepository,
    private val cardRepository: CardRepository,
    private val checklistRepository: ChecklistRepository,
    private val attachmentRepository: AttachmentRepository,
    private val application: Application,
    private val notificationPreferences: NotificationPreferences
) : ViewModel() {
    
    private val notificationScheduler = NotificationScheduler(application, notificationPreferences)
    
    // ==================== BOARDS ====================
    
    private val _boardsState = MutableStateFlow<UiState<List<Board>>>(UiState.Loading)
    val boardsState: StateFlow<UiState<List<Board>>> = _boardsState.asStateFlow()
    
    private val _selectedBoard = MutableStateFlow<Board?>(null)
    val selectedBoard: StateFlow<Board?> = _selectedBoard.asStateFlow()
    
    // ==================== COLUMNS ====================
    
    private val _columnsState = MutableStateFlow<UiState<List<Column>>>(UiState.Loading)
    val columnsState: StateFlow<UiState<List<Column>>> = _columnsState.asStateFlow()
    
    // ==================== CARDS ====================
    
    private val _selectedCard = MutableStateFlow<Card?>(null)
    val selectedCard: StateFlow<Card?> = _selectedCard.asStateFlow()
    
    // ==================== BOARD OPERATIONS ====================
    
    fun loadBoards() {
        viewModelScope.launch {
            _boardsState.value = UiState.Loading
            
            val result = boardRepository.getBoards()
            
            result.onSuccess { boards ->
                _boardsState.value = UiState.Success(boards)
            }.onFailure { error ->
                _boardsState.value = UiState.Error(error.message ?: "Error al cargar tableros")
            }
        }
    }
    
    fun selectBoard(board: Board) {
        _selectedBoard.value = board
        loadColumns(board.id)
    }
    
    fun createBoard(
        name: String,
        description: String?,
        color: String,
        backgroundImageUrl: String? = null
    ) {
        viewModelScope.launch {
            val result = boardRepository.createBoard(name, description, color, backgroundImageUrl)
            
            result.onSuccess {
                loadBoards()
            }.onFailure { error ->
                _boardsState.value = UiState.Error(error.message ?: "Error al crear tablero")
            }
        }
    }
    
    fun updateBoard(
        id: Int,
        name: String,
        description: String?,
        color: String,
        backgroundImageUrl: String? = null
    ) {
        viewModelScope.launch {
            val result = boardRepository.updateBoard(id, name, description, color, backgroundImageUrl)
            
            result.onSuccess { updatedBoard ->
                _selectedBoard.value = updatedBoard
                loadBoards()
            }.onFailure { error ->
                _boardsState.value = UiState.Error(error.message ?: "Error al actualizar tablero")
            }
        }
    }
    
    fun deleteBoard(id: Int) {
        viewModelScope.launch {
            val result = boardRepository.deleteBoard(id)
            
            result.onSuccess {
                _selectedBoard.value = null
                loadBoards()
            }.onFailure { error ->
                _boardsState.value = UiState.Error(error.message ?: "Error al eliminar tablero")
            }
        }
    }
    
    // ==================== COLUMN OPERATIONS ====================
    
    fun loadColumns(boardId: Int) {
        viewModelScope.launch {
            _columnsState.value = UiState.Loading
            
            val result = columnRepository.getColumnsByBoard(boardId)
            
            result.onSuccess { columns ->
                _columnsState.value = UiState.Success(columns)
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al cargar columnas")
            }
        }
    }
    
    fun createColumn(boardId: Int, title: String) {
        viewModelScope.launch {
            val result = columnRepository.createColumn(boardId, title)
            
            result.onSuccess { column ->
                // Recargar columnas para mostrar la nueva columna
                loadColumns(boardId)
            }.onFailure { error ->
                // Solo mostrar error si realmente falló la creación
                _columnsState.value = UiState.Error(error.message ?: "Error al crear columna")
            }
        }
    }
    
    fun updateColumn(id: Int, title: String, boardId: Int) {
        viewModelScope.launch {
            val result = columnRepository.updateColumn(id, title, null)
            
            result.onSuccess {
                loadColumns(boardId)
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al actualizar columna")
            }
        }
    }
    
    fun deleteColumn(id: Int, boardId: Int) {
        viewModelScope.launch {
            val result = columnRepository.deleteColumn(id)
            
            result.onSuccess {
                loadColumns(boardId)
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al eliminar columna")
            }
        }
    }
    
    // ==================== CARD OPERATIONS ====================
    
    fun selectCard(card: Card) {
        _selectedCard.value = card
    }
    
    fun createCard(
        columnId: Int,
        title: String,
        description: String? = null,
        dueDate: String? = null,
        taskId: Int? = null,
        boardId: Int
    ) {
        viewModelScope.launch {
            // No cambiar el estado a Loading aquí para evitar parpadeo
            val result = cardRepository.createCard(
                columnId = columnId,
                title = title,
                description = description,
                position = null,
                taskId = taskId,
                dueDate = dueDate,
                priority = null,
                labels = null
            )
            
            result.onSuccess { card ->
                // Programar notificaciones si tiene fecha límite
                if (!dueDate.isNullOrBlank() && notificationPreferences.cardNotificationsEnabled) {
                    try {
                        val dueDateParsed = LocalDateTime.parse(dueDate.replace(" ", "T").substringBefore("."))
                        val hasTime = dueDate.contains(":")
                        notificationScheduler.scheduleCardNotifications(
                            cardId = card.id,
                            dueDate = dueDateParsed,
                            hasSpecificTime = hasTime
                        )
                    } catch (e: Exception) {
                        // Error al programar notificación, continuar sin notificación
                    }
                }
                
                // Recargar columnas para mostrar la nueva tarjeta
                loadColumns(boardId)
            }.onFailure { error ->
                // Solo mostrar error si realmente falló la creación
                _columnsState.value = UiState.Error(error.message ?: "Error al crear tarjeta")
            }
        }
    }
    
    fun updateCard(
        id: Int,
        title: String?,
        description: String?,
        dueDate: String?,
        priority: String?,
        labels: String?,
        boardId: Int
    ) {
        viewModelScope.launch {
            val result = cardRepository.updateCard(id, title, description, dueDate, priority, labels)
            
            result.onSuccess { updatedCard ->
                _selectedCard.value = updatedCard
                
                // Re-programar notificaciones si tiene fecha límite
                if (!dueDate.isNullOrBlank() && notificationPreferences.cardNotificationsEnabled) {
                    try {
                        val dueDateParsed = LocalDateTime.parse(dueDate.replace(" ", "T").substringBefore("."))
                        val hasTime = dueDate.contains(":")
                        notificationScheduler.scheduleCardNotifications(
                            cardId = id,
                            dueDate = dueDateParsed,
                            hasSpecificTime = hasTime
                        )
                    } catch (e: Exception) {
                        // Error al programar notificación, continuar sin notificación
                    }
                } else {
                    // Si no tiene fecha, cancelar notificaciones
                    notificationScheduler.cancelCardNotifications(id)
                }
                
                loadColumns(boardId)
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al actualizar tarjeta")
            }
        }
    }
    
    fun deleteCard(id: Int, boardId: Int) {
        viewModelScope.launch {
            val result = cardRepository.deleteCard(id)
            
            result.onSuccess {
                // Cancelar notificaciones de la tarjeta eliminada
                notificationScheduler.cancelCardNotifications(id)
                
                _selectedCard.value = null
                loadColumns(boardId)
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al eliminar tarjeta")
            }
        }
    }
    
    fun linkTaskToCard(cardId: Int, taskId: Int, boardId: Int) {
        viewModelScope.launch {
            val result = cardRepository.linkTaskToCard(cardId, taskId)
            
            result.onSuccess {
                loadColumns(boardId)
                refreshSelectedCard(cardId)
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al vincular tarea")
            }
        }
    }
    
    fun unlinkTaskFromCard(cardId: Int, boardId: Int) {
        viewModelScope.launch {
            val result = cardRepository.unlinkTaskFromCard(cardId)
            
            result.onSuccess {
                loadColumns(boardId)
                refreshSelectedCard(cardId)
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al desvincular tarea")
            }
        }
    }
    
    fun createTaskFromCard(
        cardId: Int,
        title: String,
        description: String?,
        priority: String,
        dueDate: String?,
        boardId: Int
    ) {
        viewModelScope.launch {
            val result = cardRepository.createTaskFromCard(
                cardId = cardId,
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate
            )
            
            result.onSuccess {
                loadColumns(boardId)
                refreshSelectedCard(cardId)
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al crear tarea")
            }
        }
    }
    
    // ==================== CHECKLIST OPERATIONS ====================
    
    fun createChecklist(cardId: Int, title: String, boardId: Int) {
        viewModelScope.launch {
            val result = checklistRepository.createChecklist(cardId, title)
            
            result.onSuccess {
                loadColumns(boardId)
                refreshSelectedCard(cardId)
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al crear checklist")
            }
        }
    }
    
    fun createChecklistWithItem(cardId: Int, checklistTitle: String, itemTitle: String, boardId: Int) {
        viewModelScope.launch {
            // Primero crear el checklist
            val checklistResult = checklistRepository.createChecklist(cardId, checklistTitle)
            
            checklistResult.onSuccess { checklist ->
                // Luego crear el item en el checklist recién creado
                val itemResult = checklistRepository.createChecklistItem(checklist.id, itemTitle)
                
                itemResult.onSuccess {
                    loadColumns(boardId)
                    refreshSelectedCard(cardId)
                }.onFailure { error ->
                    _columnsState.value = UiState.Error(error.message ?: "Error al crear item")
                }
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al crear checklist")
            }
        }
    }
    
    fun createChecklistItem(checklistId: Int, title: String, boardId: Int) {
        viewModelScope.launch {
            val result = checklistRepository.createChecklistItem(checklistId, title)
            
            result.onSuccess {
                loadColumns(boardId)
                _selectedCard.value?.let { refreshSelectedCard(it.id) }
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al crear item")
            }
        }
    }
    
    fun toggleChecklistItem(id: Int, isCompleted: Boolean, boardId: Int) {
        viewModelScope.launch {
            val result = checklistRepository.updateChecklistItem(id, null, isCompleted)
            
            result.onSuccess {
                loadColumns(boardId)
                _selectedCard.value?.let { refreshSelectedCard(it.id) }
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al actualizar item")
            }
        }
    }
    
    fun updateChecklistItem(id: Int, title: String, boardId: Int) {
        viewModelScope.launch {
            val result = checklistRepository.updateChecklistItem(id, title, null)
            
            result.onSuccess {
                loadColumns(boardId)
                _selectedCard.value?.let { refreshSelectedCard(it.id) }
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al actualizar item")
            }
        }
    }
    
    fun deleteChecklistItem(id: Int, boardId: Int) {
        viewModelScope.launch {
            val result = checklistRepository.deleteChecklistItem(id)
            
            result.onSuccess {
                loadColumns(boardId)
                _selectedCard.value?.let { refreshSelectedCard(it.id) }
            }.onFailure { error ->
                _columnsState.value = UiState.Error(error.message ?: "Error al eliminar item")
            }
        }
    }
    
    // ==================== HELPERS ====================
    
    private suspend fun refreshSelectedCard(cardId: Int) {
        val result = cardRepository.getCardById(cardId)
        result.onSuccess { card ->
            _selectedCard.value = card
        }
    }
    
    fun clearSelectedBoard() {
        _selectedBoard.value = null
        _columnsState.value = UiState.Loading
    }
    
    fun setSelectedCard(card: Card) {
        _selectedCard.value = card
    }
    
    fun clearSelectedCard() {
        _selectedCard.value = null
    }
}
