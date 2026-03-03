package com.miplan.di

import android.content.Context
import androidx.room.Room
import com.miplan.data.local.MiPlanDatabase
import com.miplan.data.local.dao.BoardDao
import com.miplan.data.local.dao.CardDao
import com.miplan.data.local.dao.ColumnDao
import com.miplan.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt para proveer la base de datos Room y DAOs
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideMiPlanDatabase(
        @ApplicationContext context: Context
    ): MiPlanDatabase {
        return Room.databaseBuilder(
            context,
            MiPlanDatabase::class.java,
            MiPlanDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // En producción, usar migraciones apropiadas
            .build()
    }
    
    @Provides
    @Singleton
    fun provideTaskDao(database: MiPlanDatabase): TaskDao {
        return database.taskDao()
    }
    
    @Provides
    @Singleton
    fun provideBoardDao(database: MiPlanDatabase): BoardDao {
        return database.boardDao()
    }
    
    @Provides
    @Singleton
    fun provideColumnDao(database: MiPlanDatabase): ColumnDao {
        return database.columnDao()
    }
    
    @Provides
    @Singleton
    fun provideCardDao(database: MiPlanDatabase): CardDao {
        return database.cardDao()
    }
}
