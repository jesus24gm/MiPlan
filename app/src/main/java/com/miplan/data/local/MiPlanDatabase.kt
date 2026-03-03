package com.miplan.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.miplan.data.local.dao.BoardDao
import com.miplan.data.local.dao.CardDao
import com.miplan.data.local.dao.ColumnDao
import com.miplan.data.local.dao.TaskDao
import com.miplan.data.local.entity.BoardEntity
import com.miplan.data.local.entity.CardEntity
import com.miplan.data.local.entity.ColumnEntity
import com.miplan.data.local.entity.TaskEntity

/**
 * Base de datos Room de MiPlan
 * Almacena tareas, tableros, columnas y tarjetas localmente
 */
@Database(
    entities = [
        TaskEntity::class,
        BoardEntity::class,
        ColumnEntity::class,
        CardEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MiPlanDatabase : RoomDatabase() {
    
    abstract fun taskDao(): TaskDao
    abstract fun boardDao(): BoardDao
    abstract fun columnDao(): ColumnDao
    abstract fun cardDao(): CardDao
    
    companion object {
        const val DATABASE_NAME = "miplan_database"
    }
}
