package com.miplan.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "miplan_prefs")

/**
 * Gestor de tokens JWT y preferencias del usuario
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val dataStore = context.dataStore
    
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
    }
    
    /**
     * Guarda el token JWT
     */
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }
    
    /**
     * Obtiene el token JWT
     */
    suspend fun getToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }.first()
    }
    
    /**
     * Flow del token JWT
     */
    val tokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }
    
    /**
     * Guarda información del usuario
     */
    suspend fun saveUserInfo(
        userId: Int,
        email: String,
        name: String,
        role: String
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId.toString()
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_NAME_KEY] = name
            preferences[USER_ROLE_KEY] = role
        }
    }
    
    /**
     * Obtiene el ID del usuario
     */
    suspend fun getUserId(): Int? {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]?.toIntOrNull()
        }.first()
    }
    
    /**
     * Obtiene el email del usuario
     */
    suspend fun getUserEmail(): String? {
        return dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY]
        }.first()
    }
    
    /**
     * Obtiene el nombre del usuario
     */
    suspend fun getUserName(): String? {
        return dataStore.data.map { preferences ->
            preferences[USER_NAME_KEY]
        }.first()
    }
    
    /**
     * Obtiene el rol del usuario
     */
    suspend fun getUserRole(): String? {
        return dataStore.data.map { preferences ->
            preferences[USER_ROLE_KEY]
        }.first()
    }
    
    /**
     * Limpia todos los datos almacenados (logout)
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    /**
     * Verifica si hay un token guardado
     */
    suspend fun hasToken(): Boolean {
        return getToken() != null
    }
}
