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
 * Helper para gestionar notificaciones de la app
 */
object NotificationHelper {
    
    // IDs de canales
    const val CHANNEL_TASKS_ID = "tasks_channel"
    const val CHANNEL_CARDS_ID = "cards_channel"
    const val CHANNEL_REMINDERS_ID = "reminders_channel"
    
    // IDs de notificaciones
    const val NOTIFICATION_TASK_BASE_ID = 1000
    const val NOTIFICATION_CARD_BASE_ID = 2000
    const val NOTIFICATION_REMINDER_BASE_ID = 3000
    
    /**
     * Crear canales de notificación
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Canal para tareas
            val tasksChannel = NotificationChannel(
                CHANNEL_TASKS_ID,
                "Tareas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de tareas programadas"
                enableVibration(true)
                enableLights(true)
            }
            
            // Canal para tarjetas
            val cardsChannel = NotificationChannel(
                CHANNEL_CARDS_ID,
                "Tarjetas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de tarjetas con fecha límite"
                enableVibration(true)
                enableLights(true)
            }
            
            // Canal para recordatorios
            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS_ID,
                "Recordatorios",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios de tareas no completadas"
                enableVibration(true)
                enableLights(true)
            }
            
            notificationManager.createNotificationChannel(tasksChannel)
            notificationManager.createNotificationChannel(cardsChannel)
            notificationManager.createNotificationChannel(remindersChannel)
        }
    }
    
    /**
     * Mostrar notificación de tarea
     */
    fun showTaskNotification(
        context: Context,
        taskId: Int,
        title: String,
        description: String?,
        dueDateTime: String?,
        priority: String?,
        isReminder: Boolean = false
    ) {
        val notificationId = if (isReminder) {
            NOTIFICATION_REMINDER_BASE_ID + taskId
        } else {
            NOTIFICATION_TASK_BASE_ID + taskId
        }
        
        val channelId = if (isReminder) CHANNEL_REMINDERS_ID else CHANNEL_TASKS_ID
        
        // Intent para abrir la tarea al tocar la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("taskId", taskId)
            putExtra("openTaskDetail", true)
            action = Intent.ACTION_VIEW
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Emoji de prioridad
        val priorityEmoji = when (priority?.uppercase()) {
            "HIGH" -> "🔴"
            "MEDIUM" -> "🟠"
            "LOW" -> "🟢"
            else -> "⚪"
        }
        
        // Construir contenido
        val contentText = buildString {
            if (dueDateTime != null) {
                append("📅 $dueDateTime")
                if (!description.isNullOrBlank()) append("\n")
            }
            if (!description.isNullOrBlank()) {
                append(description)
            }
        }
        
        val notificationTitle = if (isReminder) {
            "⏰ Recordatorio: $title"
        } else {
            "$priorityEmoji $title"
        }
        
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Necesitarás crear este icono
            .setContentTitle(notificationTitle)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_check,
                "Ver detalles",
                pendingIntent
            )
        
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
    
    /**
     * Mostrar notificación de tarjeta
     */
    fun showCardNotification(
        context: Context,
        cardId: Int,
        title: String,
        dueDateTime: String?,
        boardName: String?,
        isReminder: Boolean = false
    ) {
        val notificationId = if (isReminder) {
            NOTIFICATION_REMINDER_BASE_ID + cardId + 10000
        } else {
            NOTIFICATION_CARD_BASE_ID + cardId
        }
        
        val channelId = if (isReminder) CHANNEL_REMINDERS_ID else CHANNEL_CARDS_ID
        
        // Intent para abrir la tarjeta
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("cardId", cardId)
            putExtra("openCardDetail", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val contentText = buildString {
            if (boardName != null) {
                append("📋 $boardName")
                if (dueDateTime != null) append("\n")
            }
            if (dueDateTime != null) {
                append("📅 $dueDateTime")
            }
        }
        
        val notificationTitle = if (isReminder) {
            "⏰ Recordatorio: $title"
        } else {
            "📌 $title"
        }
        
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notificationTitle)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_check,
                "Ver detalles",
                pendingIntent
            )
        
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
    
