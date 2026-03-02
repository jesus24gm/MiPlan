package com.miplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplan.domain.model.UiState
import com.miplan.domain.model.User
import com.miplan.domain.repository.AuthRepository
import com.miplan.domain.repository.UserRepository
import com.miplan.domain.repository.UserStats
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
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val loginState: StateFlow<UiState<User>> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val registerState: StateFlow<UiState<String>> = _registerState.asStateFlow()
    
    private val _verifyEmailState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val verifyEmailState: StateFlow<UiState<String>> = _verifyEmailState.asStateFlow()
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _updateProfileState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val updateProfileState: StateFlow<UiState<User>> = _updateProfileState.asStateFlow()
    
    private val _changePasswordState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val changePasswordState: StateFlow<UiState<Unit>> = _changePasswordState.asStateFlow()
    
    private val _deleteAccountState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteAccountState: StateFlow<UiState<Unit>> = _deleteAccountState.asStateFlow()
    
    private val _updateAvatarState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val updateAvatarState: StateFlow<UiState<User>> = _updateAvatarState.asStateFlow()
    
    private val _userStatsState = MutableStateFlow<UiState<UserStats>>(UiState.Idle)
    val userStatsState: StateFlow<UiState<UserStats>> = _userStatsState.asStateFlow()
    
    init {
        checkAuthentication()
    }
    
    /**
     * Verifica si el usuario está autenticado
     */
    private fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = authRepository.isAuthenticated()
            if (_isAuthenticated.value) {
                loadCurrentUser()
            }
        }
    }
    
    /**
     * Carga el usuario actual
     */
    fun loadCurrentUser() {
        viewModelScope.launch {
            val result = authRepository.getCurrentUser()
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
            }
        }
    }
    
    /**
     * Inicia sesión
     */
    fun login(email: String, password: String, rememberMe: Boolean = true) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            
            val result = authRepository.login(email, password, rememberMe)
            
            _loginState.value = if (result.isSuccess) {
                val (_, user) = result.getOrNull()!!
                _isAuthenticated.value = true
                
                // Obtener y enviar token FCM al backend
                updateFcmToken()
                
                UiState.Success(user)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al iniciar sesión")
            }
        }
    }
    
    /**
     * Obtiene el token FCM y lo envía al backend
     */
    private fun updateFcmToken() {
        viewModelScope.launch {
            try {
                // Importar Firebase Messaging
                val task = com.google.firebase.messaging.FirebaseMessaging.getInstance().token
                task.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val token = tokenTask.result
                        viewModelScope.launch {
                            userRepository.updateFcmToken(token)
                        }
                    }
                }
            } catch (e: Exception) {
                // Silenciosamente fallar si Firebase no está configurado
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
    
    /**
     * Actualiza el perfil del usuario
     */
    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            _updateProfileState.value = UiState.Loading
            
            val result = userRepository.updateProfile(name, email)
            
            _updateProfileState.value = if (result.isSuccess) {
                val user = result.getOrNull()!!
                _currentUser.value = user
                UiState.Success(user)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar perfil")
            }
        }
    }
    
    /**
     * Cambia la contraseña del usuario
     */
    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _changePasswordState.value = UiState.Loading
            
            val result = userRepository.changePassword(currentPassword, newPassword)
            
            _changePasswordState.value = if (result.isSuccess) {
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al cambiar contraseña")
            }
        }
    }
    
    /**
     * Elimina la cuenta del usuario
     */
    fun deleteAccount() {
        viewModelScope.launch {
            _deleteAccountState.value = UiState.Loading
            
            val result = userRepository.deleteAccount()
            
            _deleteAccountState.value = if (result.isSuccess) {
                logout()
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al eliminar cuenta")
            }
        }
    }
    
    /**
     * Obtiene las estadísticas del usuario
     */
    fun loadUserStats() {
        viewModelScope.launch {
            _userStatsState.value = UiState.Loading
            
            val result = userRepository.getUserStats()
            
            _userStatsState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al cargar estadísticas")
            }
        }
    }
    
    /**
     * Resetea el estado de actualización de perfil
     */
    fun resetUpdateProfileState() {
        _updateProfileState.value = UiState.Idle
    }
    
    /**
     * Resetea el estado de cambio de contraseña
     */
    fun resetChangePasswordState() {
        _changePasswordState.value = UiState.Idle
    }
    
    /**
     * Resetea el estado de eliminación de cuenta
     */
    fun resetDeleteAccountState() {
        _deleteAccountState.value = UiState.Idle
    }
    
    /**
     * Actualiza el avatar del usuario
     */
    fun updateAvatar(avatarUrl: String) {
        viewModelScope.launch {
            _updateAvatarState.value = UiState.Loading
            
            val result = userRepository.updateAvatar(avatarUrl)
            
            _updateAvatarState.value = if (result.isSuccess) {
                val updatedUser = result.getOrNull()
                _currentUser.value = updatedUser
                
                // NOTA: No recargamos el usuario desde /api/auth/me porque ese endpoint
                // no devuelve el avatarUrl, lo que causaría que el avatar desaparezca.
                // El backend debe corregir /api/auth/me para incluir avatarUrl.
                
                UiState.Success(updatedUser!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar avatar")
            }
        }
    }
    
    /**
     * Resetea el estado de actualización de avatar
     */
    fun resetUpdateAvatarState() {
        _updateAvatarState.value = UiState.Idle
    }
}
