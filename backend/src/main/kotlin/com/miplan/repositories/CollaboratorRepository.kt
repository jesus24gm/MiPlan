package com.miplan.repositories

import com.miplan.database.TaskCollaborators
import com.miplan.database.Users
import com.miplan.models.entities.CollaboratorRole
import com.miplan.models.entities.TaskCollaborator
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * Repositorio para gestionar colaboradores de tareas
 */
class CollaboratorRepository {
    
    /**
     * Agrega un colaborador a una tarea
     */
    fun addCollaborator(
        taskId: Int,
        userId: Int,
        role: CollaboratorRole,
        addedBy: Int
    ): TaskCollaborator? {
        return transaction {
            // Verificar que no exista ya
            val exists = TaskCollaborators.select {
                (TaskCollaborators.taskId eq taskId) and (TaskCollaborators.userId eq userId)
            }.count() > 0
            
            if (exists) {
                return@transaction null
            }
            
            val now = LocalDateTime.now()
            
            TaskCollaborators.insert {
                it[TaskCollaborators.taskId] = taskId
                it[TaskCollaborators.userId] = userId
                it[TaskCollaborators.role] = role.name
                it[addedAt] = now
                it[TaskCollaborators.addedBy] = addedBy
            }
            
            getCollaborator(taskId, userId)
        }
    }
    
    /**
     * Obtiene un colaborador específico
     */
    fun getCollaborator(taskId: Int, userId: Int): TaskCollaborator? {
        return transaction {
            (TaskCollaborators innerJoin Users)
                .select {
                    (TaskCollaborators.taskId eq taskId) and (TaskCollaborators.userId eq userId)
                }
                .map { rowToCollaborator(it) }
                .singleOrNull()
        }
    }
    
    /**
     * Obtiene todos los colaboradores de una tarea
     */
    fun getCollaboratorsByTask(taskId: Int): List<TaskCollaborator> {
        return transaction {
            (TaskCollaborators innerJoin Users)
                .select { TaskCollaborators.taskId eq taskId }
                .map { rowToCollaborator(it) }
        }
    }
    
    /**
     * Obtiene todas las tareas en las que un usuario es colaborador
     */
    fun getTasksByCollaborator(userId: Int): List<Int> {
        return transaction {
            TaskCollaborators
                .select { TaskCollaborators.userId eq userId }
                .map { it[TaskCollaborators.taskId] }
        }
    }
    
    /**
     * Verifica si un usuario es colaborador de una tarea
     */
    fun isCollaborator(taskId: Int, userId: Int): Boolean {
        return transaction {
            TaskCollaborators.select {
                (TaskCollaborators.taskId eq taskId) and (TaskCollaborators.userId eq userId)
            }.count() > 0
        }
    }
    
    /**
     * Obtiene el rol de un colaborador
     */
    fun getCollaboratorRole(taskId: Int, userId: Int): CollaboratorRole? {
        return transaction {
            TaskCollaborators.select {
                (TaskCollaborators.taskId eq taskId) and (TaskCollaborators.userId eq userId)
            }
                .map { CollaboratorRole.valueOf(it[TaskCollaborators.role]) }
                .singleOrNull()
        }
    }
    
    /**
     * Actualiza el rol de un colaborador
     */
    fun updateCollaboratorRole(taskId: Int, userId: Int, newRole: CollaboratorRole): Boolean {
        return transaction {
            val updated = TaskCollaborators.update({
                (TaskCollaborators.taskId eq taskId) and (TaskCollaborators.userId eq userId)
            }) {
                it[role] = newRole.name
            }
            updated > 0
        }
    }
    
    /**
     * Elimina un colaborador de una tarea
     */
    fun removeCollaborator(taskId: Int, userId: Int): Boolean {
        return transaction {
            val deleted = TaskCollaborators.deleteWhere {
                (TaskCollaborators.taskId eq taskId) and (TaskCollaborators.userId eq userId)
            }
            deleted > 0
        }
    }
    
    /**
     * Elimina todos los colaboradores de una tarea
     */
    fun removeAllCollaborators(taskId: Int): Int {
        return transaction {
            TaskCollaborators.deleteWhere {
                TaskCollaborators.taskId eq taskId
            }
        }
    }
    
    /**
     * Convierte una fila de base de datos a TaskCollaborator
     */
    private fun rowToCollaborator(row: ResultRow): TaskCollaborator {
        return TaskCollaborator(
            taskId = row[TaskCollaborators.taskId],
            userId = row[TaskCollaborators.userId],
            userName = row[Users.name],
            userEmail = row[Users.email],
            userAvatarUrl = row[Users.avatarUrl],
            role = CollaboratorRole.valueOf(row[TaskCollaborators.role]),
            addedAt = row[TaskCollaborators.addedAt],
            addedBy = row[TaskCollaborators.addedBy]
        )
    }
}
