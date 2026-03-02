package com.miplan.data.remote

import com.miplan.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio para buscar imágenes en Unsplash
 */
@Singleton
class UnsplashService @Inject constructor() {
    
    private val api: UnsplashApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        
        Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UnsplashApi::class.java)
    }
    
    /**
     * Busca fotos en Unsplash
     * @param query Término de búsqueda
     * @param page Página de resultados
     * @return Lista de fotos o error
     */
    suspend fun searchPhotos(
        query: String,
        page: Int = 1
    ): Result<List<UnsplashPhoto>> {
        return try {
            println("🔍 Buscando en Unsplash: $query")
            
            val response = api.searchPhotos(
                query = query,
                page = page,
                perPage = 20,
                clientId = BuildConfig.UNSPLASH_ACCESS_KEY
            )
            
            if (response.isSuccessful && response.body() != null) {
                val photos = response.body()!!.results
                println("✅ Encontradas ${photos.size} fotos")
                Result.success(photos)
            } else {
                val error = "Error ${response.code()}: ${response.message()}"
                println("❌ $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            println("❌ Error buscando en Unsplash: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
