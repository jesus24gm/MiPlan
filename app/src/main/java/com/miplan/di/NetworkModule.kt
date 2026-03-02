package com.miplan.di

import com.miplan.data.local.TokenManager
import com.miplan.data.remote.ApiConfig
import com.miplan.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

/**
 * Módulo de red y API
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideApiConfig(tokenManager: TokenManager): ApiConfig {
        return ApiConfig(tokenManager)
    }
    
    @Provides
    @Singleton
    fun provideHttpClient(apiConfig: ApiConfig): HttpClient {
        return apiConfig.createHttpClient()
    }
    
    @Provides
    @Singleton
    fun provideApiService(httpClient: HttpClient): ApiService {
        return ApiService(httpClient)
    }
}
