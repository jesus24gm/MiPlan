# 🔧 Fix: Token JWT No Se Enviaba en Requests

## 🐛 Problema

Al intentar crear una tarea, la app mostraba el error:
```
Response status '401 Unauthorized'
```

## 🔍 Causa

El `HttpClient` de Ktor era un Singleton que se creaba una sola vez al iniciar la app. El plugin `Auth` con `bearer` cargaba el token solo en la inicialización, por lo que:

1. App inicia → HttpClient se crea → Token = null (no hay sesión)
2. Usuario hace login → Token se guarda en DataStore
3. Usuario intenta crear tarea → HttpClient usa el token viejo (null)
4. Backend rechaza el request con 401

## ✅ Solución

Cambiar de `Auth` plugin a `HttpSend` interceptor para cargar el token **dinámicamente en cada request**:

### Antes:
```kotlin
install(Auth) {
    bearer {
        loadTokens {
            runBlocking {
                val token = tokenManager.getToken()
                token?.let {
                    BearerTokens(accessToken = it, refreshToken = "")
                }
            }
        }
    }
}
```

### Después:
```kotlin
defaultRequest {
    url(BASE_URL)
    contentType(ContentType.Application.Json)
    
    // Agregar token JWT si existe
    val token = runBlocking { tokenManager.getToken() }
    if (token != null) {
        header(HttpHeaders.Authorization, "Bearer $token")
    }
}
```

## 🧪 Cómo Probar

### 1. Sincronizar Proyecto
```
File > Sync Project with Gradle Files
```

### 2. Limpiar y Reconstruir
```
Build > Clean Project
Build > Rebuild Project
```

### 3. Desinstalar App Anterior
En el emulador/dispositivo:
- Mantén presionada la app MiPlan
- Desinstalar

O desde Android Studio:
```
Run > Edit Configurations > Always install with package manager
```

### 4. Ejecutar App
```
Run > Run 'app'
```

### 5. Probar Flujo Completo

#### a) Registro
```
Email: test@example.com
Password: test123
Nombre: Test User
```

#### b) Login
```
Email: test@example.com
Password: test123
```

#### c) Crear Tarea
1. Click en botón flotante (+)
2. Título: "Mi primera tarea"
3. Descripción: "Descripción de prueba"
4. Prioridad: Alta
5. Fecha: Mañana
6. Click en guardar (✓)

**Resultado esperado:** La tarea se crea exitosamente y vuelves a la pantalla de inicio.

## 📊 Verificación en Logs

En Logcat busca:
```
Ktor: REQUEST: https://miplan-production.up.railway.app/api/tasks
Ktor: COMMON HEADERS
Ktor: -> Authorization: Bearer eyJ...
```

Deberías ver el header `Authorization` con el token JWT.

## 🎯 Archivos Modificados

- `ApiConfig.kt` - Cambio de Auth plugin a HttpSend interceptor

## 💡 Explicación Técnica

### defaultRequest

El bloque `defaultRequest` se ejecuta **antes de cada request**, permitiendo:

1. Obtener el token actual del DataStore
2. Agregarlo al header Authorization
3. Enviar el request con el token correcto

Esto garantiza que siempre se use el token más reciente, incluso si el usuario acaba de hacer login.

### Ventajas

- ✅ Token siempre actualizado
- ✅ No requiere recrear el HttpClient
- ✅ Funciona con Singleton
- ✅ Más simple que Auth plugin
- ✅ Se ejecuta en cada request automáticamente

## 🔮 Mejoras Futuras

### Refresh Token

Si implementas refresh tokens en el backend, puedes agregar lógica en el interceptor:

```kotlin
install(HttpSend) {
    intercept { request ->
        var token = runBlocking { tokenManager.getToken() }
        
        // Verificar si el token está expirado
        if (isTokenExpired(token)) {
            // Refrescar token
            token = refreshToken()
            tokenManager.saveToken(token)
        }
        
        if (token != null) {
            request.headers.append(HttpHeaders.Authorization, "Bearer $token")
        }
        execute(request)
    }
}
```

### Retry en 401

Puedes agregar lógica para reintentar con refresh token:

```kotlin
install(HttpCallValidator) {
    handleResponseExceptionWithRequest { exception, request ->
        val clientException = exception as? ClientRequestException
            ?: return@handleResponseExceptionWithRequest
        
        when (clientException.response.status) {
            HttpStatusCode.Unauthorized -> {
                // Intentar refresh token
                val newToken = refreshToken()
                if (newToken != null) {
                    // Reintentar request con nuevo token
                    // ...
                } else {
                    // Logout
                    tokenManager.clearAll()
                }
            }
        }
    }
}
```

---

**Fecha:** 17 de febrero de 2026, 22:10 UTC+01:00
