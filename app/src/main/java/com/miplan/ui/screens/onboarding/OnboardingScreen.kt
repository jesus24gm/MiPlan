package com.miplan.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Pantalla de onboarding para nuevos usuarios
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Bienvenido a MiPlan",
            description = "Organiza tus tareas y proyectos de manera eficiente con nuestra aplicación de gestión de tareas.",
            icon = Icons.Default.CheckCircle,
            iconTint = MaterialTheme.colorScheme.primary
        ),
        OnboardingPage(
            title = "Crea Tareas",
            description = "Añade tareas con título, descripción, prioridad, fecha límite e imágenes para mantener todo organizado.",
            icon = Icons.Default.AddTask,
            iconTint = MaterialTheme.colorScheme.secondary
        ),
        OnboardingPage(
            title = "Tableros Kanban",
            description = "Organiza tus proyectos en tableros visuales con columnas personalizables y tarjetas arrastrables.",
            icon = Icons.Default.Dashboard,
            iconTint = MaterialTheme.colorScheme.tertiary
        ),
        OnboardingPage(
            title = "Colabora en Equipo",
            description = "Invita colaboradores a tus tareas y trabaja en equipo con diferentes roles y permisos.",
            icon = Icons.Default.People,
            iconTint = MaterialTheme.colorScheme.primary
        ),
        OnboardingPage(
            title = "¡Comienza Ahora!",
            description = "Estás listo para empezar a organizar tu vida. ¡Crea tu primera tarea y descubre todo lo que puedes hacer!",
            icon = Icons.Default.Star,
            iconTint = MaterialTheme.colorScheme.secondary
        )
    )
    
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(pages[page])
            }
            
            // Indicadores y botones
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Indicadores de página
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == pagerState.currentPage) 24.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == pagerState.currentPage)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }
                
                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Botón Saltar (solo si no es la última página)
                    if (pagerState.currentPage < pages.size - 1) {
                        TextButton(
                            onClick = onFinish
                        ) {
                            Text("Saltar")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }
                    
                    // Botón Siguiente/Comenzar
                    Button(
                        onClick = {
                            if (pagerState.currentPage < pages.size - 1) {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            } else {
                                onFinish()
                            }
                        },
                        modifier = Modifier.widthIn(min = 120.dp)
                    ) {
                        Text(
                            if (pagerState.currentPage < pages.size - 1) "Siguiente" else "Comenzar"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            if (pagerState.currentPage < pages.size - 1) 
                                Icons.Default.ArrowForward 
                            else 
                                Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Contenido de una página de onboarding
 */
@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = page.iconTint.copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = page.iconTint
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Título
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Descripción
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

/**
 * Modelo de datos para una página de onboarding
 */
private data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconTint: androidx.compose.ui.graphics.Color
)
