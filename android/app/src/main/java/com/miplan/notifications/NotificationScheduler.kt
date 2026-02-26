package com.miplan.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Programa notificaciones futuras usando AlarmManager
 */
object NotificationScheduler {
    
    /**
     * Programa todas las notificaciones para una tarea
     */
    fun scheduleTaskNotifications(
        context: Context,
        taskId: Int,
        taskTitle: String,
        dueDate: String?,
        dueTime: String?
    ) {
        if (dueDate == null) return
        
        val preferences = NotificationPreferences(context)
        
        // No programar si las notificaciones están deshabilitadas
        if (!preferences.notificationsEnabled) return
        
        val dueDateTimeMillis = parseDateTimeToMillis(dueDate, dueTime) ?: return
        val currentTimeMillis = System.currentTimeMillis()
        
        // No programar si la fecha ya pasó
        if (dueDateTimeMillis <= currentTimeMillis) return
        
        // Programar notificaciones anticipadas
        if (preferences.advanceNotificationsEnabled) {
            val advanceMinutes = preferences.getAdvanceNotificationMinutesList()
            advanceMinutes.forEach { minutes ->
                scheduleAdvanceNotification(
                    context,
                    taskId,
                    taskTitle,
                    minutes,
                    dueDateTimeMillis,
                    dueDate,
                    dueTime
                )
            }
        }
        
        // Programar notificación principal (en la fecha límite)
        scheduleReminderNotification(
            context,
            taskId,
            taskTitle,
            dueDateTimeMillis,
            dueDate,
            dueTime
        )
        
        // Programar recordatorio (después de la fecha límite)
        if (preferences.reminderEnabled) {
            val reminderMillis = dueDateTimeMillis + (preferences.reminderMinutes * 60 * 1000)
            scheduleReminderNotification(
                context,
                taskId + 10000, // ID diferente para el recordatorio
                taskTitle,
                reminderMillis,
                dueDate,
                dueTime
            )
        }
    }
    
    /**
     * Programa una notificación anticipada
     */
    private fun scheduleAdvanceNotification(
        context: Context,
        taskId: Int,
        taskTitle: String,
        minutesBefore: Int,
        dueDateTimeMillis: Long,
        dueDate: String,
        dueTime: String?
    ) {
        val notificationTimeMillis = dueDateTimeMillis - (minutesBefore * 60 * 1000)
        val currentTimeMillis = System.currentTimeMillis()
        
        // No programar si el tiempo ya pasó
        if (notificationTimeMillis <= currentTimeMillis) return
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_ADVANCE_NOTIFICATION
            putExtra(NotificationReceiver.EXTRA_TASK_ID, taskId)
            putExtra(NotificationReceiver.EXTRA_TASK_TITLE, taskTitle)
            putExtra(NotificationReceiver.EXTRA_MINUTES_BEFORE, minutesBefore)
            putExtra(NotificationReceiver.EXTRA_DUE_DATE, dueDate)
            putExtra(NotificationReceiver.EXTRA_DUE_TIME, dueTime)
        }
        
        val requestCode = taskId * 100 + minutesBefore
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Usar setExactAndAllowWhileIdle para mayor precisión
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                notificationTimeMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                notificationTimeMillis,
                pendingIntent
            )
        }
    }
    
    /**
     * Programa una notificación de recordatorio
     */
    private fun scheduleReminderNotification(
        context: Context,
        taskId: Int,
        taskTitle: String,
        notificationTimeMillis: Long,
        dueDate: String,
        dueTime: String?
    ) {
        val currentTimeMillis = System.currentTimeMillis()
        
        // No programar si el tiempo ya pasó
        if (notificationTimeMillis <= currentTimeMillis) return
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_REMINDER_NOTIFICATION
            putExtra(NotificationReceiver.EXTRA_TASK_ID, taskId)
            putExtra(NotificationReceiver.EXTRA_TASK_TITLE, taskTitle)
            putExtra(NotificationReceiver.EXTRA_DUE_DATE, dueDate)
            putExtra(NotificationReceiver.EXTRA_DUE_TIME, dueTime)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                notificationTimeMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                notificationTimeMillis,
                pendingIntent
            )
        }
    }
    
    /**
     * Cancela todas las notificaciones programadas para una tarea
     */
    fun cancelTaskNotifications(context: Context, taskId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val preferences = NotificationPreferences(context)
        
        // Cancelar notificaciones anticipadas
        val advanceMinutes = preferences.getAdvanceNotificationMinutesList()
        advanceMinutes.forEach { minutes ->
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = NotificationReceiver.ACTION_ADVANCE_NOTIFICATION
            }
            val requestCode = taskId * 100 + minutes
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
        
        // Cancelar notificación principal
        val reminderIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_REMINDER_NOTIFICATION
        }
        val reminderPendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            reminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(reminderPendingIntent)
        reminderPendingIntent.cancel()
        
        // Cancelar recordatorio posterior
        val laterReminderPendingIntent = PendingIntent.getBroadcast(
            context,
            taskId + 10000,
            reminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(laterReminderPendingIntent)
        laterReminderPendingIntent.cancel()
        
        // Cancelar notificaciones ya mostradas
        NotificationHelper.cancelTaskNotifications(context, taskId)
    }
    
    /**
     * Parsea fecha y hora a milisegundos
     * Formato esperado: fecha "yyyy-MM-dd", hora "HH:mm"
     */
    private fun parseDateTimeToMillis(date: String, time: String?): Long? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val dateTime = if (time != null) {
                    LocalDateTime.parse(
                        "$date $time",
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    )
                } else {
                    LocalDateTime.parse(
                        "$date 09:00", // Hora por defecto si no se especifica
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    )
                }
                dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            } else {
                // Fallback para versiones antiguas de Android
                val parts = date.split("-")
                val year = parts[0].toInt()
                val month = parts[1].toInt() - 1
                val day = parts[2].toInt()
                
                val timeParts = time?.split(":") ?: listOf("09", "00")
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].toInt()
                
                val calendar = java.util.Calendar.getInstance()
                calendar.set(year, month, day, hour, minute, 0)
                calendar.timeInMillis
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
