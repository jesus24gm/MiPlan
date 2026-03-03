package com.miplan.data.local.dao

import androidx.room.*
import com.miplan.data.local.entity.BoardEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones de tableros en la base de datos local
 */
@Dao
interface BoardDao {
    
    @Query("SELECT * FROM boards WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllBoards(): Flow<List<BoardEntity>>
    
    @Query("SELECT * FROM boards WHERE localId = :localId AND isDeleted = 0")
    suspend fun getBoardByLocalId(localId: Int): BoardEntity?
    
    @Query("SELECT * FROM boards WHERE serverId = :serverId AND isDeleted = 0")
    suspend fun getBoardByServerId(serverId: Int): BoardEntity?
    
    @Query("SELECT * FROM boards WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedBoards(): List<BoardEntity>
    
    @Query("SELECT * FROM boards WHERE isDeleted = 1")
    suspend fun getDeletedBoards(): List<BoardEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoard(board: BoardEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoards(boards: List<BoardEntity>)
    
    @Update
    suspend fun updateBoard(board: BoardEntity)
    
    @Query("UPDATE boards SET serverId = :serverId, isSynced = 1 WHERE localId = :localId")
    suspend fun updateServerIdAndSync(localId: Int, serverId: Int)
    
    @Query("UPDATE boards SET isSynced = :isSynced WHERE localId = :localId")
    suspend fun updateSyncStatus(localId: Int, isSynced: Boolean)
    
    @Query("UPDATE boards SET isDeleted = 1, isSynced = 0 WHERE localId = :localId")
    suspend fun softDelete(localId: Int)
    
    @Query("UPDATE boards SET isDeleted = 1, isSynced = 0 WHERE serverId = :serverId")
    suspend fun softDeleteByServerId(serverId: Int)
    
    @Query("DELETE FROM boards WHERE localId = :localId")
    suspend fun hardDelete(localId: Int)
    
    @Query("DELETE FROM boards WHERE isDeleted = 1 AND isSynced = 1")
    suspend fun cleanupDeletedSyncedBoards()
    
    @Query("DELETE FROM boards")
    suspend fun deleteAllBoards()
}
