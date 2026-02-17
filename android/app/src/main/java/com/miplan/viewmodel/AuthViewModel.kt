package com.miplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplan.domain.model.UiState
import com.miplan.domain.model.User
import com.miplan.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para autenticación
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val loginState: StateFlow<UiState<User>> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val registerState: StateFlow<UiState<String>> = _registerState.asStateFlow()
    
    private val _verifyEmailState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val verifyEmailState: StateFlow<UiState<String>> = _verifyEmailState.asStateFlow()
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    init {
        checkAuthentication()
    }
    
    /**
     * Verifica si el usuario está autenticado
     */
    private fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = authRepository.isAuthenticated()
        }
    }
    
    /**
     * Inicia sesión
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            
            val result = authRepository.login(email, password)
            
            _loginState.value = if (result.isSuccess) {
                val (_, user) = result.getOrNull()!!
                _isAuthenticated.value = true
                UiState.Success(user)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al iniciar sesión")
            }
        }
    }
    
    /**
     * Registra un nuevo usuario
     */
    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _registerState.value = UiState.Loading
            
            val result = authRepository.register(email, password, name)
            
            _registerState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al registrar usuario")
            }
        }
    }
    
    /**
     * Verifica el email con el token
     */
    fun verifyEmail(token: String) {
        viewModelScope.launch {
            _verifyEmailState.value = UiState.Loading
            
            val result = authRepository.verifyEmail(token)
            
            _verifyEmailState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al verificar email")
            }
        }
    }
    
    /**
     * Cierra sesión
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isAuthenticated.value = false
            _loginState.value = UiState.Idle
        }
    }
    
    /**
     * Resetea el estado de login
     */
    fun resetLoginState() {
        _loginState.value = UiState.Idle
    }
    
    /**
     * Resetea el estado de registro
     */
    fun resetRegisterState() {
        _registerState.value = UiState.Idle
    }
}
