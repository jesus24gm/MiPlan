package com.miplan.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.miplan.domain.repository.TaskRepository
import com.miplan.domain.repository.CardRepository
import com.miplan.domain.model.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Worker para ejecutar notificaciones programadas
 */
class NotificationWorker(
    context: Context,
    params: WorkerParameters,
    private val taskRepository: TaskRepository,
    private val cardRepository: CardRepository
) : CoroutineWorker(context, params) {
    
    companion object {
        const val WORK_TYPE_KEY = "work_type"
        const val ITEM_ID_KEY = "item_id"
        const val IS_REMINDER_KEY = "is_reminder"
        
        const val TYPE_TASK = "task"
        const val TYPE_CARD = "card"
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val workType = inputData.getString(WORK_TYPE_KEY) ?: return@withContext Result.failure()
            val itemId = inputData.getInt(ITEM_ID_KEY, -1)
            val isReminder = inputData.getBoolean(IS_REMINDER_KEY, false)
            
            if (itemId == -1) return@withContext Result.failure()
            
            when (workType) {
                TYPE_TASK -> handleTaskNotification(itemId, isReminder)
                TYPE_CARD -> handleCardNotification(itemId, isReminder)
                else -> return@withContext Result.failure()
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    private suspend fun handleTaskNotification(taskId: Int, isReminder: Boolean) {
        try {
            val task = taskRepository.getTaskById(taskId).getOrNull()
            
            // Si no existe la tarea o está completada/cancelada, no mostrar notificación
            if (task == null || 
                task.status == TaskStatus.COMPLETED || 
                task.status == TaskStatus.CANCELLED) {
                return
            }
            
            // Formatear fecha y hora
            val formattedDateTime = task.dueDate?.let { formatDateTime(it) }
            
            // Mostrar notificación con datos reales
            NotificationHelper.showTaskNotification(
                context = applicationContext,
                taskId = taskId,
                title = task.title,
                description = task.description,
                dueDateTime = formattedDateTime,
                priority = task.priority.displayName,
                isReminder = isReminder
            )
        } catch (e: Exception) {
            // Si hay error, mostrar notificación básica
            NotificationHelper.showTaskNotification(
                context = applicationContext,
                taskId = taskId,
                title = "Tarea pendiente",
                description = null,
                dueDateTime = null,
                priority = null,
                isReminder = isReminder
            )
        }
    }
    
    private suspend fun handleCardNotification(cardId: Int, isReminder: Boolean) {
        try {
            val card = cardRepository.getCardById(cardId).getOrNull()
            
            // Si no existe la tarjeta, no mostrar notificación
            if (card == null) {
                return
            }
            
            // Formatear fecha y hora
            val formattedDateTime = card.dueDate?.let { formatDateTime(it) }
            
            // Mostrar notificación con datos reales
            NotificationHelper.showCardNotification(
                context = applicationContext,
                cardId = cardId,
                title = card.title,
                dueDateTime = formattedDateTime,
                boardName = null,
                isReminder = isReminder
            )
        } catch (e: Exception) {
            // Si hay error, mostrar notificación básica
            NotificationHelper.showCardNotification(
                context = applicationContext,
                cardId = cardId,
                title = "Tarjeta pendiente",
                dueDateTime = null,
                boardName = null,
                isReminder = isReminder
            )
        }
    }
    
    /**
     * Formatea una fecha/hora para mostrar en la notificación
     */
    private fun formatDateTime(dateTimeString: String): String {
        return try {
            val inputFormat = when {
                dateTimeString.contains("T") -> SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                dateTimeString.contains(" ") -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                else -> SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            }
            
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateTimeString)
            date?.let { outputFormat.format(it) } ?: dateTimeString
        } catch (e: Exception) {
            dateTimeString
        }
    }
}
