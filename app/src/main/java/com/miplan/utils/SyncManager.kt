package com.miplan.utils

import android.content.Context
import androidx.work.*
import com.miplan.workers.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager para programar sincronización periódica de datos
 */
@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val workManager = WorkManager.getInstance(context)
    
    companion object {
        private const val SYNC_WORK_NAME = "miplan_sync_work"
        private const val SYNC_INTERVAL_MINUTES = 30L
    }
    
    /**
     * Programa sincronización periódica cada 30 minutos
     */
    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Solo con internet
            .build()
        
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            SYNC_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
    
    /**
     * Fuerza una sincronización inmediata
     */
    fun syncNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()
        
        workManager.enqueue(syncRequest)
    }
    
    /**
     * Cancela la sincronización periódica
     */
    fun cancelSync() {
        workManager.cancelUniqueWork(SYNC_WORK_NAME)
    }
}
