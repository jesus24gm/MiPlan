package com.miplan.ui.screens.kanban

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.miplan.viewmodel.KanbanViewModel

/**
 * Pantalla completa para crear un nuevo tablero
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBoardScreen(
    onNavigateBack: () -> Unit,
    onBoardCreated: (Int) -> Unit = {},
    viewModel: KanbanViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(true) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Tablero") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showDialog) {
                CreateBoardDialog(
                    onDismiss = {
                        showDialog = false
                        onNavigateBack()
                    },
                    onConfirm = { name, description, color ->
                        viewModel.createBoard(name, description, color)
                        showDialog = false
                        // Volver a la pantalla anterior después de crear
                        onNavigateBack()
                    }
                )
            }
        }
    }
}
