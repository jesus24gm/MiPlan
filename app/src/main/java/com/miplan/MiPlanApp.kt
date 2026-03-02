package com.miplan

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkerFactory
import com.miplan.notifications.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Clase Application principal de MiPlan
 * Anotada con @HiltAndroidApp para habilitar Hilt Dependency Injection
 */
@HiltAndroidApp
class MiPlanApp : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: WorkerFactory
    
    override fun onCreate() {
        super.onCreate()
        
        // Crear canales de notificación
        NotificationHelper.createNotificationChannels(this)
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
