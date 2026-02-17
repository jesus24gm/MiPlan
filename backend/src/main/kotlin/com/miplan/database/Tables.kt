package com.miplan.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Definición de tablas con Exposed ORM
 */

object Roles : Table("roles") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50).uniqueIndex()
    val description = varchar("description", 255).nullable()
    
    override val primaryKey = PrimaryKey(id)
}

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val name = varchar("name", 255)
    val roleId = integer("role_id").references(Roles.id)
    val isVerified = bool("is_verified").default(false)
    val verificationToken = varchar("verification_token", 255).nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    
    override val primaryKey = PrimaryKey(id)
}

object Boards : Table("boards") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val color = varchar("color", 20).default("#E3F2FD")
    val userId = integer("user_id").references(Users.id)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    
    override val primaryKey = PrimaryKey(id)
}

object Tasks : Table("tasks") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val status = varchar("status", 50).default("PENDING")
    val priority = varchar("priority", 50).default("MEDIUM")
    val dueDate = datetime("due_date").nullable()
    val boardId = integer("board_id").references(Boards.id).nullable()
    val createdBy = integer("created_by").references(Users.id)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    
    override val primaryKey = PrimaryKey(id)
}

object Notifications : Table("notifications") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val taskId = integer("task_id").references(Tasks.id).nullable()
    val message = text("message")
    val type = varchar("type", 50).default("SYSTEM")
    val isRead = bool("is_read").default(false)
    val createdAt = datetime("created_at")
    
    override val primaryKey = PrimaryKey(id)
}

object UserTasks : Table("user_tasks") {
    val userId = integer("user_id").references(Users.id)
    val taskId = integer("task_id").references(Tasks.id)
    val permission = varchar("permission", 20).default("view")
    val assignedAt = datetime("assigned_at")
    
    override val primaryKey = PrimaryKey(userId, taskId)
}
