package com.miplan

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase Application principal de MiPlan
 * Anotada con @HiltAndroidApp para habilitar Hilt Dependency Injection
 */
@HiltAndroidApp
class MiPlanApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Inicialización de librerías si es necesario
    }
}
