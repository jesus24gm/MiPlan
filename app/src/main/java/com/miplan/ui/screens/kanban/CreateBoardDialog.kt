package com.miplan.ui.screens.kanban

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Diálogo para crear o editar un tablero
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBoardDialog(
    board: com.miplan.domain.model.Board? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String?, color: String) -> Unit
) {
    var name by remember { mutableStateOf(board?.name ?: "") }
    var description by remember { mutableStateOf(board?.description ?: "") }
    var selectedColor by remember { mutableStateOf(board?.color ?: "#2196F3") }
    
    val predefinedColors = listOf(
        "#2196F3", // Blue
        "#4CAF50", // Green
        "#FF9800", // Orange
        "#F44336", // Red
        "#9C27B0", // Purple
        "#00BCD4", // Cyan
        "#FFEB3B", // Yellow
        "#795548", // Brown
        "#607D8B", // Blue Grey
        "#E91E63", // Pink
        "#3F51B5", // Indigo
        "#009688"  // Teal
    )
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (board == null) "Crear Tablero" else "Editar Tablero",
                    style = MaterialTheme.typography.titleLarge
                )
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del tablero") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Color del tablero",
                        style = MaterialTheme.typography.labelLarge
                    )
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(6),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(120.dp)
                    ) {
                        items(predefinedColors) { color ->
                            ColorOption(
                                color = color,
                                isSelected = color == selectedColor,
                                onClick = { selectedColor = color }
                            )
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(
                                    name.trim(),
                                    description.trim().ifBlank { null },
                                    selectedColor
                                )
                            }
                        },
                        enabled = name.isNotBlank()
                    ) {
                        Text(if (board == null) "Crear" else "Guardar")
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorOption(
    color: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(parseColor(color))
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Seleccionado",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Alias para editar un tablero existente
 */
@Composable
fun EditBoardDialog(
    board: com.miplan.domain.model.Board,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String?, color: String) -> Unit
) {
    CreateBoardDialog(
        board = board,
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}

/**
 * Convierte un string de color hexadecimal a Color
 */
fun parseColor(colorString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        Color(0xFF6200EE)
    }
}
