package com.miplan.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.miplan.domain.model.Task
import com.miplan.domain.model.UiState
import com.miplan.viewmodel.CalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateToTaskDetail: (Int) -> Unit,
    onNavigateToCreateTask: () -> Unit = {},
    onNavigateToCreateBoard: () -> Unit = {},
    calendarViewModel: CalendarViewModel = hiltViewModel()
) {
    val tasksState by calendarViewModel.tasksState.collectAsState()
    val selectedMonth by calendarViewModel.selectedMonth.collectAsState()
    val selectedDate by calendarViewModel.selectedDate.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val daysOfWeek = remember { daysOfWeek() }
    
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = selectedMonth,
        firstDayOfWeek = daysOfWeek.first()
    )
    
    // Recargar tareas cada vez que la pantalla vuelve a estar visible
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            calendarViewModel.loadTasks()
        }
    }
    
    // Sincronizar cambios de mes desde el ViewModel al estado del calendario
    LaunchedEffect(selectedMonth) {
        state.animateScrollToMonth(selectedMonth)
    }
    
    // Sincronizar el mes visible con el ViewModel cuando el usuario hace swipe
    LaunchedEffect(state.firstVisibleMonth) {
        if (state.firstVisibleMonth.yearMonth != selectedMonth) {
            calendarViewModel.selectMonth(state.firstVisibleMonth.yearMonth)
        }
    }
    
    // Mostrar bottom sheet cuando se selecciona un día
    var showDayDetail by remember { mutableStateOf(false) }
    
    // Mostrar diálogo de selección de mes/año
    var showMonthYearPicker by remember { mutableStateOf(false) }
    
    // Estado del FAB expandible
    var fabExpanded by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendario") },
                actions = {
                    IconButton(onClick = { calendarViewModel.goToToday() }) {
                        Icon(Icons.Default.Today, "Ir a hoy")
                    }
                }
            )
        },
        floatingActionButton = {
            ExpandableFab(
                expanded = fabExpanded,
                onExpandedChange = { fabExpanded = it },
                onCreateTask = {
                    fabExpanded = false
                    onNavigateToCreateTask()
                },
                onCreateBoard = {
                    fabExpanded = false
                    onNavigateToCreateBoard()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header del mes
            CalendarHeader(
                currentMonth = selectedMonth,
                onPreviousMonth = { calendarViewModel.previousMonth() },
                onNextMonth = { calendarViewModel.nextMonth() },
                onMonthClick = { showMonthYearPicker = true }
            )
            
            // Días de la semana
            DaysOfWeekTitle(daysOfWeek = daysOfWeek)
            
            // Calendario - Ocupa todo el espacio disponible
            HorizontalCalendar(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = state,
                dayContent = { day ->
                    Day(
                        day = day,
                        isSelected = selectedDate == day.date,
                        taskCount = if (day.position == DayPosition.MonthDate) {
                            calendarViewModel.getTaskCountForDate(day.date)
                        } else 0,
                        onClick = {
                            if (day.position == DayPosition.MonthDate) {
                                calendarViewModel.selectDate(day.date)
                                showDayDetail = true
                            }
                        }
                    )
                },
                monthHeader = { /* Opcional: header personalizado por mes */ }
            )
            
            // Estado de carga
            when (tasksState) {
                is UiState.Idle -> {
                    // Estado inicial, no hacer nada
                }
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (tasksState as UiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is UiState.Success<*> -> {
                    // El calendario ya se muestra arriba
                }
            }
        }
        
        // Bottom Sheet para mostrar tareas del día
        if (showDayDetail && selectedDate != null) {
            DayDetailBottomSheet(
                date = selectedDate!!,
                tasks = calendarViewModel.getTasksForDate(selectedDate!!),
                onDismiss = {
                    showDayDetail = false
                    calendarViewModel.selectDate(null)
                },
                onTaskClick = onNavigateToTaskDetail
            )
        }
        
        // Diálogo de selección de mes/año
        if (showMonthYearPicker) {
            MonthYearPickerDialog(
                currentMonth = selectedMonth,
                onDismiss = { showMonthYearPicker = false },
                onMonthYearSelected = { yearMonth ->
                    calendarViewModel.selectMonth(yearMonth)
                    showMonthYearPicker = false
                }
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ChevronLeft, "Mes anterior")
        }
        
        Text(
            text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { it.uppercase() } + " ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onMonthClick() }
        )
        
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ChevronRight, "Mes siguiente")
        }
    }
}

