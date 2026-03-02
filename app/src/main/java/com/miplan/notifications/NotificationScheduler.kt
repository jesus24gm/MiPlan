package com.miplan.notifications

import android.content.Context
import androidx.work.*
import com.miplan.data.preferences.NotificationPreferences
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

/**
 * Gestor para programar notificaciones de tareas y tarjetas
 */
class NotificationScheduler(
    private val context: Context,
    private val preferences: NotificationPreferences? = null
) {
    
    private val workManager = WorkManager.getInstance(context)
    private val prefs = preferences ?: NotificationPreferences(context)
    
    companion object {
        private const val TAG_TASK_PREFIX = "task_notification_"
        private const val TAG_CARD_PREFIX = "card_notification_"
        private const val TAG_REMINDER_SUFFIX = "_reminder"
    }
    
    /**
     * Programar notificaciones para una tarea
     */
    fun scheduleTaskNotifications(
        taskId: Int,
        dueDate: LocalDateTime?,
        hasSpecificTime: Boolean = false
    ) {
        if (dueDate == null) return
        
        // Cancelar notificaciones previas
        cancelTaskNotifications(taskId)
        
        val now = LocalDateTime.now()
        
        // 1. Notificación principal (hora configurada del día o a la hora específica)
        val mainNotificationTime = if (hasSpecificTime) {
            // Si tiene hora, notificar a la hora configurada del mismo día
            dueDate.withHour(prefs.defaultNotificationHour).withMinute(prefs.defaultNotificationMinute)
        } else {
            // Si no tiene hora, notificar a la hora configurada
            dueDate.withHour(prefs.defaultNotificationHour).withMinute(prefs.defaultNotificationMinute)
        }
        
        if (mainNotificationTime.isAfter(now)) {
            scheduleNotification(
                tag = "$TAG_TASK_PREFIX$taskId",
                itemId = taskId,
                type = NotificationWorker.TYPE_TASK,
                scheduledTime = mainNotificationTime,
                isReminder = false
            )
        }
        
        // 2. Notificaciones anticipadas (solo si tiene hora específica y está habilitada)
        if (hasSpecificTime && prefs.advanceNotificationEnabled) {
            val advanceMinutesList = prefs.getAdvanceNotificationMinutesList()
            advanceMinutesList.forEachIndexed { index, minutes ->
                val advanceTime = dueDate.minusMinutes(minutes.toLong())
                if (advanceTime.isAfter(now)) {
                    scheduleNotification(
                        tag = "${TAG_TASK_PREFIX}${taskId}_advance_$index",
                        itemId = taskId,
                        type = NotificationWorker.TYPE_TASK,
                        scheduledTime = advanceTime,
                        isReminder = false
                    )
                }
            }
        }
        
        // 3. Recordatorio después si no está completada
        val reminderTime = if (hasSpecificTime) {
            dueDate.plusHours(prefs.reminderDelayHours.toLong())
        } else {
            // Si no tiene hora, recordatorio a la hora configurada del día siguiente
            dueDate.plusDays(1).withHour(prefs.defaultNotificationHour).withMinute(prefs.defaultNotificationMinute)
        }
        
        if (reminderTime.isAfter(now)) {
            scheduleNotification(
                tag = "$TAG_TASK_PREFIX$taskId$TAG_REMINDER_SUFFIX",
                itemId = taskId,
                type = NotificationWorker.TYPE_TASK,
                scheduledTime = reminderTime,
                isReminder = true
            )
        }
    }
    
    /**
     * Programar notificaciones para una tarjeta
     */
    fun scheduleCardNotifications(
        cardId: Int,
        dueDate: LocalDateTime?,
        hasSpecificTime: Boolean = false
    ) {
        if (dueDate == null) return
        
        // Cancelar notificaciones previas
        cancelCardNotifications(cardId)
        
        val now = LocalDateTime.now()
        
        // 1. Notificación principal
        val mainNotificationTime = if (hasSpecificTime) {
            dueDate.withHour(prefs.defaultNotificationHour).withMinute(prefs.defaultNotificationMinute)
        } else {
            dueDate.withHour(prefs.defaultNotificationHour).withMinute(prefs.defaultNotificationMinute)
        }
        
        if (mainNotificationTime.isAfter(now)) {
            scheduleNotification(
                tag = "$TAG_CARD_PREFIX$cardId",
                itemId = cardId,
                type = NotificationWorker.TYPE_CARD,
                scheduledTime = mainNotificationTime,
                isReminder = false
            )
        }
        
        // 2. Notificaciones anticipadas (solo si tiene hora específica y está habilitada)
        if (hasSpecificTime && prefs.advanceNotificationEnabled) {
            val advanceMinutesList = prefs.getAdvanceNotificationMinutesList()
            advanceMinutesList.forEachIndexed { index, minutes ->
                val advanceTime = dueDate.minusMinutes(minutes.toLong())
                if (advanceTime.isAfter(now)) {
                    scheduleNotification(
                        tag = "${TAG_CARD_PREFIX}${cardId}_advance_$index",
                        itemId = cardId,
                        type = NotificationWorker.TYPE_CARD,
                        scheduledTime = advanceTime,
                        isReminder = false
                    )
                }
            }
        }
        
        // 3. Recordatorio
        val reminderTime = if (hasSpecificTime) {
            dueDate.plusHours(prefs.reminderDelayHours.toLong())
        } else {
            dueDate.plusDays(1).withHour(prefs.defaultNotificationHour).withMinute(prefs.defaultNotificationMinute)
        }
        
        if (reminderTime.isAfter(now)) {
            scheduleNotification(
                tag = "$TAG_CARD_PREFIX$cardId$TAG_REMINDER_SUFFIX",
                itemId = cardId,
                type = NotificationWorker.TYPE_CARD,
                scheduledTime = reminderTime,
                isReminder = true
            )
        }
    }
    
    /**
     * Programar una notificación individual
     */
    private fun scheduleNotification(
        tag: String,
        itemId: Int,
        type: String,
        scheduledTime: LocalDateTime,
        isReminder: Boolean
    ) {
        val now = LocalDateTime.now()
        val delayMillis = ChronoUnit.MILLIS.between(now, scheduledTime)
        
        if (delayMillis <= 0) return
        
        val data = workDataOf(
            NotificationWorker.WORK_TYPE_KEY to type,
            NotificationWorker.ITEM_ID_KEY to itemId,
            NotificationWorker.IS_REMINDER_KEY to isReminder
        )
        
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(tag)
            .build()
        
        workManager.enqueueUniqueWork(
            tag,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    /**
     * Cancelar notificaciones de una tarea
     */
    fun cancelTaskNotifications(taskId: Int) {
        workManager.cancelAllWorkByTag("$TAG_TASK_PREFIX$taskId")
        
        // Cancelar todas las notificaciones anticipadas (hasta 10 posibles)
        for (i in 0..9) {
            workManager.cancelAllWorkByTag("${TAG_TASK_PREFIX}${taskId}_advance_$i")
        }
        
        workManager.cancelAllWorkByTag("$TAG_TASK_PREFIX$taskId$TAG_REMINDER_SUFFIX")
        
        // Cancelar notificación activa
        NotificationHelper.cancelTaskNotification(context, taskId)
    }
    
    /**
     * Cancelar notificaciones de una tarjeta
     */
    fun cancelCardNotifications(cardId: Int) {
        workManager.cancelAllWorkByTag("$TAG_CARD_PREFIX$cardId")
        
        // Cancelar todas las notificaciones anticipadas (hasta 10 posibles)
        for (i in 0..9) {
            workManager.cancelAllWorkByTag("${TAG_CARD_PREFIX}${cardId}_advance_$i")
        }
        
        workManager.cancelAllWorkByTag("$TAG_CARD_PREFIX$cardId$TAG_REMINDER_SUFFIX")
        
        // Cancelar notificación activa
        NotificationHelper.cancelCardNotification(context, cardId)
    }
}
