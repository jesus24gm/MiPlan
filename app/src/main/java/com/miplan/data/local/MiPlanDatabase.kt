package com.miplan.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 2,
    exportSchema = false
)
abstract class MiPlanDatabase : RoomDatabase() {
    
    abstract fun taskDao(): TaskDao
    abstract fun boardDao(): BoardDao
    abstract fun columnDao(): ColumnDao
    abstract fun cardDao(): CardDao
    
    companion object {
        const val DATABASE_NAME = "miplan_database"
        
        /**
         * Migración de versión 1 a 2: Añadir campos de recurrencia a TaskEntity
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Añadir campos de recurrencia a la tabla tasks
                database.execSQL("ALTER TABLE tasks ADD COLUMN recurrenceType TEXT NOT NULL DEFAULT 'NONE'")
                database.execSQL("ALTER TABLE tasks ADD COLUMN recurrenceInterval INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE tasks ADD COLUMN recurrenceDays TEXT")
                database.execSQL("ALTER TABLE tasks ADD COLUMN recurrenceEndDate TEXT")
                database.execSQL("ALTER TABLE tasks ADD COLUMN isRecurringInstance INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE tasks ADD COLUMN parentTaskId INTEGER")
            }
        }
    }
}
