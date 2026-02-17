# Arquitectura del Sistema MiPlan

## 1. Visión General

MiPlan implementa una arquitectura de tres capas con separación clara de responsabilidades:

```
┌─────────────────────────────────────────────────────────────┐
│                  ANDROID APP (Cliente)                       │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │  UI Layer (Jetpack Compose)                        │    │
│  │  - Screens, Components, Navigation                 │    │
│  └──────────────────┬─────────────────────────────────┘    │
│                     │                                        │
│  ┌──────────────────▼─────────────────────────────────┐    │
│  │  ViewModel Layer                                   │    │
│  │  - StateFlow, UI States, Event Handling            │    │
│  └──────────────────┬─────────────────────────────────┘    │
│                     │                                        │
│  ┌──────────────────▼─────────────────────────────────┐    │
│  │  Domain Layer                                      │    │
│  │  - Use Cases, Business Models, Repository Interfaces│   │
│  └──────────────────┬─────────────────────────────────┘    │
│                     │                                        │
│  ┌──────────────────▼─────────────────────────────────┐    │
│  │  Data Layer                                        │    │
│  │  - Repository Impl, Remote API, Local Cache        │    │
│  └──────────────────┬─────────────────────────────────┘    │
└─────────────────────┼──────────────────────────────────────┘
                      │
                      │ HTTP/REST + JSON
                      │ JWT Authentication
                      │
┌─────────────────────▼──────────────────────────────────────┐
│                  KTOR SERVER (Backend)                      │
│                                                             │
│  ┌────────────────────────────────────────────────────┐   │
│  │  Routes Layer                                      │   │
│  │  - HTTP Endpoints, Request/Response Handling       │   │
│  └──────────────────┬─────────────────────────────────┘   │
│                     │                                       │
│  ┌──────────────────▼─────────────────────────────────┐   │
│  │  Services Layer                                    │   │
│  │  - Business Logic, Validation, Email Service       │   │
│  └──────────────────┬─────────────────────────────────┘   │
│                     │                                       │
│  ┌──────────────────▼─────────────────────────────────┐   │
│  │  Repositories Layer                                │   │
│  │  - Data Access, SQL Queries, Transactions          │   │
│  └──────────────────┬─────────────────────────────────┘   │
│                     │                                       │
│  ┌──────────────────▼─────────────────────────────────┐   │
│  │  Models Layer                                      │   │
│  │  - Entities, DTOs, Request/Response Objects        │   │
│  └────────────────────────────────────────────────────┘   │
└─────────────────────┼──────────────────────────────────────┘
                      │
                      │ JDBC
                      │
┌─────────────────────▼──────────────────────────────────────┐
│                  MYSQL DATABASE                             │
│                                                             │
│  Tables: users, roles, boards, tasks, notifications,       │
│          user_tasks                                         │
└─────────────────────────────────────────────────────────────┘
```

## 2. Arquitectura Frontend (Android)

### 2.1 Clean Architecture + MVVM

