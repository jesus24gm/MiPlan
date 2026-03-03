package com.miplan.data.local.dao

import androidx.room.*
import com.miplan.data.local.entity.CardEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones de tarjetas en la base de datos local
 */
@Dao
interface CardDao {
    
    @Query("SELECT * FROM cards WHERE columnLocalId = :columnLocalId AND isDeleted = 0 ORDER BY position ASC")
    fun getCardsByColumnLocalId(columnLocalId: Int): Flow<List<CardEntity>>
    
    @Query("SELECT * FROM cards WHERE columnServerId = :columnServerId AND isDeleted = 0 ORDER BY position ASC")
    fun getCardsByColumnServerId(columnServerId: Int): Flow<List<CardEntity>>
    
    @Query("SELECT * FROM cards WHERE localId = :localId AND isDeleted = 0")
    suspend fun getCardByLocalId(localId: Int): CardEntity?
    
    @Query("SELECT * FROM cards WHERE serverId = :serverId AND isDeleted = 0")
    suspend fun getCardByServerId(serverId: Int): CardEntity?
    
    @Query("SELECT * FROM cards WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedCards(): List<CardEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardEntity>)
    
    @Update
    suspend fun updateCard(card: CardEntity)
    
    @Query("UPDATE cards SET serverId = :serverId, isSynced = 1 WHERE localId = :localId")
    suspend fun updateServerIdAndSync(localId: Int, serverId: Int)
    
    @Query("UPDATE cards SET columnLocalId = :newColumnLocalId, position = :newPosition, isSynced = 0 WHERE localId = :localId")
    suspend fun moveCard(localId: Int, newColumnLocalId: Int, newPosition: Int)
    
    @Query("UPDATE cards SET isDeleted = 1, isSynced = 0 WHERE localId = :localId")
    suspend fun softDelete(localId: Int)
    
    @Query("DELETE FROM cards WHERE localId = :localId")
    suspend fun hardDelete(localId: Int)
    
    @Query("DELETE FROM cards WHERE isDeleted = 1 AND isSynced = 1")
    suspend fun cleanupDeletedSyncedCards()
}
