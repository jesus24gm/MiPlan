# 📧 Configurar Mailtrap para Emails

## ✅ Ventajas de Mailtrap

- ✅ **1000 emails/mes gratis**
- ✅ **No requiere dominio**
- ✅ **Configuración en 5 minutos**
- ✅ **Funciona desde Railway sin problemas**
- ✅ **Dashboard para ver emails enviados**

---

## 🚀 Pasos para Configurar

### 1️⃣ Crear Cuenta en Mailtrap

1. Ve a: https://mailtrap.io/register/signup
2. Regístrate con tu email
3. Verifica tu email
4. Completa el onboarding

---

### 2️⃣ Obtener Credenciales SMTP

1. En el dashboard de Mailtrap, ve a **"Sending Domains"** o **"Email API"**
2. Click en **"SMTP Settings"** o **"Transactional Email"**
3. Verás las credenciales:

```
Host: live.smtp.mailtrap.io
Port: 587
Username: api
Password: [tu_token_aquí]
```

4. **Copia estos valores**

---

### 3️⃣ Configurar Variables en Railway

1. Ve a Railway Dashboard: https://railway.app/
2. Abre tu proyecto **MiPlan**
3. Click en el servicio **backend**
4. Ve a la pestaña **"Variables"**

#### Eliminar Variable de Resend:

Si tienes `RESEND_API_KEY`, **elimínala** (o déjala, el código detectará automáticamente cuál usar).

#### Actualizar Variables de Email:

Actualiza estas variables con los valores de Mailtrap:

```
EMAIL_HOST=live.smtp.mailtrap.io
EMAIL_PORT=587
EMAIL_USERNAME=api
EMAIL_PASSWORD=[tu_token_de_mailtrap]
EMAIL_FROM=MiPlan <noreply@miplan.app>
```

**Importante:** 
- Reemplaza `[tu_token_de_mailtrap]` con el token que copiaste
- Puedes usar cualquier email en `EMAIL_FROM` (ej: `noreply@miplan.app`)

---

### 4️⃣ Esperar Redeploy

Railway detectará los cambios y reiniciará automáticamente (1-2 minutos).

Monitorea en: Railway > Backend > Deployments

---

### 5️⃣ Probar el Registro

1. Abre la app en Android Studio
2. **Run > Run 'app'**
3. Registra un **nuevo usuario** con cualquier email
4. **Revisa los logs de Railway**
5. Deberías ver: `✅ Email de verificación enviado a: ...`

---

### 6️⃣ Ver Email en Mailtrap Dashboard

1. Ve a Mailtrap Dashboard
2. Click en **"Email Log"** o **"Emails"**
3. Verás el email enviado
4. Click para ver el contenido
5. **Copia el enlace de verificación** del email
6. Pégalo en tu navegador para verificar el usuario

---

## 🔍 Verificar que Funciona

### En los Logs de Railway:

Busca mensajes como:
```
✅ Email de verificación enviado a: usuario@example.com
```

Si ves:
```
❌ Error al enviar email: ...
```

Verifica:
1. Las credenciales de Mailtrap son correctas
2. Las variables están bien configuradas en Railway
3. El backend se redesplegó

---

## 📊 Dashboard de Mailtrap

En el dashboard de Mailtrap puedes:
- ✅ Ver todos los emails enviados
- ✅ Ver el contenido HTML de cada email
- ✅ Ver estadísticas de envío
- ✅ Verificar que los emails se enviaron correctamente

---

## 🎯 Flujo Completo de Prueba

### 1. Registrar Usuario

En la app:
- Email: `test@example.com`
- Password: `test123`
- Nombre: `Test User`

### 2. Ver Email en Mailtrap

1. Ve a Mailtrap Dashboard
2. Click en el email más reciente
3. Verás el email bonito de verificación

### 3. Copiar Enlace de Verificación

En el email, busca el enlace que empieza con:
```
https://miplan-production.up.railway.app/api/auth/verify/...
```

### 4. Verificar Usuario

1. Copia el enlace completo
2. Pégalo en tu navegador
3. Deberías ver una página de éxito

### 5. Hacer Login

Vuelve a la app y haz login con:
- Email: `test@example.com`
- Password: `test123`

---

## 🔧 Troubleshooting

### Error: "Could not connect to SMTP host"

**Solución:**
- Verifica que `EMAIL_HOST` sea `live.smtp.mailtrap.io`
- Verifica que `EMAIL_PORT` sea `587`

### Error: "Authentication failed"

**Solución:**
- Verifica que `EMAIL_USERNAME` sea `api`
- Verifica que `EMAIL_PASSWORD` sea el token correcto de Mailtrap
- Genera un nuevo token en Mailtrap si es necesario

### Emails no aparecen en Mailtrap

**Solución:**
1. Revisa los logs de Railway
2. Verifica que el email se envió sin errores
3. Refresca el dashboard de Mailtrap

---

## 🆓 Plan Gratuito de Mailtrap

- ✅ **1,000 emails/mes** gratis
- ✅ **Sin tarjeta de crédito**
- ✅ **Perfecto para desarrollo y producción pequeña**
- ✅ **Dashboard con logs y estadísticas**

---

## 🚀 Migrar a Producción (Futuro)

Cuando quieras usar un dominio propio:

1. Consigue un dominio
2. Verifica el dominio en Mailtrap
3. Actualiza `EMAIL_FROM` con tu dominio
4. O migra a Resend/SendGrid con dominio verificado

---

## ✅ Checklist Final

- [ ] Cuenta de Mailtrap creada
- [ ] Credenciales SMTP copiadas
- [ ] Variables actualizadas en Railway
- [ ] Backend redesplegado
- [ ] Logs muestran "✅ Email enviado"
- [ ] Email visible en Mailtrap Dashboard
- [ ] Usuario verificado correctamente
- [ ] Login funciona

---

## 🎉 ¡Listo!

Ahora tu app puede enviar emails a **cualquier dirección** sin restricciones de dominio.

**Ventajas de Mailtrap:**
- ✅ Más fácil que Resend (no requiere dominio)
- ✅ Dashboard para ver todos los emails
- ✅ Perfecto para desarrollo y producción
- ✅ Funciona desde Railway sin problemas

---

**¿Necesitas ayuda? Revisa los logs de Railway o el dashboard de Mailtrap para diagnosticar problemas.** 🚀
