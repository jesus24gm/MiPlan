package com.miplan.repositories

import com.miplan.database.DatabaseFactory.dbQuery
import com.miplan.database.Roles
import com.miplan.database.Users
import com.miplan.models.entities.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

/**
 * Repositorio para operaciones de usuario en la base de datos
 */
class UserRepository {
    
    /**
     * Crea un nuevo usuario
     */
    suspend fun create(
        email: String,
        passwordHash: String,
        name: String,
        roleId: Int,
        verificationToken: String?
    ): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.email] = email
            it[Users.passwordHash] = passwordHash
            it[Users.name] = name
            it[Users.roleId] = roleId
            it[Users.isVerified] = false
            it[Users.verificationToken] = verificationToken
            it[Users.createdAt] = LocalDateTime.now()
            it[Users.updatedAt] = LocalDateTime.now()
        }
        
        insertStatement.resultedValues?.singleOrNull()?.let(::rowToUser)
    }
    
    /**
     * Busca un usuario por email
     */
    suspend fun findByEmail(email: String): User? = dbQuery {
        Users.select { Users.email eq email }
            .map(::rowToUser)
            .singleOrNull()
    }
    
    /**
     * Busca un usuario por ID
     */
    suspend fun findById(id: Int): User? = dbQuery {
        Users.select { Users.id eq id }
            .map(::rowToUser)
            .singleOrNull()
    }
    
    /**
     * Busca un usuario por token de verificación
     */
    suspend fun findByVerificationToken(token: String): User? = dbQuery {
        Users.select { Users.verificationToken eq token }
            .map(::rowToUser)
            .singleOrNull()
    }
    
    /**
     * Verifica el email de un usuario
     */
    suspend fun verifyEmail(userId: Int): Boolean = dbQuery {
        Users.update({ Users.id eq userId }) {
            it[isVerified] = true
            it[verificationToken] = null
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }
    
    /**
     * Obtiene todos los usuarios (para admin)
     */
    suspend fun findAll(): List<User> = dbQuery {
        Users.selectAll()
            .map(::rowToUser)
    }
    
    /**
     * Obtiene todos los usuarios con información de rol
     */
    suspend fun getAllUsers(): List<com.miplan.models.User> = dbQuery {
        Users.join(Roles, JoinType.INNER, Users.roleId, Roles.id)
            .selectAll()
            .map { row ->
                com.miplan.models.User(
                    id = row[Users.id],
                    email = row[Users.email],
                    name = row[Users.name],
                    role = row[Roles.name],
                    isVerified = row[Users.isVerified],
                    createdAt = row[Users.createdAt]
                )
            }
    }
    
    /**
     * Cuenta el total de usuarios
     */
    suspend fun countUsers(): Int = dbQuery {
        Users.selectAll().count().toInt()
    }
    
    /**
     * Cuenta usuarios activos (últimos 30 días)
     * Por ahora retorna el total de usuarios verificados
     */
    suspend fun countActiveUsers(): Int = dbQuery {
        Users.select { Users.isVerified eq true }.count().toInt()
    }
    
    /**
     * Actualiza el perfil de un usuario
     */
    suspend fun updateProfile(userId: Int, name: String, email: String): Boolean = dbQuery {
        Users.update({ Users.id eq userId }) {
            it[Users.name] = name
            it[Users.email] = email
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }
    
    /**
     * Actualiza la contraseña de un usuario
     */
    suspend fun updatePassword(userId: Int, passwordHash: String): Boolean = dbQuery {
        Users.update({ Users.id eq userId }) {
            it[Users.passwordHash] = passwordHash
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }
    
    /**
     * Cambia el rol de un usuario (por roleId)
     */
    suspend fun updateRole(userId: Int, roleId: Int): Boolean = dbQuery {
        Users.update({ Users.id eq userId }) {
            it[Users.roleId] = roleId
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }
    
    /**
     * Cambia el rol de un usuario (por nombre de rol)
     */
    suspend fun updateRole(userId: Int, roleName: String): Boolean = dbQuery {
        val role = Roles.select { Roles.name eq roleName }.singleOrNull()
            ?: throw IllegalArgumentException("Rol no encontrado")
        
        Users.update({ Users.id eq userId }) {
            it[Users.roleId] = role[Roles.id]
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }
    
    /**
     * Actualiza el estado activo de un usuario
     * Nota: Por ahora solo actualiza isVerified ya que no hay campo isActive
     */
    suspend fun updateStatus(userId: Int, isActive: Boolean): Boolean = dbQuery {
        Users.update({ Users.id eq userId }) {
            it[isVerified] = isActive
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }
    
    /**
     * Elimina un usuario
     */
    suspend fun delete(userId: Int): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq userId } > 0
    }
    
    /**
     * Obtiene el nombre del rol de un usuario
     */
    suspend fun getUserRoleName(userId: Int): String? = dbQuery {
        Users.join(Roles, JoinType.INNER, Users.roleId, Roles.id)
            .slice(Roles.name)
            .select { Users.id eq userId }
            .map { it[Roles.name] }
            .singleOrNull()
    }
    
    /**
     * Convierte una fila de la base de datos a un objeto User
     */
    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            email = row[Users.email],
            passwordHash = row[Users.passwordHash],
            name = row[Users.name],
            roleId = row[Users.roleId],
            isVerified = row[Users.isVerified],
            verificationToken = row[Users.verificationToken],
            createdAt = row[Users.createdAt],
            updatedAt = row[Users.updatedAt]
        )
    }
}
