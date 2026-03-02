package com.miplan.ui.screens.kanban

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Diálogo para crear o editar una columna
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateColumnDialog(
    column: com.miplan.domain.model.Column? = null,
    onDismiss: () -> Unit,
    onConfirm: (title: String) -> Unit
) {
    var title by remember { mutableStateOf(column?.title ?: "") }
    
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
                    text = if (column == null) "Crear Columna" else "Editar Columna",
                    style = MaterialTheme.typography.titleLarge
                )
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la columna") },
                    placeholder = { Text("Ej: Por hacer, En progreso, Completado") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
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
                            if (title.isNotBlank()) {
                                onConfirm(title.trim())
                            }
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text(if (column == null) "Crear" else "Guardar")
                    }
                }
            }
        }
    }
}
