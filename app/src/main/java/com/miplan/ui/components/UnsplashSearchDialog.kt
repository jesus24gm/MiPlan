package com.miplan.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.miplan.data.remote.UnsplashPhoto
import com.miplan.data.remote.UnsplashService
import kotlinx.coroutines.launch

/**
 * Diálogo para buscar y seleccionar imágenes de Unsplash
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun UnsplashSearchDialog(
    unsplashService: UnsplashService,
    onDismiss: () -> Unit,
    onImageSelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var photos by remember { mutableStateOf<List<UnsplashPhoto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Función para realizar búsqueda
    val performSearch = {
        if (searchQuery.isNotBlank()) {
            keyboardController?.hide()
            scope.launch {
                isLoading = true
                errorMessage = null
                val result = unsplashService.searchPhotos(searchQuery)
                isLoading = false
                
                result.onSuccess { photos = it }
                result.onFailure { errorMessage = it.message }
            }
        }
    }
    
    // Búsqueda inicial
    LaunchedEffect(Unit) {
        searchQuery = "workspace"
        scope.launch {
            isLoading = true
            errorMessage = null
            val result = unsplashService.searchPhotos(searchQuery)
            isLoading = false
            
            result.onSuccess { photos = it }
            result.onFailure { errorMessage = it.message }
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Buscar en Unsplash",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Cerrar")
                    }
                }
                
                // Barra de búsqueda
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Buscar imágenes...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, "Buscar")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            performSearch()
                        }
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Contenido
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        
                        errorMessage != null -> {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    errorMessage!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        
                        photos.isEmpty() -> {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.ImageSearch,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No se encontraron imágenes",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(photos) { photo ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            .clickable {
                                                onImageSelected(photo.urls.regular)
                                                onDismiss()
                                            }
                                    ) {
                                        Box {
                                            AsyncImage(
                                                model = photo.urls.small,
                                                contentDescription = photo.altDescription,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                            
                                            // Atribución
                                            Surface(
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .fillMaxWidth(),
                                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                            ) {
                                                Text(
                                                    text = "📷 ${photo.user.name}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