```
app/src/main/java/com/miplan/
│
├── MiPlanApp.kt                    # Application class con Hilt
│
├── ui/                             # 🎨 Presentation Layer
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   ├── Type.kt
│   │   └── Shape.kt
│   │
│   ├── components/                 # Componentes reutilizables
│   │   ├── AppBar.kt
│   │   ├── TaskCard.kt
│   │   ├── BoardCard.kt
│   │   ├── LoadingIndicator.kt
│   │   ├── ErrorMessage.kt
│   │   └── EmptyState.kt
│   │
│   ├── navigation/
│   │   ├── NavGraph.kt            # Grafo de navegación
│   │   ├── Screen.kt              # Sealed class de rutas
│   │   └── NavigationDrawer.kt    # Menú lateral
│   │
│   └── screens/
│       ├── auth/
│       │   ├── LoginScreen.kt
│       │   ├── RegisterScreen.kt
│       │   └── VerifyEmailScreen.kt
│       │
│       ├── home/
│       │   ├── HomeScreen.kt
│       │   └── components/
│       │       ├── TaskList.kt
│       │       └── QuickActions.kt
│       │
│       ├── tasks/
│       │   ├── TaskListScreen.kt
│       │   ├── TaskDetailScreen.kt
│       │   ├── CreateTaskScreen.kt
│       │   └── EditTaskScreen.kt
│       │
│       ├── boards/
│       │   ├── BoardListScreen.kt
│       │   ├── BoardDetailScreen.kt
│       │   ├── CreateBoardScreen.kt
│       │   └── KanbanView.kt
│       │
│       ├── calendar/
│       │   ├── CalendarScreen.kt
│       │   └── components/
│       │       ├── MonthView.kt
│       │       └── DayTaskList.kt
│       │
│       ├── notifications/
│       │   ├── NotificationListScreen.kt
│       │   └── NotificationDetailScreen.kt
│       │
│       ├── profile/
│       │   ├── ProfileScreen.kt
│       │   └── EditProfileScreen.kt
│       │
│       └── admin/
│           ├── AdminDashboardScreen.kt
│           ├── UserManagementScreen.kt
│           └── UserDetailScreen.kt
│
├── viewmodel/                      # 🎯 ViewModel Layer
│   ├── AuthViewModel.kt
│   ├── TaskViewModel.kt
│   ├── BoardViewModel.kt
│   ├── CalendarViewModel.kt
│   ├── NotificationViewModel.kt
│   ├── ProfileViewModel.kt
│   └── AdminViewModel.kt
│
├── domain/                         # 💼 Domain Layer
│   ├── model/                      # Modelos de negocio
│   │   ├── User.kt
│   │   ├── Task.kt
│   │   ├── Board.kt
│   │   ├── Notification.kt
│   │   ├── Role.kt
│   │   └── TaskStatus.kt
│   │
│   ├── repository/                 # Interfaces de repositorios
│   │   ├── AuthRepository.kt
│   │   ├── TaskRepository.kt
│   │   ├── BoardRepository.kt
│   │   ├── NotificationRepository.kt
│   │   └── UserRepository.kt
│   │
│   └── usecase/                    # Casos de uso (opcional para MVP)
│       ├── GetTasksUseCase.kt
│       ├── CreateTaskUseCase.kt
│       └── UpdateTaskUseCase.kt
│
├── data/                           # 📦 Data Layer
│   ├── repository/                 # Implementaciones
│   │   ├── AuthRepositoryImpl.kt
│   │   ├── TaskRepositoryImpl.kt
│   │   ├── BoardRepositoryImpl.kt
│   │   ├── NotificationRepositoryImpl.kt
│   │   └── UserRepositoryImpl.kt
│   │
│   ├── remote/                     # API REST
│   │   ├── ApiService.kt          # Interface Ktor Client
│   │   ├── ApiConfig.kt           # Configuración base
│   │   │
│   │   ├── dto/                   # Data Transfer Objects
│   │   │   ├── request/
│   │   │   │   ├── LoginRequest.kt
│   │   │   │   ├── RegisterRequest.kt
│   │   │   │   ├── CreateTaskRequest.kt
│   │   │   │   └── CreateBoardRequest.kt
│   │   │   │
│   │   │   └── response/
│   │   │       ├── AuthResponse.kt
│   │   │       ├── TaskResponse.kt
│   │   │       ├── BoardResponse.kt
│   │   │       └── ApiResponse.kt
│   │   │
│   │   └── interceptor/
│   │       ├── AuthInterceptor.kt  # Añade JWT token
│   │       └── LoggingInterceptor.kt
│   │
│   └── local/                      # Almacenamiento local
│       ├── PreferencesManager.kt   # DataStore
│       └── TokenManager.kt         # Gestión de JWT
│
└── di/                             # 💉 Dependency Injection
    ├── AppModule.kt                # Módulo principal
    ├── NetworkModule.kt            # Ktor Client, API
    ├── RepositoryModule.kt         # Repositorios
    └── DataStoreModule.kt          # Preferences
```

