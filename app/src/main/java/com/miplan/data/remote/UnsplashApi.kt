package com.miplan.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API de Unsplash para buscar imágenes
 */
interface UnsplashApi {
    
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("client_id") clientId: String
    ): Response<UnsplashSearchResponse>
}

/**
 * Respuesta de búsqueda de Unsplash
 */
data class UnsplashSearchResponse(
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("total_pages")
    val totalPages: Int,
    
    @SerializedName("results")
    val results: List<UnsplashPhoto>
)

/**
 * Foto de Unsplash
 */
data class UnsplashPhoto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("alt_description")
    val altDescription: String?,
    
    @SerializedName("urls")
    val urls: UnsplashUrls,
    
    @SerializedName("user")
    val user: UnsplashUser
)

/**
 * URLs de la foto
 */
data class UnsplashUrls(
    @SerializedName("raw")
    val raw: String,
    
    @SerializedName("full")
    val full: String,
    
    @SerializedName("regular")
    val regular: String,
    
    @SerializedName("small")
    val small: String,
    
    @SerializedName("thumb")
    val thumb: String
)

/**
 * Usuario de Unsplash
 */
data class UnsplashUser(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("name")
    val name: String
)