    /**
     * Cancelar notificación de tarea
     */
    fun cancelTaskNotification(context: Context, taskId: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(NOTIFICATION_TASK_BASE_ID + taskId)
            cancel(NOTIFICATION_REMINDER_BASE_ID + taskId)
        }
    }
    
    /**
     * Cancelar notificación de tarjeta
     */
    fun cancelCardNotification(context: Context, cardId: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(NOTIFICATION_CARD_BASE_ID + cardId)
            cancel(NOTIFICATION_REMINDER_BASE_ID + cardId + 10000)
        }
    }
    
    /**
     * Mostrar notificación de confirmación al crear tarea con fecha
     */
    fun showTaskCreatedNotification(
        context: Context,
        taskId: Int,
        taskTitle: String,
        dueDate: String?,
        dueTime: String?,
        boardId: Int? = null
    ) {
        val notificationId = NOTIFICATION_TASK_BASE_ID + taskId + 5000
        
        // Intent para abrir la tarea
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("taskId", taskId)
            putExtra("openTaskDetail", true)
            action = Intent.ACTION_VIEW
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Construir mensaje
        val message = buildString {
            append("Has apuntado la tarea \"")
            append(taskTitle)
            append("\"")
            if (dueDate != null) {
                append(" para el día ")
                append(dueDate)
                if (dueTime != null) {
                    append(" a las ")
                    append(dueTime)
                }
            }
            if (boardId != null) {
                append(" que pertenece a un tablero")
            }
        }
        
        val builder = NotificationCompat.Builder(context, CHANNEL_TASKS_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("✅ Tarea creada")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        
        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            // Permisos de notificación no otorgados
        } catch (e: Exception) {
            // Error al mostrar notificación
        }
    }
    
    /**
     * Mostrar notificación de confirmación al crear tarea sin fecha
     */
    fun showTaskCreatedNotificationWithoutDate(
        context: Context,
        taskId: Int,
        taskTitle: String,
        boardId: Int? = null
    ) {
        val notificationId = NOTIFICATION_TASK_BASE_ID + taskId + 5000
        
        // Intent para abrir la tarea
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("taskId", taskId)
            putExtra("openTaskDetail", true)
            action = Intent.ACTION_VIEW
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Construir mensaje
        val message = buildString {
            append("Has apuntado la tarea \"")
            append(taskTitle)
            append("\"")
            if (boardId != null) {
                append(" que pertenece a un tablero")
            }
        }
        
        val builder = NotificationCompat.Builder(context, CHANNEL_TASKS_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("✅ Tarea creada")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        
        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            // Permisos de notificación no otorgados
        } catch (e: Exception) {
            // Error al mostrar notificación
        }
    }
    
    /**
     * Mostrar notificación al actualizar tarea con fecha
     */
    fun showTaskUpdatedNotification(
        context: Context,
        taskId: Int,
        taskTitle: String,
        dueDate: String?,
        dueTime: String?,
        boardId: Int? = null
    ) {
        val notificationId = NOTIFICATION_TASK_BASE_ID + taskId + 6000
        
        // Intent para abrir la tarea
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("taskId", taskId)
            putExtra("openTaskDetail", true)
            action = Intent.ACTION_VIEW
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Construir mensaje
        val message = buildString {
            append("Se ha actualizado la tarea \"")
            append(taskTitle)
            append("\"")
            if (dueDate != null) {
                append(" para el día ")
                append(dueDate)
                if (dueTime != null) {
                    append(" a las ")
                    append(dueTime)
                }
            }
            if (boardId != null) {
                append(" que pertenece a un tablero")
            }
        }
        
        val builder = NotificationCompat.Builder(context, CHANNEL_TASKS_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("📝 Tarea actualizada")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        
        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            // Permisos de notificación no otorgados
        } catch (e: Exception) {
            // Error al mostrar notificación
        }
    }
}
