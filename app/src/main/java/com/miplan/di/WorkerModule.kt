package com.miplan.di

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker
import com.miplan.domain.repository.TaskRepository
import com.miplan.domain.repository.CardRepository
import com.miplan.notifications.NotificationWorker
import com.miplan.notifications.RescheduleNotificationsWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo para inyección de dependencias en Workers
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {
    
    @Provides
    @Singleton
    fun provideWorkerFactory(
        taskRepository: TaskRepository,
        cardRepository: CardRepository
    ): WorkerFactory {
        return MiPlanWorkerFactory(taskRepository, cardRepository)
    }
}

/**
 * Factory personalizada para crear Workers con dependencias inyectadas
 */
class MiPlanWorkerFactory(
    private val taskRepository: TaskRepository,
    private val cardRepository: CardRepository
) : WorkerFactory() {
    
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            NotificationWorker::class.java.name -> {
                NotificationWorker(
                    appContext,
                    workerParameters,
                    taskRepository,
                    cardRepository
                )
            }
            RescheduleNotificationsWorker::class.java.name -> {
                RescheduleNotificationsWorker(
                    appContext,
                    workerParameters,
                    taskRepository,
                    cardRepository
                )
            }
            else -> null
        }
    }
}
