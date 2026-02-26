package com.miplan.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.miplan.MainActivity
import com.miplan.R

/**
 * Helper para gestionar notificaciones locales de Android
 */
object NotificationHelper {
    
    // IDs de canales de notificación
    private const val CHANNEL_TASK_CREATED = "task_created_channel"
    private const val CHANNEL_TASK_REMINDER = "task_reminder_channel"
    private const val CHANNEL_TASK_ADVANCE = "task_advance_channel"
    
    // IDs de notificación
    private const val NOTIFICATION_TASK_CREATED = 1000
    private const val NOTIFICATION_TASK_REMINDER_BASE = 2000
    private const val NOTIFICATION_TASK_ADVANCE_BASE = 3000
    
    /**
     * Crea los canales de notificación necesarios
     * Debe llamarse al iniciar la app
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Canal para notificaciones de tarea creada
            val createdChannel = NotificationChannel(
                CHANNEL_TASK_CREATED,
                "Tarea creada",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de confirmación al crear tareas"
                enableVibration(true)
            }
            
            // Canal para recordatorios de tareas
            val reminderChannel = NotificationChannel(
                CHANNEL_TASK_REMINDER,
                "Recordatorios de tareas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios cuando llega la fecha límite de una tarea"
                enableVibration(true)
                enableLights(true)
            }
            
            // Canal para notificaciones anticipadas
            val advanceChannel = NotificationChannel(
                CHANNEL_TASK_ADVANCE,
                "Notificaciones anticipadas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones antes de la fecha límite"
                enableVibration(true)
            }
            
            notificationManager.createNotificationChannel(createdChannel)
            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(advanceChannel)
        }
    }
    
    /**
     * Muestra una notificación de confirmación al crear una tarea
     */
    fun showTaskCreatedNotification(
        context: Context,
        taskId: Int,
        taskTitle: String,
        dueDate: String?,
        dueTime: String?
    ) {
        val message = buildTaskCreatedMessage(taskTitle, dueDate, dueTime)
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("taskId", taskId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_TASK_CREATED)
            .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener este icono
            .setContentTitle("✅ Tarea creada")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_TASK_CREATED, notification)
        } catch (e: SecurityException) {
            // El usuario no ha concedido permisos de notificación
            e.printStackTrace()
        }
    }
    
    /**
     * Muestra una notificación de recordatorio de tarea
     */
    fun showTaskReminderNotification(
        context: Context,
        taskId: Int,
        taskTitle: String,
        dueDate: String?,
        dueTime: String?
    ) {
        val message = buildReminderMessage(taskTitle, dueDate, dueTime)
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("taskId", taskId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_TASK_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⏰ Recordatorio de tarea")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_TASK_REMINDER_BASE + taskId,
                notification
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
    
    /**
     * Muestra una notificación anticipada de tarea
     */
    fun showTaskAdvanceNotification(
        context: Context,
        taskId: Int,
        taskTitle: String,
        minutesBefore: Int,
        dueDate: String?,
        dueTime: String?
    ) {
        val timeText = formatMinutesToText(minutesBefore)
        val message = "La tarea \"$taskTitle\" vence en $timeText"
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("taskId", taskId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId * 100 + minutesBefore,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_TASK_ADVANCE)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⏳ Recordatorio anticipado")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_TASK_ADVANCE_BASE + taskId * 100 + minutesBefore,
                notification
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
    
    /**
     * Construye el mensaje para la notificación de tarea creada
     */
    private fun buildTaskCreatedMessage(taskTitle: String, dueDate: String?, dueTime: String?): String {
        return when {
            dueDate == null -> "Has apuntado \"$taskTitle\""
            dueTime == null -> "Has apuntado \"$taskTitle\" para el día $dueDate"
            else -> "Has apuntado \"$taskTitle\" para el día $dueDate a las $dueTime"
        }
    }
    
    /**
     * Construye el mensaje para la notificación de recordatorio
     */
    private fun buildReminderMessage(taskTitle: String, dueDate: String?, dueTime: String?): String {
        return when {
            dueTime != null -> "La tarea \"$taskTitle\" vence hoy a las $dueTime"
            dueDate != null -> "La tarea \"$taskTitle\" vence hoy"
            else -> "Tienes pendiente la tarea \"$taskTitle\""
        }
    }
    
    /**
     * Formatea minutos a texto legible
     */
    private fun formatMinutesToText(minutes: Int): String {
        return when {
            minutes < 60 -> "$minutes minutos"
            minutes < 1440 -> "${minutes / 60} hora${if (minutes / 60 > 1) "s" else ""}"
            else -> "${minutes / 1440} día${if (minutes / 1440 > 1) "s" else ""}"
        }
    }
    
    /**
     * Cancela todas las notificaciones de una tarea
     */
    fun cancelTaskNotifications(context: Context, taskId: Int) {
        val notificationManager = NotificationManagerCompat.from(context)
        
        try {
            // Cancelar notificación de recordatorio
            notificationManager.cancel(NOTIFICATION_TASK_REMINDER_BASE + taskId)
            
            // Cancelar notificaciones anticipadas (probar con los tiempos comunes)
            val commonMinutes = listOf(15, 30, 60, 120, 1440) // 15min, 30min, 1h, 2h, 24h
            commonMinutes.forEach { minutes ->
                notificationManager.cancel(NOTIFICATION_TASK_ADVANCE_BASE + taskId * 100 + minutes)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
