# 🔧 Variables de Entorno en Railway

## 📍 Dónde Configurarlas

### En Railway Dashboard:

1. Ve a: https://railway.app/dashboard
2. Abre tu proyecto **"MiPlan"**
3. Click en el servicio **backend** (NO en MySQL)
4. Click en la pestaña **"Variables"**
5. Aquí puedes agregar/editar variables

---

## ✅ Variables Requeridas para MiPlan

Copia y pega estas variables en Railway:

### 🗄️ Base de Datos (Automáticas desde MySQL)

```
DATABASE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}
DATABASE_USER=${{MySQL.MYSQLUSER}}
DATABASE_PASSWORD=${{MySQL.MYSQLPASSWORD}}
```

**Nota:** Railway reemplaza automáticamente `${{MySQL.VARIABLE}}` con los valores de tu base de datos MySQL.

### 🔐 Seguridad JWT

```
JWT_SECRET=miplan-production-secret-key-change-this-to-something-very-random-123456789
```

**Importante:** Cambia esto por algo aleatorio y seguro.

### 📧 Email (Gmail SMTP)

```
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=jeez24897@gmail.com
EMAIL_PASSWORD=eircvkpvdhhiunoa
EMAIL_FROM=MiPlan <jeez24897@gmail.com>
```

### 🌐 URL Base (Tu Dominio de Railway)

```
BASE_URL=https://miplan-production.up.railway.app
```

### 🔌 Puerto

```
PORT=8080
```

**Nota:** Railway puede configurar esto automáticamente, pero es bueno tenerlo explícito.

---

## 📋 Lista Completa (Copiar y Pegar)

Para agregar todas de una vez, usa el formato de Railway:

```env
DATABASE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}
DATABASE_USER=${{MySQL.MYSQLUSER}}
DATABASE_PASSWORD=${{MySQL.MYSQLPASSWORD}}
JWT_SECRET=miplan-production-secret-key-change-this-to-something-very-random-123456789
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=jeez24897@gmail.com
EMAIL_PASSWORD=eircvkpvdhhiunoa
EMAIL_FROM=MiPlan <jeez24897@gmail.com>
BASE_URL=https://miplan-production.up.railway.app
PORT=8080
```

---

## 🔄 Cómo Agregar Variables en Railway

### Método 1: Una por Una

1. Click en **"+ New Variable"**
2. **Variable:** Nombre de la variable (ej: `JWT_SECRET`)
3. **Value:** Valor de la variable
4. Click **"Add"**
5. Repite para cada variable

### Método 2: Modo Raw (Más Rápido)

1. Click en **"Raw Editor"** (icono de código)
2. Pega todas las variables en formato:
   ```
   VARIABLE=valor
   OTRA_VARIABLE=otro_valor
   ```
3. Click **"Update Variables"**

---

## ⚠️ Importante

### Variables Sensibles

Estas variables contienen información sensible:
- `DATABASE_PASSWORD`
- `JWT_SECRET`
- `EMAIL_PASSWORD`

**NO las compartas** ni las subas a GitHub.

### Redeploy Automático

Cuando cambies variables en Railway:
- ✅ Railway redesplegará automáticamente
- ⏱️ Espera 1-2 minutos a que termine
- 🔍 Verifica los logs para confirmar que inició correctamente

---

## 🔍 Verificar Variables

### En Railway:

1. Ve a **Variables**
2. Verás todas las variables configuradas
3. Los valores sensibles estarán ocultos (●●●●●)

### En los Logs:

Después de desplegar, verifica en los logs:
```
MiPlan Backend iniciado correctamente
Responding at http://0.0.0.0:8080
```

---

## 🆘 Troubleshooting

### Error: "Can't connect to database"

**Solución:**
- Verifica que MySQL esté corriendo en Railway
- Verifica las variables `DATABASE_*`
- Asegúrate de usar la sintaxis `${{MySQL.VARIABLE}}`

### Error: "Email sending failed"

**Solución:**
- Verifica `EMAIL_USERNAME` y `EMAIL_PASSWORD`
- Asegúrate de usar App Password de Gmail (no tu contraseña normal)

### Error: "JWT validation failed"

**Solución:**
- Verifica que `JWT_SECRET` esté configurado
- Debe ser el mismo en todos los despliegues

---

## 📚 Recursos

- Railway Docs: https://docs.railway.app/develop/variables
- Railway Dashboard: https://railway.app/dashboard
- Tu Proyecto: https://railway.app/project/[TU_PROJECT_ID]

---

## ✅ Checklist

Después de configurar las variables:

- [ ] Todas las variables agregadas en Railway
- [ ] Railway redesplegó automáticamente
- [ ] Backend inició correctamente (ver logs)
- [ ] MySQL conectado
- [ ] Dominio generado y configurado en `BASE_URL`
- [ ] Probar endpoint: `https://miplan-production.up.railway.app/health`