### 2.2 Flujo de Datos Frontend

```
User Action (Compose UI)
    ↓
ViewModel (recibe evento)
    ↓
Repository (abstracción)
    ↓
Remote Data Source (API call)
    ↓
Backend Response
    ↓
Repository (mapea DTO → Domain Model)
    ↓
ViewModel (actualiza StateFlow)
    ↓
UI (recomposición automática)
```

### 2.3 Gestión de Estados

```kotlin
// Patrón de estado UI
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// En ViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _tasksState = MutableStateFlow<UiState<List<Task>>>(UiState.Idle)
    val tasksState: StateFlow<UiState<List<Task>>> = _tasksState.asStateFlow()
    
    fun loadTasks() {
        viewModelScope.launch {
            _tasksState.value = UiState.Loading
            try {
                val tasks = taskRepository.getTasks()
                _tasksState.value = UiState.Success(tasks)
            } catch (e: Exception) {
                _tasksState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}

// En Composable
@Composable
fun TaskListScreen(viewModel: TaskViewModel = hiltViewModel()) {
    val state by viewModel.tasksState.collectAsState()
    
    when (state) {
        is UiState.Idle -> { /* Initial state */ }
        is UiState.Loading -> LoadingIndicator()
        is UiState.Success -> TaskList((state as UiState.Success).data)
        is UiState.Error -> ErrorMessage((state as UiState.Error).message)
    }
}
```

## 3. Arquitectura Backend (Ktor)

### 3.1 Estructura por Capas

```
backend/src/main/kotlin/com/miplan/
│
├── Application.kt                  # Entry point
│
├── plugins/                        # Configuración Ktor
│   ├── Routing.kt                 # Registro de rutas
│   ├── Security.kt                # JWT authentication
│   ├── Serialization.kt           # JSON kotlinx.serialization
│   ├── StatusPages.kt             # Manejo de errores
│   ├── CORS.kt                    # CORS configuration
│   └── Monitoring.kt              # Logging
│
├── routes/                         # 🛣️ HTTP Endpoints
│   ├── AuthRoutes.kt              # /api/auth/*
│   ├── TaskRoutes.kt              # /api/tasks/*
│   ├── BoardRoutes.kt             # /api/boards/*
│   ├── NotificationRoutes.kt      # /api/notifications/*
│   ├── UserRoutes.kt              # /api/users/*
│   └── AdminRoutes.kt             # /api/admin/*
│
├── services/                       # 💼 Business Logic
│   ├── AuthService.kt             # Autenticación, registro
│   ├── EmailService.kt            # Envío de emails
│   ├── TaskService.kt             # Lógica de tareas
│   ├── BoardService.kt            # Lógica de tableros
│   ├── NotificationService.kt     # Gestión notificaciones
│   └── UserService.kt             # Gestión usuarios
│
├── repositories/                   # 📊 Data Access
│   ├── UserRepository.kt
│   ├── TaskRepository.kt
│   ├── BoardRepository.kt
│   └── NotificationRepository.kt
│
├── models/                         # 📦 Data Models
│   ├── entities/                  # Entidades de DB
│   │   ├── User.kt
│   │   ├── Role.kt
│   │   ├── Task.kt
│   │   ├── Board.kt
│   │   └── Notification.kt
│   │
│   ├── requests/                  # DTOs de entrada
│   │   ├── LoginRequest.kt
│   │   ├── RegisterRequest.kt
│   │   ├── CreateTaskRequest.kt
│   │   ├── UpdateTaskRequest.kt
│   │   └── CreateBoardRequest.kt
│   │
│   └── responses/                 # DTOs de salida
│       ├── AuthResponse.kt
│       ├── UserResponse.kt
│       ├── TaskResponse.kt
│       ├── BoardResponse.kt
│       └── ApiResponse.kt
│
├── security/                       # 🔐 Security
│   ├── JwtConfig.kt               # Generación y validación JWT
│   ├── PasswordHasher.kt          # BCrypt hashing
│   ├── TokenManager.kt            # Gestión de tokens
│   └── RoleAuthorization.kt       # Verificación de roles
│
├── database/                       # 🗄️ Database
│   ├── DatabaseFactory.kt         # HikariCP + Exposed
│   ├── Tables.kt                  # Definición de tablas Exposed
│   └── DatabaseConfig.kt          # Configuración
│
└── utils/                          # 🛠️ Utilities
    ├── Extensions.kt
    ├── Validators.kt
    └── Constants.kt
```

