package com.miplan.data.remote

import com.miplan.data.local.TokenManager
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Configuración del cliente HTTP Ktor
 */
@Singleton
class ApiConfig @Inject constructor(
    private val tokenManager: TokenManager
) {
    
    companion object {
        const val BASE_URL = "https://miplan-production.up.railway.app"
        private const val TIMEOUT_MILLIS = 30_000L
    }
    
    fun createHttpClient(): HttpClient {
        return HttpClient(Android) {
            // Configuración para no lanzar excepciones automáticamente
            expectSuccess = false
            
            // URL base y headers por defecto
            defaultRequest {
                url(BASE_URL)
                contentType(ContentType.Application.Json)
                
                // Agregar token JWT si existe
                val token = runBlocking { tokenManager.getToken() }
                if (token != null) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            
            // Timeout
            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT_MILLIS
                connectTimeoutMillis = TIMEOUT_MILLIS
                socketTimeoutMillis = TIMEOUT_MILLIS
            }
            
            // Content Negotiation (JSON)
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            
            // Logging
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }
            
            // Manejo de respuestas
            install(HttpCallValidator) {
                handleResponseExceptionWithRequest { exception, _ ->
                    val clientException = exception as? ClientRequestException
                        ?: return@handleResponseExceptionWithRequest
                    
                    when (clientException.response.status) {
                        HttpStatusCode.Unauthorized -> {
                            // Token inválido o expirado
                            runBlocking {
                                tokenManager.clearAll()
                            }
                        }
                        else -> {
                            // Otros errores
                        }
                    }
                }
            }
        }
    }
}
