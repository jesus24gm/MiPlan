package com.miplan.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.miplan.domain.model.UiState
import com.miplan.ui.components.SnackbarManager
import com.miplan.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

/**
 * Pantalla para cambiar la contraseña del usuario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    
    val changePasswordState by authViewModel.changePasswordState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Validación
    val isValid = currentPassword.isNotBlank() && 
                  newPassword.isNotBlank() && 
                  newPassword.length >= 6 &&
                  newPassword == confirmPassword
    
    // Manejar estado de cambio de contraseña
    LaunchedEffect(changePasswordState) {
        when (changePasswordState) {
            is UiState.Success -> {
                SnackbarManager.showSuccess(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = "Contraseña actualizada correctamente"
                )
                delay(800)
                authViewModel.resetChangePasswordState()
                onNavigateBack()
            }
            is UiState.Error -> {
                SnackbarManager.showError(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = (changePasswordState as UiState.Error).message
                )
                authViewModel.resetChangePasswordState()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cambiar Contraseña") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información de seguridad
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Requisitos de contraseña:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "• Mínimo 6 caracteres\n• Debe ser diferente a la actual",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
            
            // Contraseña actual
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { 
                    currentPassword = it
                },
                label = { Text("Contraseña actual") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                        Icon(
                            if (showCurrentPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showCurrentPassword) "Ocultar" else "Mostrar"
                        )
                    }
                },
                visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                enabled = changePasswordState !is UiState.Loading,
                singleLine = true
            )
            
            Divider()
            
            // Nueva contraseña
            OutlinedTextField(
                value = newPassword,
                onValueChange = { 
                    newPassword = it
                },
                label = { Text("Nueva contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.LockOpen, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { showNewPassword = !showNewPassword }) {
                        Icon(
                            if (showNewPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showNewPassword) "Ocultar" else "Mostrar"
                        )
                    }
                },
                visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                enabled = changePasswordState !is UiState.Loading,
                singleLine = true,
                isError = newPassword.isNotBlank() && newPassword.length < 6,
                supportingText = if (newPassword.isNotBlank() && newPassword.length < 6) {
                    { Text("La contraseña debe tener al menos 6 caracteres") }
                } else null
            )
            
            // Confirmar contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                },
                label = { Text("Confirmar nueva contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.LockOpen, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                        Icon(
                            if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showConfirmPassword) "Ocultar" else "Mostrar"
                        )
                    }
                },
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                enabled = changePasswordState !is UiState.Loading,
                singleLine = true,
                isError = confirmPassword.isNotBlank() && newPassword != confirmPassword,
                supportingText = if (confirmPassword.isNotBlank() && newPassword != confirmPassword) {
                    { Text("Las contraseñas no coinciden") }
                } else null
            )
            
            // Botón de guardar
            Button(
                onClick = {
                    authViewModel.changePassword(currentPassword, newPassword)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isValid && changePasswordState !is UiState.Loading
            ) {
                if (changePasswordState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Cambiar contraseña")
            }
        }
    }
}