### 3.2 Flujo de Petición Backend

```
HTTP Request
    ↓
Ktor Routing (routes/)
    ↓
JWT Validation (security/)
    ↓
Request DTO Deserialization
    ↓
Service Layer (services/)
    ↓
Business Logic + Validation
    ↓
Repository Layer (repositories/)
    ↓
Database Query (Exposed ORM)
    ↓
MySQL Database
    ↓
Entity → Response DTO
    ↓
JSON Serialization
    ↓
HTTP Response
```

### 3.3 Ejemplo de Endpoint Completo

```kotlin
// routes/TaskRoutes.kt
fun Route.taskRoutes(taskService: TaskService) {
    authenticate("jwt") {
        route("/api/tasks") {
            
            get {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asInt()
                    ?: throw UnauthorizedException()
                
                val tasks = taskService.getUserTasks(userId)
                call.respond(HttpStatusCode.OK, tasks)
            }
            
            post {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asInt()
                    ?: throw UnauthorizedException()
                
                val request = call.receive<CreateTaskRequest>()
                val task = taskService.createTask(userId, request)
                call.respond(HttpStatusCode.Created, task)
            }
        }
    }
}

// services/TaskService.kt
class TaskService(private val taskRepository: TaskRepository) {
    
    suspend fun getUserTasks(userId: Int): List<TaskResponse> {
        return taskRepository.findByUserId(userId)
            .map { it.toResponse() }
    }
    
    suspend fun createTask(userId: Int, request: CreateTaskRequest): TaskResponse {
        validateTaskRequest(request)
        
        val task = taskRepository.create(
            userId = userId,
            title = request.title,
            description = request.description,
            dueDate = request.dueDate,
            priority = request.priority,
            boardId = request.boardId
        )
        
        return task.toResponse()
    }
    
    private fun validateTaskRequest(request: CreateTaskRequest) {
        require(request.title.isNotBlank()) { "El título es obligatorio" }
        require(request.title.length <= 200) { "El título es demasiado largo" }
    }
}

// repositories/TaskRepository.kt
class TaskRepository {
    
    suspend fun findByUserId(userId: Int): List<Task> = dbQuery {
        Tasks.select { Tasks.createdBy eq userId }
            .map { it.toTask() }
    }
    
    suspend fun create(
        userId: Int,
        title: String,
        description: String?,
        dueDate: LocalDateTime?,
        priority: String,
        boardId: Int?
    ): Task = dbQuery {
        val id = Tasks.insertAndGetId {
            it[Tasks.title] = title
            it[Tasks.description] = description
            it[Tasks.dueDate] = dueDate
            it[Tasks.priority] = priority
            it[Tasks.boardId] = boardId
            it[Tasks.createdBy] = userId
            it[Tasks.status] = "PENDING"
            it[Tasks.createdAt] = LocalDateTime.now()
        }
        
        Tasks.select { Tasks.id eq id }
            .single()
            .toTask()
    }
}
```

## 4. Base de Datos MySQL

### 4.1 Modelo Entidad-Relación