@Composable
private fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    isSelected: Boolean,
    taskCount: Int,
    onClick: () -> Unit
) {
    val isToday = day.date == LocalDate.now()
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }
            )
            .clickable(enabled = day.position == DayPosition.MonthDate) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    day.position != DayPosition.MonthDate -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )
            
            if (taskCount > 0 && day.position == DayPosition.MonthDate) {
                Text(
                    text = "$taskCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.primary
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayDetailBottomSheet(
    date: LocalDate,
    tasks: List<Task>,
    onDismiss: () -> Unit,
    onTaskClick: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "${date.dayOfMonth} de ${date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (tasks.isEmpty()) "Sin tareas" else "${tasks.size} tarea${if (tasks.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista de tareas
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay tareas para este día",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                tasks.forEach { task ->
                    TaskItemInCalendar(
                        task = task,
                        onClick = {
                            onTaskClick(task.id)
                            onDismiss()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TaskItemInCalendar(
    task: Task,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de prioridad
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when (task.priority.name) {
                            "HIGH" -> Color(0xFFEF5350)
                            "MEDIUM" -> Color(0xFFFFA726)
                            else -> Color(0xFF66BB6A)
                        }
                    )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                if (!task.description.isNullOrBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthYearPickerDialog(
    currentMonth: YearMonth,
    onDismiss: () -> Unit,
    onMonthYearSelected: (YearMonth) -> Unit
) {
    var selectedYear by remember { mutableStateOf(currentMonth.year) }
    var selectedMonthValue by remember { mutableStateOf(currentMonth.monthValue) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Mes y Año") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Selector de Año
                Text(
                    text = "Año",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedYear-- }) {
                        Icon(Icons.Default.ChevronLeft, "Año anterior")
                    }
                    Text(
                        text = selectedYear.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    IconButton(onClick = { selectedYear++ }) {
                        Icon(Icons.Default.ChevronRight, "Año siguiente")
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Grid de Meses
                Text(
                    text = "Mes",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val months = listOf(
                        "Enero", "Febrero", "Marzo", "Abril",
                        "Mayo", "Junio", "Julio", "Agosto",
                        "Septiembre", "Octubre", "Noviembre", "Diciembre"
                    )
                    
                    for (row in 0..2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (col in 0..3) {
                                val monthIndex = row * 4 + col
                                val isSelected = selectedMonthValue == monthIndex + 1
                                
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp)
                                        .clickable {
                                            selectedMonthValue = monthIndex + 1
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                ) {
                                    Text(
                                        text = months[monthIndex].substring(0, 3),
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onMonthYearSelected(YearMonth.of(selectedYear, selectedMonthValue))
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ExpandableFab(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCreateTask: () -> Unit,
    onCreateBoard: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Opciones expandidas
        if (expanded) {
            // Opción: Crear Tablero
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Crear Tablero",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                SmallFloatingActionButton(
                    onClick = onCreateBoard,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.Default.Dashboard, "Crear tablero")
                }
            }
            
            // Opción: Crear Tarea
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Crear Tarea",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                SmallFloatingActionButton(
                    onClick = onCreateTask,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(Icons.Default.CheckCircle, "Crear tarea")
                }
            }
        }
        
        // FAB principal
        FloatingActionButton(
            onClick = { onExpandedChange(!expanded) }
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = if (expanded) "Cerrar" else "Crear"
            )
        }
    }
}

private fun daysOfWeek(): List<DayOfWeek> {
    return listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )
}
