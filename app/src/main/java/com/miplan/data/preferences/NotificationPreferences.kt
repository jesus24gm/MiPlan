package com.miplan.data.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor de preferencias de notificaciones
 */
@Singleton
class NotificationPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "notification_preferences",
        Context.MODE_PRIVATE
    )
    
    companion object {
        // Keys
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_TASK_NOTIFICATIONS_ENABLED = "task_notifications_enabled"
        private const val KEY_CARD_NOTIFICATIONS_ENABLED = "card_notifications_enabled"
        private const val KEY_REMINDER_NOTIFICATIONS_ENABLED = "reminder_notifications_enabled"
        
        private const val KEY_DEFAULT_NOTIFICATION_HOUR = "default_notification_hour"
        private const val KEY_DEFAULT_NOTIFICATION_MINUTE = "default_notification_minute"
        
        private const val KEY_ADVANCE_NOTIFICATION_ENABLED = "advance_notification_enabled"
        private const val KEY_ADVANCE_NOTIFICATION_MINUTES = "advance_notification_minutes"
        
        private const val KEY_REMINDER_DELAY_HOURS = "reminder_delay_hours"
        
        // Valores por defecto
        private const val DEFAULT_HOUR = 7
        private const val DEFAULT_MINUTE = 0
        private const val DEFAULT_ADVANCE_MINUTES = 60 // 1 hora
        private const val DEFAULT_REMINDER_HOURS = 1
    }
    
    // Notificaciones generales
    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()
    
    var taskNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_TASK_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_TASK_NOTIFICATIONS_ENABLED, value).apply()
    
    var cardNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_CARD_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_CARD_NOTIFICATIONS_ENABLED, value).apply()
    
    var reminderNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMINDER_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER_NOTIFICATIONS_ENABLED, value).apply()
    
    // Hora por defecto para notificaciones
    var defaultNotificationHour: Int
        get() = prefs.getInt(KEY_DEFAULT_NOTIFICATION_HOUR, DEFAULT_HOUR)
        set(value) = prefs.edit().putInt(KEY_DEFAULT_NOTIFICATION_HOUR, value).apply()
    
    var defaultNotificationMinute: Int
        get() = prefs.getInt(KEY_DEFAULT_NOTIFICATION_MINUTE, DEFAULT_MINUTE)
        set(value) = prefs.edit().putInt(KEY_DEFAULT_NOTIFICATION_MINUTE, value).apply()
    
    // Notificación anticipada
    var advanceNotificationEnabled: Boolean
        get() = prefs.getBoolean(KEY_ADVANCE_NOTIFICATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ADVANCE_NOTIFICATION_ENABLED, value).apply()
    
    var advanceNotificationMinutes: Int
        get() = prefs.getInt(KEY_ADVANCE_NOTIFICATION_MINUTES, DEFAULT_ADVANCE_MINUTES)
        set(value) = prefs.edit().putInt(KEY_ADVANCE_NOTIFICATION_MINUTES, value).apply()
    
    /**
     * Obtener lista de tiempos de anticipación seleccionados
     */
    fun getAdvanceNotificationMinutesList(): Set<Int> {
        val savedString = prefs.getString("advance_notification_minutes_list", "60") ?: "60"
        return savedString.split(",").mapNotNull { it.toIntOrNull() }.toSet()
    }
    
    /**
     * Guardar lista de tiempos de anticipación seleccionados
     */
    fun setAdvanceNotificationMinutesList(minutes: Set<Int>) {
        val stringValue = minutes.joinToString(",")
        prefs.edit().putString("advance_notification_minutes_list", stringValue).apply()
    }
    
    // Recordatorio
    var reminderDelayHours: Int
        get() = prefs.getInt(KEY_REMINDER_DELAY_HOURS, DEFAULT_REMINDER_HOURS)
        set(value) = prefs.edit().putInt(KEY_REMINDER_DELAY_HOURS, value).apply()
    
    /**
     * Obtener hora formateada
     */
    fun getFormattedDefaultTime(): String {
        return String.format("%02d:%02d", defaultNotificationHour, defaultNotificationMinute)
    }
    
    /**
     * Restablecer a valores por defecto
     */
    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }
}
