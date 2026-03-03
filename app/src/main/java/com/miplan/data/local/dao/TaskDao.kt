package com.miplan.data.local.dao

import androidx.room.*
import com.miplan.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones de tareas en la base de datos local
 */
@Dao
interface TaskDao {
    
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE localId = :localId AND isDeleted = 0")
    suspend fun getTaskByLocalId(localId: Int): TaskEntity?
    
    @Query("SELECT * FROM tasks WHERE serverId = :serverId AND isDeleted = 0")
    suspend fun getTaskByServerId(serverId: Int): TaskEntity?
    
    @Query("SELECT * FROM tasks WHERE status = :status AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getTasksByStatus(status: String): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE boardId = :boardId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getTasksByBoard(boardId: Int): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE dueDate LIKE :date || '%' AND isDeleted = 0 ORDER BY dueDate ASC")
    fun getTasksByDate(date: String): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedTasks(): List<TaskEntity>
    
    @Query("SELECT * FROM tasks WHERE isDeleted = 1")
    suspend fun getDeletedTasks(): List<TaskEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)
    
    @Update
    suspend fun updateTask(task: TaskEntity)
    
    @Query("UPDATE tasks SET serverId = :serverId, isSynced = 1 WHERE localId = :localId")
    suspend fun updateServerIdAndSync(localId: Int, serverId: Int)
    
    @Query("UPDATE tasks SET isSynced = :isSynced WHERE localId = :localId")
    suspend fun updateSyncStatus(localId: Int, isSynced: Boolean)
    
    @Query("UPDATE tasks SET isDeleted = 1, isSynced = 0 WHERE localId = :localId")
    suspend fun softDelete(localId: Int)
    
    @Query("UPDATE tasks SET isDeleted = 1, isSynced = 0 WHERE serverId = :serverId")
    suspend fun softDeleteByServerId(serverId: Int)
    
    @Query("DELETE FROM tasks WHERE localId = :localId")
    suspend fun hardDelete(localId: Int)
    
    @Query("DELETE FROM tasks WHERE isDeleted = 1 AND isSynced = 1")
    suspend fun cleanupDeletedSyncedTasks()
    
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
