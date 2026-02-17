-- ============================================
-- Verificar estado de la base de datos Railway
-- ============================================

-- Ver qué base de datos estamos usando
SELECT DATABASE() AS 'Base de datos actual';

-- Ver todas las bases de datos disponibles
SHOW DATABASES;

-- Cambiar explícitamente a railway
USE railway;

-- Ver todas las tablas en railway
SHOW TABLES;

-- Ver estructura de la tabla users si existe
SHOW CREATE TABLE users;

-- Contar registros en cada tabla
SELECT 'roles' AS tabla, COUNT(*) AS registros FROM roles
UNION ALL
SELECT 'users', COUNT(*) FROM users
UNION ALL
SELECT 'boards', COUNT(*) FROM boards
UNION ALL
SELECT 'tasks', COUNT(*) FROM tasks
UNION ALL
SELECT 'notifications', COUNT(*) FROM notifications
UNION ALL
SELECT 'user_tasks', COUNT(*) FROM user_tasks;