```
┌─────────────┐
│    roles    │
├─────────────┤
│ id (PK)     │
│ name        │
│ description │
└──────┬──────┘
       │
       │ 1:N
       │
┌──────▼──────────┐         ┌─────────────────┐
│     users       │         │     boards      │
├─────────────────┤         ├─────────────────┤
│ id (PK)         │◄────┐   │ id (PK)         │
│ email (UNIQUE)  │     │   │ name            │
│ password_hash   │     │   │ description     │
│ name            │     │   │ color           │
│ role_id (FK)    │     │   │ user_id (FK)    │
│ is_verified     │     │   │ created_at      │
│ verification_tk │     │   │ updated_at      │
│ created_at      │     │   └────────┬────────┘
│ updated_at      │     │            │
└─────────────────┘     │            │ 1:N
                        │            │
                        │   ┌────────▼────────┐
                        │   │      tasks      │
                        │   ├─────────────────┤
                        │   │ id (PK)         │
                        │   │ title           │
                        │   │ description     │
                        │   │ status          │
                        │   │ priority        │
                        │   │ due_date        │
                        │   │ board_id (FK)   │
                        │   │ created_by (FK) ├──┘
                        │   │ created_at      │
                        │   │ updated_at      │
                        │   └────────┬────────┘
                        │            │
                        │            │ 1:N
                        │            │
                        │   ┌────────▼────────────┐
                        └───┤   notifications     │
                            ├─────────────────────┤
                            │ id (PK)             │
                            │ user_id (FK)        │
                            │ task_id (FK)        │
                            │ message             │
                            │ type                │
                            │ is_read             │
                            │ created_at          │
                            └─────────────────────┘

┌──────────────────────────┐
│      user_tasks          │  (Tareas compartidas)
├──────────────────────────┤
│ user_id (FK, PK)         │
│ task_id (FK, PK)         │
│ permission               │  (view/edit)
│ assigned_at              │
└──────────────────────────┘
```

### 4.2 Índices y Optimizaciones

```sql
-- Índices para búsquedas frecuentes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_tasks_created_by ON tasks(created_by);
CREATE INDEX idx_tasks_board_id ON tasks(board_id);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_boards_user_id ON boards(user_id);

-- Índice compuesto para consultas comunes
CREATE INDEX idx_tasks_user_status ON tasks(created_by, status);
CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read);
```

## 5. Seguridad

### 5.1 Autenticación JWT

**Flujo de Login:**
1. Usuario envía email + password
2. Backend valida credenciales
3. Backend genera JWT con payload: `{ userId, email, role, exp }`
4. Cliente almacena JWT en DataStore (encriptado)
5. Cliente envía JWT en header `Authorization: Bearer <token>`
6. Backend valida JWT en cada petición protegida

**Configuración JWT:**
```kotlin
// security/JwtConfig.kt
object JwtConfig {
    private const val SECRET = "your-secret-key-change-in-production"
    private const val ISSUER = "miplan-backend"
    private const val AUDIENCE = "miplan-app"
    private const val VALIDITY = 7 * 24 * 60 * 60 * 1000L // 7 días
    
    fun generateToken(user: User): String {
        return JWT.create()
            .withAudience(AUDIENCE)
            .withIssuer(ISSUER)
            .withClaim("userId", user.id)
            .withClaim("email", user.email)
            .withClaim("role", user.role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY))
            .sign(Algorithm.HMAC256(SECRET))
    }
}
```

### 5.2 Protección de Rutas

```kotlin
// Rutas públicas
route("/api/auth") {
    post("/register") { /* ... */ }
    post("/login") { /* ... */ }
    get("/verify/{token}") { /* ... */ }
}

// Rutas autenticadas
authenticate("jwt") {
    route("/api/tasks") { /* ... */ }
    route("/api/boards") { /* ... */ }
}

// Rutas solo admin
authenticate("jwt") {
    route("/api/admin") {
        // Middleware adicional verifica role
        intercept(ApplicationCallPipeline.Call) {
            val role = call.principal<JWTPrincipal>()
                ?.payload?.getClaim("role")?.asString()
            
            if (role != "ADMIN") {
                call.respond(HttpStatusCode.Forbidden)
                finish()
            }
        }
        
        get("/users") { /* ... */ }
    }
}
```

