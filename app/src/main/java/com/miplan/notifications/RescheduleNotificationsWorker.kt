package com.miplan.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.miplan.domain.repository.TaskRepository
import com.miplan.domain.repository.CardRepository
import com.miplan.domain.model.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Worker para re-programar todas las notificaciones después del reinicio
 */
class RescheduleNotificationsWorker(
    context: Context,
    params: WorkerParameters,
    private val taskRepository: TaskRepository,
    private val cardRepository: CardRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val scheduler = NotificationScheduler(applicationContext)
            
            // Obtener todas las tareas pendientes con fecha límite
            val tasksResult = taskRepository.getTasks()
            tasksResult.onSuccess { tasks ->
                tasks.filter { task ->
                    // Solo tareas pendientes o en progreso con fecha límite
                    task.dueDate != null && 
                    (task.status == TaskStatus.PENDING || task.status == TaskStatus.IN_PROGRESS)
                }.forEach { task ->
                    try {
                        // Parsear la fecha
                        val dueDateTime = parseDateString(task.dueDate!!)
                        
                        // Determinar si tiene hora específica
                        val hasSpecificTime = task.dueDate.contains(":")
                        
                        scheduler.scheduleTaskNotifications(
                            taskId = task.id,
                            dueDate = dueDateTime,
                            hasSpecificTime = hasSpecificTime
                        )
                    } catch (e: Exception) {
                        // Si hay error al parsear la fecha, continuar con la siguiente tarea
                        e.printStackTrace()
                    }
                }
            }
            
            // Nota: No hay método getAllCards() en CardRepository
            // Las tarjetas se obtienen por columna, por lo que no podemos
            // re-programar todas las tarjetas sin conocer todas las columnas.
            // Esto se debería manejar a nivel de tablero.
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
    
    /**
     * Parsea un string de fecha a LocalDateTime
     * Soporta formatos: "yyyy-MM-dd HH:mm:ss" y "yyyy-MM-dd"
     */
    private fun parseDateString(dateString: String): LocalDateTime {
        return try {
            if (dateString.contains(":")) {
                // Formato con hora: "yyyy-MM-dd HH:mm:ss"
                LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            } else {
                // Formato solo fecha: "yyyy-MM-dd"
                LocalDateTime.parse("$dateString 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }
        } catch (e: Exception) {
            // Si falla, intentar con ISO format
            LocalDateTime.parse(dateString)
        }
    }
}
