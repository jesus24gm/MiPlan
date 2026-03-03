package com.miplan.data.local.dao

import androidx.room.*
import com.miplan.data.local.entity.ColumnEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones de columnas en la base de datos local
 */
@Dao
interface ColumnDao {
    
    @Query("SELECT * FROM columns WHERE boardLocalId = :boardLocalId AND isDeleted = 0 ORDER BY position ASC")
    fun getColumnsByBoardLocalId(boardLocalId: Int): Flow<List<ColumnEntity>>
    
    @Query("SELECT * FROM columns WHERE boardServerId = :boardServerId AND isDeleted = 0 ORDER BY position ASC")
    fun getColumnsByBoardServerId(boardServerId: Int): Flow<List<ColumnEntity>>
    
    @Query("SELECT * FROM columns WHERE localId = :localId AND isDeleted = 0")
    suspend fun getColumnByLocalId(localId: Int): ColumnEntity?
    
    @Query("SELECT * FROM columns WHERE serverId = :serverId AND isDeleted = 0")
    suspend fun getColumnByServerId(serverId: Int): ColumnEntity?
    
    @Query("SELECT * FROM columns WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedColumns(): List<ColumnEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColumn(column: ColumnEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColumns(columns: List<ColumnEntity>)
    
    @Update
    suspend fun updateColumn(column: ColumnEntity)
    
    @Query("UPDATE columns SET serverId = :serverId, isSynced = 1 WHERE localId = :localId")
    suspend fun updateServerIdAndSync(localId: Int, serverId: Int)
    
    @Query("UPDATE columns SET isDeleted = 1, isSynced = 0 WHERE localId = :localId")
    suspend fun softDelete(localId: Int)
    
    @Query("DELETE FROM columns WHERE localId = :localId")
    suspend fun hardDelete(localId: Int)
    
    @Query("DELETE FROM columns WHERE isDeleted = 1 AND isSynced = 1")
    suspend fun cleanupDeletedSyncedColumns()
}
