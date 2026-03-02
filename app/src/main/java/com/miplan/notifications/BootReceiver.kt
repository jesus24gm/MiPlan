package com.miplan.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

/**
 * Receiver para re-programar notificaciones después del reinicio del dispositivo
 */
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Programar trabajo para re-programar todas las notificaciones
            val workRequest = OneTimeWorkRequestBuilder<RescheduleNotificationsWorker>()
                .build()
            
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
