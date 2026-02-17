# 📧 Configurar Resend para Emails

## ✅ Cambios Realizados

- ✅ Agregado `ResendEmailService` con API REST
- ✅ Emails con HTML bonito y responsive
- ✅ Interfaz `IEmailService` para flexibilidad
- ✅ Código subido a GitHub
- ✅ Railway redesplegará automáticamente

---

## 🚀 Pasos para Configurar Resend

### 1️⃣ Crear Cuenta en Resend

1. Ve a: https://resend.com/signup
2. Regístrate con tu email
3. Verifica tu email
4. Completa el onboarding

---

### 2️⃣ Crear API Key

1. En el dashboard de Resend, ve a **"API Keys"**
2. Click en **"Create API Key"**
3. **Name:** `MiPlan Railway`
4. **Permission:** `Sending access`
5. Click **"Create"**
6. **Copia el API Key** (empieza con `re_...`)
   - ⚠️ Solo se muestra una vez, guárdalo bien

---

### 3️⃣ Configurar Variables en Railway

1. Ve a Railway Dashboard: https://railway.app/dashboard
2. Abre tu proyecto **MiPlan**
3. Click en el servicio **backend**
4. Ve a la pestaña **"Variables"**
5. Agrega esta nueva variable:

```
RESEND_API_KEY=re_TU_API_KEY_AQUI
```

Reemplaza `re_TU_API_KEY_AQUI` con el API Key que copiaste.

**Opcional:** Si quieres personalizar el remitente:
```
RESEND_FROM=MiPlan <onboarding@resend.dev>
```

---

### 4️⃣ Esperar Redeploy

Railway detectará los cambios de GitHub y redesplegará automáticamente (2-3 minutos).

Monitorea en: Railway > Backend > Deployments

---

### 5️⃣ Probar el Registro

1. Abre la app en Android Studio
2. **Run > Run 'app'**
3. Registra un **nuevo usuario** con un email real
4. **Revisa tu bandeja de entrada**
5. Deberías recibir un email bonito de MiPlan
6. Click en **"Verificar mi cuenta"**
7. Haz login en la app

---

## 📧 Emails que Enviará la App

### Email de Verificación

- ✅ Diseño HTML bonito con colores de MiPlan
- ✅ Botón grande para verificar
- ✅ Enlace alternativo por si el botón no funciona
- ✅ Responsive (se ve bien en móvil)

### Email de Recordatorio de Tarea

- ✅ Diseño con color naranja (alerta)
- ✅ Muestra título y fecha límite de la tarea
- ✅ Responsive

---

## 🆓 Plan Gratuito de Resend

- ✅ **3,000 emails/mes** gratis
- ✅ Sin tarjeta de crédito
- ✅ Perfecto para desarrollo y producción pequeña
- ✅ Emails ilimitados desde `onboarding@resend.dev`

---

## 🔧 Verificar que Funciona

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
1. El API Key es correcto
2. La variable `RESEND_API_KEY` está configurada
3. El backend se redesplegó

---

## 🌐 Dominio Personalizado (Opcional - Avanzado)

Para usar tu propio dominio (ej: `noreply@miplan.com`):

### Paso 1: Agregar Dominio en Resend

1. Ve a: https://resend.com/domains
2. Click **"Add Domain"**
3. Ingresa tu dominio (ej: `miplan.com`)
4. Resend te dará registros DNS

### Paso 2: Configurar DNS

Agrega estos registros en tu proveedor de dominio:
- SPF
- DKIM
- DMARC

### Paso 3: Verificar

Resend verificará automáticamente (puede tardar hasta 72 horas).

### Paso 4: Actualizar Variable

```
RESEND_FROM=MiPlan <noreply@miplan.com>
```

---

## 🆘 Troubleshooting

### Error: "API key is invalid"

**Solución:**
- Verifica que copiaste el API Key completo
- Asegúrate de que empieza con `re_`
- Crea un nuevo API Key si es necesario

### Error: "Domain not verified"

**Solución:**
- Usa `onboarding@resend.dev` (dominio de prueba de Resend)
- O verifica tu dominio personalizado

### Emails no llegan

**Solución:**
1. Revisa la carpeta de spam
2. Verifica los logs de Railway
3. Verifica en Resend Dashboard > Logs

### Backend no se redesplega

**Solución:**
1. Ve a Railway > Backend > Deployments
2. Click en **"Redeploy"** manualmente

---

## ✅ Checklist Final

- [ ] Cuenta de Resend creada
- [ ] API Key generado y copiado
- [ ] Variable `RESEND_API_KEY` agregada en Railway
- [ ] Backend redesplegado
- [ ] Logs muestran "✅ Email enviado"
- [ ] Email recibido en bandeja de entrada
- [ ] Usuario verificado correctamente
- [ ] Login funciona

---

## 🎉 ¡Listo!

Ahora tu app puede enviar emails desde cualquier lugar del mundo, sin depender de Gmail ni configuraciones complicadas de SMTP.

**Ventajas de Resend:**
- ✅ Más confiable que Gmail SMTP
- ✅ Mejor deliverability (menos spam)
- ✅ API moderna y fácil de usar
- ✅ Dashboard con estadísticas
- ✅ Logs de todos los emails enviados

---

## 📚 Recursos

- **Resend Dashboard:** https://resend.com/
- **Documentación:** https://resend.com/docs
- **API Reference:** https://resend.com/docs/api-reference
- **Ejemplos:** https://resend.com/docs/send-with-kotlin

---

**¿Necesitas ayuda? Revisa los logs de Railway o el dashboard de Resend para diagnosticar problemas.** 🚀
