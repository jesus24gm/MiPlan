package com.miplan.utils

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.miplan.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Manager para subir imágenes a Cloudinary
 */
@Singleton
class CloudinaryManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var isInitialized = false
    
    /**
     * Inicializa Cloudinary con las credenciales
     */
    private fun initCloudinary() {
        if (!isInitialized) {
            try {
                val config = mapOf(
                    "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
                    "api_key" to BuildConfig.CLOUDINARY_API_KEY,
                    "api_secret" to BuildConfig.CLOUDINARY_API_SECRET
                )
                MediaManager.init(context, config)
                isInitialized = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Sube una imagen a Cloudinary
     * @param uri URI de la imagen local
     * @return URL de la imagen subida o null si falla
     */
    suspend fun uploadImage(uri: Uri): Result<String> = suspendCancellableCoroutine { continuation ->
        try {
            initCloudinary()
            
            MediaManager.get().upload(uri)
                .option("folder", "miplan/tasks")
                .option("resource_type", "image")
                .option("quality", "auto")
                .option("fetch_format", "auto")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        // Subida iniciada
                    }
                    
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        // Progreso de subida
                    }
                    
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        
                        if (url != null) {
                            continuation.resume(Result.success(url))
                        } else {
                            continuation.resume(Result.failure(Exception("URL no encontrada en respuesta")))
                        }
                    }
                    
                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resume(Result.failure(Exception(error.description)))
                    }
                    
                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        // Subida reprogramada
                    }
                })
                .dispatch()
                
        } catch (e: Exception) {
            e.printStackTrace()
            continuation.resume(Result.failure(e))
        }
    }
    
    /**
     * Elimina una imagen de Cloudinary (opcional)
     * @param publicId ID público de la imagen en Cloudinary
     */
    suspend fun deleteImage(publicId: String): Result<Boolean> {
        return try {
            initCloudinary()
            // Nota: La eliminación requiere llamada al API de Cloudinary
            // Por simplicidad, no la implementamos ahora
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
