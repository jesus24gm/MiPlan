package com.miplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.miplan.notifications.NotificationHelper
import com.miplan.ui.navigation.NavGraph
import com.miplan.ui.theme.MiPlanTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Activity principal de la aplicación
 * Punto de entrada de la UI con Jetpack Compose
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // PRUEBA TEMPORAL: Mostrar notificación de prueba después de 5 segundos
        // ELIMINAR ESTO DESPUÉS DE VERIFICAR QUE FUNCIONA
        lifecycleScope.launch {
            delay(5000) // Esperar 5 segundos
            NotificationHelper.showTaskCreatedNotification(
                this@MainActivity,
                taskId = 999,
                taskTitle = "🔔 PRUEBA DE NOTIFICACIÓN",
                dueDate = "2026-02-26",
                dueTime = "15:00"
            )
        }
        
        setContent {
            MiPlanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