## 6. Manejo de Errores

### 6.1 Backend

```kotlin
// plugins/StatusPages.kt
install(StatusPages) {
    exception<Throwable> { call, cause ->
        when (cause) {
            is UnauthorizedException -> {
                call.respond(HttpStatusCode.Unauthorized, 
                    ApiResponse(success = false, message = "No autorizado"))
            }
            is NotFoundException -> {
                call.respond(HttpStatusCode.NotFound,
                    ApiResponse(success = false, message = cause.message ?: "No encontrado"))
            }
            is ValidationException -> {
                call.respond(HttpStatusCode.BadRequest,
                    ApiResponse(success = false, message = cause.message ?: "Datos inválidos"))
            }
            else -> {
                call.respond(HttpStatusCode.InternalServerError,
                    ApiResponse(success = false, message = "Error interno del servidor"))
            }
        }
    }
}
```

### 6.2 Frontend

```kotlin
// data/repository/TaskRepositoryImpl.kt
override suspend fun getTasks(): Result<List<Task>> {
    return try {
        val response = apiService.getTasks()
        if (response.success) {
            Result.success(response.data.map { it.toDomain() })
        } else {
            Result.failure(Exception(response.message))
        }
    } catch (e: Exception) {
        when (e) {
            is UnresolvedAddressException -> 
                Result.failure(Exception("No hay conexión a internet"))
            is HttpRequestTimeoutException -> 
                Result.failure(Exception("Tiempo de espera agotado"))
            else -> 
                Result.failure(Exception("Error: ${e.message}"))
        }
    }
}
```

## 7. Testing

### 7.1 Backend Tests

```kotlin
// test/routes/TaskRoutesTest.kt
class TaskRoutesTest {
    @Test
    fun `test create task returns 201`() = testApplication {
        val response = client.post("/api/tasks") {
            header("Authorization", "Bearer $validToken")
            contentType(ContentType.Application.Json)
            setBody(CreateTaskRequest(
                title = "Test Task",
                description = "Description",
                priority = "HIGH"
            ))
        }
        
        assertEquals(HttpStatusCode.Created, response.status)
    }
}
```

### 7.2 Android Tests

```kotlin
// test/viewmodel/TaskViewModelTest.kt
class TaskViewModelTest {
    @Test
    fun `loadTasks updates state to Success`() = runTest {
        val mockRepository = mockk<TaskRepository>()
        coEvery { mockRepository.getTasks() } returns listOf(mockTask)
        
        val viewModel = TaskViewModel(mockRepository)
        viewModel.loadTasks()
        
        val state = viewModel.tasksState.value
        assertTrue(state is UiState.Success)
        assertEquals(1, (state as UiState.Success).data.size)
    }
}
```

## 8. Consideraciones de Rendimiento

### 8.1 Backend
- **Connection Pooling**: HikariCP con 10 conexiones
- **Caching**: Caché de usuarios autenticados (opcional)
- **Paginación**: Implementar para listas grandes
- **Índices DB**: En columnas de búsqueda frecuente

### 8.2 Frontend
- **Lazy Loading**: LazyColumn para listas
- **Image Caching**: Coil con caché de disco
- **State Hoisting**: Evitar recomposiciones innecesarias
- **Remember**: Usar remember para cálculos costosos

## 9. Deployment

### 9.1 Backend
- **Servidor**: VPS con Ubuntu 22.04
- **Reverse Proxy**: Nginx
- **SSL**: Let's Encrypt
- **Process Manager**: systemd
- **Base de Datos**: MySQL 8.0 con backups diarios

### 9.2 Android
- **Build**: Release con ProGuard
- **Signing**: Keystore seguro
- **Distribution**: Google Play Store
- **Versioning**: Semantic Versioning (1.0.0)

---

**Última actualización**: Febrero 2026
