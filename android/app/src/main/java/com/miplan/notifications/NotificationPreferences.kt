package com.miplan.notifications

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestiona las preferencias de notificaciones del usuario
 */
class NotificationPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val PREFS_NAME = "notification_preferences"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_ADVANCE_NOTIFICATIONS_ENABLED = "advance_notifications_enabled"
        private const val KEY_ADVANCE_NOTIFICATION_MINUTES = "advance_notification_minutes"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_REMINDER_MINUTES = "reminder_minutes"
        private const val KEY_TASK_CREATED_NOTIFICATION_ENABLED = "task_created_notification_enabled"
        
        // Valores por defecto
        private const val DEFAULT_ADVANCE_MINUTES = "60" // 1 hora
        private const val DEFAULT_REMINDER_MINUTES = 60 // 1 hora después
    }
    
    /**
     * Habilita o deshabilita todas las notificaciones
     */
    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()
    
    /**
     * Habilita o deshabilita notificaciones anticipadas
     */
    var advanceNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_ADVANCE_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ADVANCE_NOTIFICATIONS_ENABLED, value).apply()
    
    /**
     * Habilita o deshabilita recordatorios
     */
    var reminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMINDER_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER_ENABLED, value).apply()
    
    /**
     * Habilita o deshabilita notificación al crear tarea
     */
    var taskCreatedNotificationEnabled: Boolean
        get() = prefs.getBoolean(KEY_TASK_CREATED_NOTIFICATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_TASK_CREATED_NOTIFICATION_ENABLED, value).apply()
    
    /**
     * Minutos de anticipación para recordatorios (después de la fecha límite)
     */
    var reminderMinutes: Int
        get() = prefs.getInt(KEY_REMINDER_MINUTES, DEFAULT_REMINDER_MINUTES)
        set(value) = prefs.edit().putInt(KEY_REMINDER_MINUTES, value).apply()
    
    /**
     * Obtiene la lista de minutos de anticipación seleccionados
     * Retorna un Set de minutos (ej: [15, 60, 1440])
     */
    fun getAdvanceNotificationMinutesList(): Set<Int> {
        val savedString = prefs.getString(KEY_ADVANCE_NOTIFICATION_MINUTES, DEFAULT_ADVANCE_MINUTES)
        return if (savedString.isNullOrEmpty()) {
            setOf(60) // Por defecto 1 hora
        } else {
            savedString.split(",")
                .mapNotNull { it.toIntOrNull() }
                .toSet()
        }
    }
    
    /**
     * Guarda la lista de minutos de anticipación
     */
    fun setAdvanceNotificationMinutesList(minutes: Set<Int>) {
        val minutesString = minutes.joinToString(",")
        prefs.edit().putString(KEY_ADVANCE_NOTIFICATION_MINUTES, minutesString).apply()
    }
    
    /**
     * Obtiene un texto descriptivo de los tiempos seleccionados
     */
    fun getAdvanceNotificationTimesText(): String {
        val minutes = getAdvanceNotificationMinutesList()
        if (minutes.isEmpty()) return "Ninguno seleccionado"
        if (minutes.size == 1) return formatMinutesToText(minutes.first())
        return "${minutes.size} seleccionados"
    }
    
    /**
     * Formatea minutos a texto legible
     */
    private fun formatMinutesToText(minutes: Int): String {
        return when {
            minutes < 60 -> "$minutes min"
            minutes < 1440 -> "${minutes / 60}h"
            else -> "${minutes / 1440}d"
        }
    }
    
    /**
     * Resetea todas las preferencias a valores por defecto
     */
    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }
}
