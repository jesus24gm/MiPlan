package com.miplan.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestiona las preferencias de onboarding
 */
@Singleton
class OnboardingPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "onboarding_prefs",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
    
    /**
     * Verifica si el usuario ya completó el onboarding
     */
    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
    
    /**
     * Marca el onboarding como completado
     */
    fun setOnboardingCompleted() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
    }
    
    /**
     * Resetea el onboarding (útil para testing)
     */
    fun resetOnboarding() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, false).apply()
    }
}
