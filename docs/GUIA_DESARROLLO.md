# Guía de Desarrollo - MiPlan

## 📋 Índice

1. [Configuración Inicial](#configuración-inicial)
2. [Orden de Implementación](#orden-de-implementación)
3. [Desarrollo por Semanas](#desarrollo-por-semanas)
4. [Testing](#testing)
5. [Troubleshooting](#troubleshooting)

---

## 🚀 Configuración Inicial

### Requisitos Previos

- **JDK 17** o superior
- **Android Studio** Hedgehog (2023.1.1) o superior
- **MySQL 8.0** o superior
- **Git** para control de versiones

### Paso 1: Configurar Base de Datos

```bash
# 1. Iniciar MySQL
mysql -u root -p

# 2. Ejecutar script de creación
mysql -u root -p < database/schema.sql

# 3. Verificar creación
mysql -u root -p miplan_db
SHOW TABLES;
SELECT * FROM roles;
SELECT * FROM users;
```

**Credenciales de administrador por defecto:**
- Email: `admin@miplan.com`
- Password: `admin123`

### Paso 2: Configurar Backend

```bash
cd backend

# 1. Editar configuración
cp src/main/resources/application.conf src/main/resources/application.conf.local

# 2. Modificar application.conf con tus credenciales:
# - database.user
# - database.password
# - email.username (Gmail)
# - email.password (App Password de Gmail)
# - jwt.secret (cambiar en producción)

# 3. Compilar proyecto
./gradlew build

# 4. Ejecutar servidor
./gradlew run
```

El servidor estará disponible en `http://localhost:8080`

**Verificar que funciona:**
```bash
curl http://localhost:8080/health
# Respuesta esperada: OK
```

### Paso 3: Configurar Android

```bash
# 1. Abrir Android Studio
# File > Open > seleccionar carpeta 'android'

# 2. Esperar sincronización de Gradle

# 3. Verificar configuración en local.properties (se crea automáticamente)
sdk.dir=C\:\\Users\\TuUsuario\\AppData\\Local\\Android\\Sdk

# 4. Configurar emulador o dispositivo físico

# 5. Build > Make Project

# 6. Run > Run 'app'
```

**Nota:** Si usas emulador, la URL del backend debe ser `http://10.0.2.2:8080`

---

## 📅 Orden de Implementación

### Fase 1: Backend Base (Días 1-7)

#### Día 1-2: Configuración y Autenticación

**Tareas:**
1. ✅ Configurar base de datos MySQL
2. ✅ Implementar modelos y tablas
3. ✅ Crear AuthService y UserRepository
4. ✅ Implementar registro de usuarios
5. ✅ Implementar login con JWT
6. ✅ Configurar envío de emails

**Testing:**
```bash
# Registrar usuario
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "name": "Usuario Test"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@miplan.com",
    "password": "admin123"
  }'

# Guardar el token recibido para siguientes peticiones
```

#### Día 3-4: CRUD de Tareas

**Tareas:**
1. ✅ Implementar TaskRepository
2. ✅ Crear TaskService
3. ✅ Implementar rutas de tareas
4. ✅ Agregar validaciones

**Testing:**
```bash
# Crear tarea (usar token del login)
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer TU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Mi primera tarea",
    "description": "Descripción de la tarea",
    "priority": "HIGH",
    "dueDate": "2026-02-20T10:00:00"
  }'

# Listar tareas
curl -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer TU_TOKEN_JWT"

# Actualizar tarea
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer TU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Tarea actualizada",
    "description": "Nueva descripción",
    "status": "IN_PROGRESS",
    "priority": "MEDIUM"
  }'
```

#### Día 5-6: CRUD de Tableros

**Tareas:**
1. Implementar BoardRepository
2. Crear BoardService
3. Implementar rutas de tableros
4. Relacionar tareas con tableros

**Archivos a crear:**
```kotlin
// backend/src/main/kotlin/com/miplan/services/BoardService.kt
// backend/src/main/kotlin/com/miplan/routes/BoardRoutes.kt
```

**Testing:**
```bash
# Crear tablero
curl -X POST http://localhost:8080/api/boards \
  -H "Authorization: Bearer TU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Proyecto Personal",
    "description": "Tareas personales",
    "color": "#E3F2FD"
  }'

# Listar tableros
curl -X GET http://localhost:8080/api/boards \
  -H "Authorization: Bearer TU_TOKEN_JWT"
```

#### Día 7: Notificaciones

**Tareas:**
1. Implementar NotificationRepository
2. Crear NotificationService
3. Implementar rutas de notificaciones
4. Crear notificaciones automáticas

**Archivos a crear:**
```kotlin
// backend/src/main/kotlin/com/miplan/services/NotificationService.kt
// backend/src/main/kotlin/com/miplan/routes/NotificationRoutes.kt
```

---

### Fase 2: Frontend Android (Días 8-21)

#### Día 8-10: Autenticación UI

**Tareas:**
1. ✅ Implementar LoginScreen
2. ✅ Implementar RegisterScreen
3. ✅ Configurar navegación
4. ✅ Implementar AuthViewModel
5. ✅ Manejar estados de UI

**Archivos ya creados:**
- `ui/screens/auth/LoginScreen.kt`
- `ui/screens/auth/RegisterScreen.kt`
- `viewmodel/AuthViewModel.kt`

**Testing:**
1. Ejecutar app en emulador
2. Probar registro de nuevo usuario
3. Verificar email de confirmación (logs)
4. Probar login con credenciales correctas
5. Verificar navegación a HomeScreen

#### Día 11-13: Pantallas de Tareas

**Tareas:**
1. Implementar TaskListScreen
2. Implementar CreateTaskScreen
3. Implementar TaskDetailScreen
4. Implementar EditTaskScreen
5. Agregar filtros y búsqueda

**Archivos a crear:**
```kotlin
// ui/screens/tasks/TaskListScreen.kt
@Composable
fun TaskListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasksState by viewModel.tasksState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Tareas") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, "Nueva tarea")
            }
        }
    ) { padding ->
        when (val state = tasksState) {
            is UiState.Loading -> LoadingIndicator()
            is UiState.Success -> {
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(state.data) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onNavigateToDetail(task.id) }
                        )
                    }
                }
            }
            is UiState.Error -> ErrorMessage(state.message)
            else -> {}
        }
    }
}

// ui/screens/tasks/CreateTaskScreen.kt
@Composable
fun CreateTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    
    val createState by viewModel.createTaskState.collectAsState()
    
    LaunchedEffect(createState) {
        if (createState is UiState.Success) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Tarea") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Selector de prioridad
            PrioritySelector(
                selected = priority,
                onSelect = { priority = it }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    viewModel.createTask(
                        title = title,
                        description = description,
                        priority = priority,
                        dueDate = null,
                        boardId = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Crear Tarea")
            }
        }
    }
}
```

#### Día 14-16: Pantallas de Tableros

**Tareas:**
1. Implementar BoardListScreen
2. Implementar CreateBoardScreen
3. Implementar BoardDetailScreen (vista Kanban)
4. Implementar drag & drop de tareas

**Componentes clave:**
```kotlin
// ui/screens/boards/BoardListScreen.kt
// ui/screens/boards/CreateBoardScreen.kt
// ui/screens/boards/BoardDetailScreen.kt
// ui/screens/boards/KanbanView.kt
```

#### Día 17-18: Vista de Calendario

**Tareas:**
1. Implementar CalendarScreen
2. Crear componente MonthView
3. Mostrar tareas por día
4. Agregar navegación entre meses

**Archivos a crear:**
```kotlin
// ui/screens/calendar/CalendarScreen.kt
// ui/screens/calendar/components/MonthView.kt
// ui/screens/calendar/components/DayTaskList.kt
// viewmodel/CalendarViewModel.kt
```

#### Día 19-20: Notificaciones

**Tareas:**
1. Implementar NotificationListScreen
2. Implementar NotificationViewModel
3. Agregar badge de notificaciones no leídas
4. Implementar marcar como leída

#### Día 21: Perfil y Configuración

**Tareas:**
1. Implementar ProfileScreen
2. Implementar EditProfileScreen
3. Agregar opción de cambiar contraseña
4. Implementar cierre de sesión

---

### Fase 3: Funcionalidades Avanzadas (Días 22-28)

#### Día 22-23: Panel de Administración

**Tareas:**
1. Implementar AdminDashboardScreen
2. Implementar UserManagementScreen
3. Agregar gestión de roles
4. Implementar estadísticas

**Verificación de rol:**
```kotlin
// En ViewModel
val isAdmin = userRole == "ADMIN"

// En UI
if (isAdmin) {
    NavigationDrawerItem(
        label = { Text("Administración") },
        onClick = { onNavigateToAdmin() }
    )
}
```

#### Día 24-25: Mejoras de UX

**Tareas:**
1. Agregar animaciones de transición
2. Implementar pull-to-refresh
3. Agregar estados vacíos personalizados
4. Mejorar manejo de errores
5. Agregar confirmaciones de eliminación

#### Día 26-27: Testing

**Tareas:**
1. Escribir tests unitarios para ViewModels
2. Escribir tests de integración para repositorios
3. Escribir tests de UI con Compose Testing
4. Probar flujos completos

**Ejemplo de test:**
```kotlin
// test/viewmodel/TaskViewModelTest.kt
@Test
fun `loadTasks updates state to Success`() = runTest {
    val mockRepository = mockk<TaskRepository>()
    coEvery { mockRepository.getTasks() } returns Result.success(listOf(mockTask))
    
    val viewModel = TaskViewModel(mockRepository)
    viewModel.loadTasks()
    
    val state = viewModel.tasksState.value
    assertTrue(state is UiState.Success)
    assertEquals(1, (state as UiState.Success).data.size)
}
```

#### Día 28: Refinamiento y Documentación

**Tareas:**
1. Revisar y refactorizar código
2. Optimizar rendimiento
3. Completar documentación
4. Preparar para despliegue

---

## 🧪 Testing

### Backend Testing

```bash
cd backend

# Ejecutar todos los tests
./gradlew test

# Ejecutar tests específicos
./gradlew test --tests "com.miplan.services.AuthServiceTest"

# Ver reporte de cobertura
./gradlew test jacocoTestReport
```

### Android Testing

```bash
cd android

# Tests unitarios
./gradlew test

# Tests instrumentados (requiere emulador/dispositivo)
./gradlew connectedAndroidTest

# Test específico
./gradlew test --tests "com.miplan.viewmodel.TaskViewModelTest"
```

### Testing Manual

**Checklist de funcionalidades:**

- [ ] Registro de usuario
- [ ] Verificación de email
- [ ] Login exitoso
- [ ] Login con credenciales incorrectas
- [ ] Crear tarea
- [ ] Editar tarea
- [ ] Eliminar tarea
- [ ] Cambiar estado de tarea
- [ ] Crear tablero
- [ ] Asignar tarea a tablero
- [ ] Ver calendario
- [ ] Ver notificaciones
- [ ] Marcar notificación como leída
- [ ] Editar perfil
- [ ] Cerrar sesión
- [ ] Panel admin (solo admin)

---

## 🔧 Troubleshooting

### Problemas Comunes

#### 1. Error de conexión a MySQL

**Síntoma:** `Communications link failure`

**Solución:**
```bash
# Verificar que MySQL está corriendo
mysql --version
sudo service mysql status

# Verificar credenciales en application.conf
database.user = "root"
database.password = "tu_password"
```

#### 2. Error de compilación en Android

**Síntoma:** `Unresolved reference`

**Solución:**
```bash
# Limpiar y reconstruir
./gradlew clean build

# En Android Studio:
# File > Invalidate Caches / Restart
```

#### 3. JWT Token inválido

**Síntoma:** `401 Unauthorized`

**Solución:**
- Verificar que el token no haya expirado (7 días)
- Verificar que el header Authorization esté correcto
- Hacer login nuevamente para obtener nuevo token

#### 4. Email no se envía

**Síntoma:** Error al enviar email de verificación

**Solución:**
```properties
# Usar App Password de Gmail
# 1. Ir a Google Account > Security
# 2. Activar 2-Step Verification
# 3. Generar App Password
# 4. Usar ese password en application.conf

email.username = "tu-email@gmail.com"
email.password = "tu-app-password-de-16-caracteres"
```

#### 5. Emulador no conecta con backend

**Síntoma:** `Failed to connect to localhost:8080`

**Solución:**
```kotlin
// En ApiConfig.kt, usar IP especial del emulador
const val BASE_URL = "http://10.0.2.2:8080"

// Para dispositivo físico, usar IP de tu PC
const val BASE_URL = "http://192.168.1.XXX:8080"
```

---

## 📊 Métricas de Progreso

### Semana 1
- ✅ Backend configurado
- ✅ Autenticación funcionando
- ✅ CRUD de tareas completo

### Semana 2
- ✅ CRUD de tableros
- ✅ Notificaciones básicas
- ✅ Frontend configurado

### Semana 3
- ⏳ Pantallas principales de Android
- ⏳ Navegación completa
- ⏳ Integración frontend-backend

### Semana 4
- ⏳ Funcionalidades avanzadas
- ⏳ Testing completo
- ⏳ Refinamiento y documentación

---

## 🎯 Próximos Pasos (Post-MVP)

### Funcionalidades Futuras

1. **Notificaciones Push**
   - Integrar Firebase Cloud Messaging
   - Notificaciones en tiempo real

2. **Modo Offline**
   - Room Database para caché local
   - Sincronización automática

3. **Colaboración**
   - Compartir tareas con otros usuarios
   - Comentarios en tareas
   - Historial de cambios

4. **Adjuntos**
   - Subir archivos a tareas
   - Almacenamiento en cloud

5. **Estadísticas**
   - Gráficos de productividad
   - Reportes semanales/mensuales

6. **Temas**
   - Modo oscuro
   - Personalización de colores

---

**¡Éxito en tu desarrollo!** 🚀
