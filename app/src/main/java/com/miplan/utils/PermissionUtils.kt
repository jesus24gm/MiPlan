package com.miplan.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Utilidades para gestión de permisos
 */
object PermissionUtils {
    
    /**
     * Verifica si se necesita solicitar permiso de notificaciones
     */
    fun shouldRequestNotificationPermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !hasNotificationPermission(context)
    }
    
    /**
     * Verifica si la app tiene permiso de notificaciones
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // En versiones anteriores a Android 13, no se necesita permiso runtime
        }
    }
    
    /**
     * Verifica si se necesita solicitar permiso de alarmas exactas
     */
    fun shouldRequestExactAlarmPermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                !hasExactAlarmPermission(context)
    }
    
    /**
     * Verifica si la app tiene permiso de alarmas exactas
     */
    fun hasExactAlarmPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
}
