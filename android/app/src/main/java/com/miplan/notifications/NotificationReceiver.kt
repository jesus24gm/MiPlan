package com.miplan.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Receptor de alarmas programadas para mostrar notificaciones
 */
class NotificationReceiver : BroadcastReceiver() {
    
    companion object {
        const val ACTION_ADVANCE_NOTIFICATION = "com.miplan.ACTION_ADVANCE_NOTIFICATION"
        const val ACTION_REMINDER_NOTIFICATION = "com.miplan.ACTION_REMINDER_NOTIFICATION"
        
        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_TASK_TITLE = "task_title"
        const val EXTRA_MINUTES_BEFORE = "minutes_before"
        const val EXTRA_DUE_DATE = "due_date"
        const val EXTRA_DUE_TIME = "due_time"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: return
        val dueDate = intent.getStringExtra(EXTRA_DUE_DATE)
        val dueTime = intent.getStringExtra(EXTRA_DUE_TIME)
        
        if (taskId == -1) return
        
        when (intent.action) {
            ACTION_ADVANCE_NOTIFICATION -> {
                val minutesBefore = intent.getIntExtra(EXTRA_MINUTES_BEFORE, 0)
                NotificationHelper.showTaskAdvanceNotification(
                    context,
                    taskId,
                    taskTitle,
                    minutesBefore,
                    dueDate,
                    dueTime
                )
            }
            
            ACTION_REMINDER_NOTIFICATION -> {
                NotificationHelper.showTaskReminderNotification(
                    context,
                    taskId,
                    taskTitle,
                    dueDate,
                    dueTime
                )
            }
        }
    }
}
