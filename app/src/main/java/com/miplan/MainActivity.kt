package com.miplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.miplan.ui.navigation.NavGraph
import com.miplan.ui.theme.MiPlanTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal de la aplicación
 * Punto de entrada de la UI con Jetpack Compose
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Obtener datos de la notificación
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
}
