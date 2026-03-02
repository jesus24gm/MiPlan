package com.miplan.utils

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Manager para seleccionar imágenes desde galería o cámara
 */
class ImagePickerManager(
    private val context: Context
) {
    
    /**
     * Crea un archivo temporal para la foto de la cámara
     */
    fun createImageFile(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "MIPLAN_${timeStamp}.jpg"
        
        val storageDir = context.getExternalFilesDir("Pictures")
        val imageFile = File(storageDir, imageFileName)
        
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }
}

/**
 * Composable para manejar la selección de imágenes
 */
@Composable
fun rememberImagePickerLaunchers(
    onImageSelected: (Uri) -> Unit
): ImagePickerLaunchers {
    val context = LocalContext.current
    val imagePickerManager = remember { ImagePickerManager(context) }
    
    // URI temporal para la cámara
    var cameraUri: Uri? = null
    
    // Launcher para galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }
    
    // Launcher para cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            onImageSelected(cameraUri!!)
        }
    }
    
    // Launcher para permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraUri = imagePickerManager.createImageFile()
            cameraLauncher.launch(cameraUri)
        }
    }
    
    return ImagePickerLaunchers(
        galleryLauncher = galleryLauncher,
        cameraLauncher = cameraLauncher,
        permissionLauncher = permissionLauncher,
        onLaunchCamera = {
            cameraUri = imagePickerManager.createImageFile()
            cameraLauncher.launch(cameraUri)
        }
    )
}

/**
 * Contenedor de launchers para selección de imágenes
 */
data class ImagePickerLaunchers(
    val galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    val cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    val permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    val onLaunchCamera: () -> Unit
)
