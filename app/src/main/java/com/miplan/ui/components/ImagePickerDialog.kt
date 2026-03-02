package com.miplan.ui.components

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Diálogo para seleccionar fuente de imagen
 */
@Composable
fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onUnsplashClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Seleccionar imagen")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Galería
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onGalleryClick() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PhotoLibrary,
                            contentDescription = "Galería",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                "Galería",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Seleccionar desde tus fotos",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Cámara
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCameraClick() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Cámara",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                "Cámara",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Tomar una foto nueva",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Unsplash
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUnsplashClick() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Unsplash",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                "Unsplash",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Buscar imágenes de stock",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
