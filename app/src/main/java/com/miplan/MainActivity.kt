package com.miplan

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.miplan.notifications.NotificationHelper
import com.miplan.ui.navigation.NavGraph
import com.miplan.ui.theme.MiPlanTheme
import com.miplan.utils.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal de la aplicación
 * Punto de entrada de la UI con Jetpack Compose
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    // Launcher para solicitar permiso de notificaciones
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // Si el usuario rechaza, podríamos mostrar un diálogo explicativo
            // Por ahora, simplemente continuamos sin notificaciones
        }
    }
    
    // Launcher para solicitar permiso de alarmas exactas
    private val exactAlarmPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // El usuario regresó de la configuración de alarmas
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Solicitar permisos de notificaciones si es necesario
        requestNotificationPermissions()
        
        val taskId = intent?.getIntExtra("taskId", -1) ?: -1
        val openTaskDetail = intent?.getBooleanExtra("openTaskDetail", false) ?: false
        
        setContent {
            MiPlanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        initialTaskId = if (openTaskDetail && taskId != -1) taskId else null
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: android.content.Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        
        // Manejar nueva notificación cuando la app ya está abierta
        val taskId = intent?.getIntExtra("taskId", -1) ?: -1
        val openTaskDetail = intent?.getBooleanExtra("openTaskDetail", false) ?: false
        
        if (openTaskDetail && taskId != -1) {
            // Aquí se podría emitir un evento para navegar, pero por ahora
            // el usuario puede cerrar y volver a abrir la notificación
        }
    }
    
    /**
     * Solicita los permisos necesarios para notificaciones
     */
    private fun requestNotificationPermissions() {
        // Solicitar permiso de notificaciones (Android 13+)
        if (PermissionUtils.shouldRequestNotificationPermission(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        // Solicitar permiso de alarmas exactas (Android 12+)
        if (PermissionUtils.shouldRequestExactAlarmPermission(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                exactAlarmPermissionLauncher.launch(intent)
            }
        }
    }
}
