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
        verificationToken: String
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
     * Cambia el rol de un usuario
     */
    suspend fun updateRole(userId: Int, roleId: Int): Boolean = dbQuery {
        Users.update({ Users.id eq userId }) {
            it[Users.roleId] = roleId
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
