# 📧 Configurar Verificación de Email Real

## ✅ Paso 1: Obtener App Password de Gmail

### 1.1 Habilitar Verificación en 2 Pasos

1. Ve a: https://myaccount.google.com/security
2. Busca **"Verificación en 2 pasos"**
3. Click en **"Empezar"** o **"Activar"**
4. Sigue los pasos (necesitarás tu teléfono)

### 1.2 Crear App Password

1. Ve a: https://myaccount.google.com/apppasswords
2. En "Seleccionar app": Elige **"Correo"**
3. En "Seleccionar dispositivo": Elige **"Otro (nombre personalizado)"**
4. Escribe: **"MiPlan Backend"**
5. Click en **"Generar"**
6. **COPIA LA CONTRASEÑA** (16 caracteres, algo como: `abcd efgh ijkl mnop`)
7. Guárdala en un lugar seguro

## ✅ Paso 2: Configurar Backend

### Opción A: Editar application.conf Directamente (Desarrollo)

**Archivo:** `backend/src/main/resources/application.conf`

Cambia estas líneas:

```hocon
email {
    host = "smtp.gmail.com"
    port = 587
    username = "TU-EMAIL@gmail.com"           # ← Cambia esto
    password = "abcdefghijklmnop"             # ← App Password (sin espacios)
    from = "MiPlan <TU-EMAIL@gmail.com>"      # ← Cambia esto
}
```

**Ejemplo:**
```hocon
email {
    host = "smtp.gmail.com"
    port = 587
    username = "jesus.miplan@gmail.com"
    password = "abcdefghijklmnop"
    from = "MiPlan <jesus.miplan@gmail.com>"
}
```

### Opción B: Variables de Entorno (Producción - Más Seguro)

**1. Crea archivo `.env` en la carpeta `backend`:**

```env
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=tu-email@gmail.com
EMAIL_PASSWORD=abcdefghijklmnop
EMAIL_FROM=MiPlan <tu-email@gmail.com>
```

**2. Instala dotenv (opcional):**

Agrega en `backend/build.gradle.kts`:
```kotlin
dependencies {
    // ... otras dependencias
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
}
```

**3. Carga las variables al iniciar:**

Crea `backend/src/main/kotlin/com/miplan/config/EnvConfig.kt`:
```kotlin
package com.miplan.config

import io.github.cdimascio.dotenv.dotenv

object EnvConfig {
    private val dotenv = dotenv {
        ignoreIfMissing = true
    }
    
    fun get(key: String): String? = dotenv[key]
}
```

## ✅ Paso 3: Reiniciar Backend

```powershell
cd C:\Users\Jesus\CascadeProjects\MiPlan\backend

# Detener el backend actual (Ctrl + C)

# Iniciar de nuevo
.\gradlew run
```

## ✅ Paso 4: Probar Verificación de Email

### 4.1 Registrar un nuevo usuario

En la app Android:
1. Click en "Registrarse"
2. Completa el formulario con tu email real
3. Click en "Registrar"

### 4.2 Verificar que llegó el email

1. Revisa tu bandeja de entrada
2. Busca un email de "MiPlan" o tu email configurado
3. Debería contener un enlace de verificación

### 4.3 Click en el enlace

El enlace será algo como:
```
http://localhost:8080/api/auth/verify/TOKEN_AQUI
```

Debería mostrar: "Email verificado exitosamente"

### 4.4 Hacer login

Ahora puedes hacer login con ese usuario en la app.

## 🐛 Troubleshooting

### Error: "Authentication failed"

**Causa:** Contraseña incorrecta o no es App Password

**Solución:**
1. Verifica que usaste la **App Password**, NO tu contraseña normal de Gmail
2. Copia la App Password sin espacios
3. Asegúrate de que la verificación en 2 pasos esté activa

### Error: "Could not connect to SMTP host"

**Causa:** Puerto bloqueado o configuración incorrecta

**Solución:**
1. Verifica que el puerto sea **587** (no 465 ni 25)
2. Verifica que `host = "smtp.gmail.com"`
3. Revisa el firewall de Windows

### No llega el email

**Causa:** Email en spam o configuración incorrecta

**Solución:**
1. Revisa la carpeta de **Spam/Correo no deseado**
2. Verifica los logs del backend para ver si se envió
3. Verifica que `from` tenga tu email

### Error: "Username and Password not accepted"

**Causa:** Verificación en 2 pasos no activada

**Solución:**
1. Ve a https://myaccount.google.com/security
2. Activa "Verificación en 2 pasos"
3. Genera una nueva App Password

## 📊 Verificar en Logs del Backend

Cuando se envíe un email, deberías ver en los logs:

```
[DefaultDispatcher-worker-1] INFO  EmailService - Enviando email a: usuario@ejemplo.com
[DefaultDispatcher-worker-1] INFO  EmailService - Email enviado exitosamente
```

Si hay error:
```
[DefaultDispatcher-worker-1] ERROR EmailService - Error al enviar email: Authentication failed
```

## 🔐 Seguridad

### ⚠️ IMPORTANTE: No subas credenciales a Git

Agrega `.env` al `.gitignore`:

```gitignore
# Variables de entorno
.env
*.env
!.env.example
```

### ✅ Mejores Prácticas

1. ✅ Usa App Password, nunca tu contraseña real
2. ✅ Usa variables de entorno en producción
3. ✅ No compartas tu App Password
4. ✅ Revoca App Passwords que no uses
5. ✅ Usa `.env.example` para documentar

## 🌐 Alternativas a Gmail

### SendGrid (Gratis hasta 100 emails/día)

```hocon
email {
    host = "smtp.sendgrid.net"
    port = 587
    username = "apikey"
    password = "TU_API_KEY_DE_SENDGRID"
    from = "noreply@tudominio.com"
}
```

### Mailgun (Gratis hasta 5,000 emails/mes)

```hocon
email {
    host = "smtp.mailgun.org"
    port = 587
    username = "postmaster@tu-dominio.mailgun.org"
    password = "TU_API_KEY_DE_MAILGUN"
    from = "MiPlan <noreply@tu-dominio.mailgun.org>"
}
```

### Outlook/Hotmail

```hocon
email {
    host = "smtp-mail.outlook.com"
    port = 587
    username = "tu-email@outlook.com"
    password = "tu-contraseña"
    from = "MiPlan <tu-email@outlook.com>"
}
```

## 📝 Plantilla del Email

El email que se envía está en:
`backend/src/main/kotlin/com/miplan/services/EmailService.kt`

Puedes personalizarlo editando el método `sendVerificationEmail()`.

## ✅ Checklist Final

- [ ] Verificación en 2 pasos activada en Gmail
- [ ] App Password generada y copiada
- [ ] `application.conf` actualizado con email y password
- [ ] Backend reiniciado
- [ ] Registro de nuevo usuario realizado
- [ ] Email recibido en bandeja de entrada
- [ ] Link de verificación funciona
- [ ] Login exitoso después de verificar

## 🎉 ¡Listo!

Ahora tu app enviará emails de verificación reales. Los usuarios recibirán un email al registrarse y deberán verificar su cuenta antes de poder hacer login.
