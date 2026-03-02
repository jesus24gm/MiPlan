package com.miplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplan.domain.model.Board
import com.miplan.domain.model.UiState
import com.miplan.domain.repository.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para gestión de tableros
 */
@HiltViewModel
class BoardViewModel @Inject constructor(
    private val boardRepository: BoardRepository
) : ViewModel() {
    
    private val _boardsState = MutableStateFlow<UiState<List<Board>>>(UiState.Idle)
    val boardsState: StateFlow<UiState<List<Board>>> = _boardsState.asStateFlow()
    
    private val _boardDetailState = MutableStateFlow<UiState<Board>>(UiState.Idle)
    val boardDetailState: StateFlow<UiState<Board>> = _boardDetailState.asStateFlow()
    
    private val _createBoardState = MutableStateFlow<UiState<Board>>(UiState.Idle)
    val createBoardState: StateFlow<UiState<Board>> = _createBoardState.asStateFlow()
    
    private val _updateBoardState = MutableStateFlow<UiState<Board>>(UiState.Idle)
    val updateBoardState: StateFlow<UiState<Board>> = _updateBoardState.asStateFlow()
    
    private val _deleteBoardState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteBoardState: StateFlow<UiState<Unit>> = _deleteBoardState.asStateFlow()
    
    /**
     * Carga todos los tableros del usuario
     */
    fun loadBoards() {
        viewModelScope.launch {
            _boardsState.value = UiState.Loading
            
            val result = boardRepository.getBoards()
            
            _boardsState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al cargar tableros")
            }
        }
    }
    
    /**
     * Carga un tablero por ID
     */
    fun loadBoardById(id: Int) {
        viewModelScope.launch {
            _boardDetailState.value = UiState.Loading
            
            val result = boardRepository.getBoardById(id)
            
            _boardDetailState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al cargar tablero")
            }
        }
    }
    
    /**
     * Crea un nuevo tablero
     */
    fun createBoard(name: String, description: String?, color: String) {
        viewModelScope.launch {
            _createBoardState.value = UiState.Loading
            
            val result = boardRepository.createBoard(name, description, color)
            
            _createBoardState.value = if (result.isSuccess) {
                loadBoards() // Recargar lista
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al crear tablero")
            }
        }
    }
    
    /**
     * Actualiza un tablero existente
     */
    fun updateBoard(id: Int, name: String, description: String?, color: String) {
        viewModelScope.launch {
            _updateBoardState.value = UiState.Loading
            
            val result = boardRepository.updateBoard(id, name, description, color)
            
            _updateBoardState.value = if (result.isSuccess) {
                loadBoards() // Recargar lista
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar tablero")
            }
        }
    }
    
    /**
     * Elimina un tablero
     */
    fun deleteBoard(id: Int) {
        viewModelScope.launch {
            _deleteBoardState.value = UiState.Loading
            
            val result = boardRepository.deleteBoard(id)
            
            _deleteBoardState.value = if (result.isSuccess) {
                loadBoards() // Recargar lista
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al eliminar tablero")
            }
        }
    }
    
    /**
     * Resetea estados
     */
    fun resetCreateState() {
        _createBoardState.value = UiState.Idle
    }
    
    fun resetUpdateState() {
        _updateBoardState.value = UiState.Idle
    }
    
    fun resetDeleteState() {
        _deleteBoardState.value = UiState.Idle
    }
}
