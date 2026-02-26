package com.miplan.services

import com.miplan.models.entities.CollaboratorRole
import com.miplan.models.entities.TaskCollaborator
import com.miplan.models.responses.CollaboratorResponse
import com.miplan.repositories.CollaboratorRepository
import com.miplan.repositories.TaskRepository
import com.miplan.repositories.UserRepository
import java.time.format.DateTimeFormatter

/**
 * Servicio para gestionar colaboradores de tareas
 */
class CollaboratorService(
    private val collaboratorRepository: CollaboratorRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val notificationService: NotificationService
) {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    /**
     * Verifica si un usuario tiene permiso para realizar una acción en una tarea
     */
    fun hasPermission(taskId: Int, userId: Int, requiredRole: CollaboratorRole): Boolean {
        // Verificar si es el creador de la tarea
        val task = taskRepository.getTaskById(taskId) ?: return false
        if (task.createdBy == userId) return true
        
        // Verificar rol de colaborador
        val role = collaboratorRepository.getCollaboratorRole(taskId, userId) ?: return false
        
        return when (requiredRole) {
            CollaboratorRole.OWNER -> role == CollaboratorRole.OWNER
            CollaboratorRole.EDITOR -> role == CollaboratorRole.OWNER || role == CollaboratorRole.EDITOR
            CollaboratorRole.VIEWER -> true // Cualquier colaborador puede ver
        }
    }
    
    /**
     * Verifica si un usuario puede ver una tarea
     */
    fun canViewTask(taskId: Int, userId: Int): Boolean {
        val task = taskRepository.getTaskById(taskId) ?: return false
        
        // El creador siempre puede ver
        if (task.createdBy == userId) return true
        
        // Verificar si es colaborador
        return collaboratorRepository.isCollaborator(taskId, userId)
    }
    
    /**
     * Verifica si un usuario puede editar una tarea
     */
    fun canEditTask(taskId: Int, userId: Int): Boolean {
        val task = taskRepository.getTaskById(taskId) ?: return false
        
        // El creador siempre puede editar
        if (task.createdBy == userId) return true
        
        // Verificar si tiene rol de EDITOR u OWNER
        return hasPermission(taskId, userId, CollaboratorRole.EDITOR)
    }
    
    /**
     * Verifica si un usuario puede gestionar colaboradores
     */
    fun canManageCollaborators(taskId: Int, userId: Int): Boolean {
        val task = taskRepository.getTaskById(taskId) ?: return false
        
        // Solo el creador puede gestionar colaboradores
        return task.createdBy == userId
    }
    
    /**
     * Agrega un colaborador a una tarea
     */
    suspend fun addCollaborator(
        taskId: Int,
        userEmail: String,
        role: CollaboratorRole,
        addedByUserId: Int
    ): Result<CollaboratorResponse> {
        return try {
            // Verificar que la tarea existe
            val task = taskRepository.getTaskById(taskId)
                ?: return Result.failure(Exception("Tarea no encontrada"))
            
            // Verificar permisos
            if (!canManageCollaborators(taskId, addedByUserId)) {
                return Result.failure(Exception("No tienes permisos para agregar colaboradores"))
            }
            
            // Buscar usuario por email
            val user = userRepository.getUserByEmail(userEmail)
                ?: return Result.failure(Exception("Usuario no encontrado"))
            
            // No se puede agregar al creador como colaborador
            if (user.id == task.createdBy) {
                return Result.failure(Exception("El creador de la tarea ya tiene acceso completo"))
            }
            
            // Agregar colaborador
            val collaborator = collaboratorRepository.addCollaborator(
                taskId = taskId,
                userId = user.id,
                role = role,
                addedBy = addedByUserId
            ) ?: return Result.failure(Exception("El usuario ya es colaborador de esta tarea"))
            
            // Enviar notificación
            notificationService.createNotification(
                userId = user.id,
                taskId = taskId,
                message = "Has sido agregado como colaborador en la tarea: ${task.title}",
                type = "TASK_SHARED"
            )
            
            Result.success(collaborator.toResponse())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene todos los colaboradores de una tarea
     */
    fun getCollaborators(taskId: Int, requestUserId: Int): Result<List<CollaboratorResponse>> {
        return try {
            // Verificar que el usuario puede ver la tarea
            if (!canViewTask(taskId, requestUserId)) {
                return Result.failure(Exception("No tienes permisos para ver esta tarea"))
            }
            
            val collaborators = collaboratorRepository.getCollaboratorsByTask(taskId)
            Result.success(collaborators.map { it.toResponse() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene las tareas compartidas con un usuario
     */
    fun getSharedTasks(userId: Int): Result<List<Int>> {
        return try {
            val taskIds = collaboratorRepository.getTasksByCollaborator(userId)
            Result.success(taskIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Actualiza el rol de un colaborador
     */
    fun updateCollaboratorRole(
        taskId: Int,
        collaboratorUserId: Int,
        newRole: CollaboratorRole,
        requestUserId: Int
    ): Result<Unit> {
        return try {
            // Verificar permisos
            if (!canManageCollaborators(taskId, requestUserId)) {
                return Result.failure(Exception("No tienes permisos para gestionar colaboradores"))
            }
            
            // No se puede cambiar el rol del creador
            val task = taskRepository.getTaskById(taskId)
                ?: return Result.failure(Exception("Tarea no encontrada"))
            
            if (collaboratorUserId == task.createdBy) {
                return Result.failure(Exception("No se puede cambiar el rol del creador"))
            }
            
            val updated = collaboratorRepository.updateCollaboratorRole(taskId, collaboratorUserId, newRole)
            if (updated) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Colaborador no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Elimina un colaborador de una tarea
     */
    fun removeCollaborator(
        taskId: Int,
        collaboratorUserId: Int,
        requestUserId: Int
    ): Result<Unit> {
        return try {
            // Verificar permisos
            if (!canManageCollaborators(taskId, requestUserId)) {
                return Result.failure(Exception("No tienes permisos para gestionar colaboradores"))
            }
            
            val removed = collaboratorRepository.removeCollaborator(taskId, collaboratorUserId)
            if (removed) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Colaborador no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Convierte TaskCollaborator a CollaboratorResponse
     */
    private fun TaskCollaborator.toResponse(): CollaboratorResponse {
        return CollaboratorResponse(
            taskId = taskId,
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            userAvatarUrl = userAvatarUrl,
            role = role.name,
            addedAt = addedAt.format(dateFormatter),
            addedBy = addedBy
        )
    }
}
