-- ============================================
-- Script de creación de base de datos MiPlan
-- ============================================

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS miplan_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE miplan_db;

-- ============================================
-- Tabla: roles
-- ============================================
CREATE TABLE IF NOT EXISTS roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    INDEX idx_role_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: users
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role_id),
    INDEX idx_verification_token (verification_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: boards
-- ============================================
CREATE TABLE IF NOT EXISTS boards (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    color VARCHAR(20) DEFAULT '#E3F2FD',
    user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_board_user (user_id),
    INDEX idx_board_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: tasks
-- ============================================
CREATE TABLE IF NOT EXISTS tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    priority VARCHAR(50) DEFAULT 'MEDIUM',
    due_date DATETIME,
    board_id INT,
    created_by INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_task_created_by (created_by),
    INDEX idx_task_board (board_id),
    INDEX idx_task_status (status),
    INDEX idx_task_priority (priority),
    INDEX idx_task_due_date (due_date),
    INDEX idx_task_created (created_at),
    INDEX idx_task_user_status (created_by, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: notifications
-- ============================================
CREATE TABLE IF NOT EXISTS notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    task_id INT,
    message TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'SYSTEM',
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    INDEX idx_notification_user (user_id),
    INDEX idx_notification_task (task_id),
    INDEX idx_notification_read (is_read),
    INDEX idx_notification_user_read (user_id, is_read),
    INDEX idx_notification_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabla: user_tasks (Tareas compartidas)
-- ============================================
CREATE TABLE IF NOT EXISTS user_tasks (
    user_id INT NOT NULL,
    task_id INT NOT NULL,
    permission VARCHAR(20) DEFAULT 'view',
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, task_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    INDEX idx_user_task_user (user_id),
    INDEX idx_user_task_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Datos iniciales
-- ============================================

-- Insertar roles predefinidos
INSERT INTO roles (id, name, description) VALUES
(1, 'USER', 'Usuario estándar con acceso a funcionalidades básicas'),
(2, 'ADMIN', 'Administrador con acceso completo al sistema')
ON DUPLICATE KEY UPDATE name=name;

-- Insertar usuario administrador por defecto
-- Email: admin@miplan.com
-- Password: admin123 (hash BCrypt)
INSERT INTO users (email, password_hash, name, role_id, is_verified, verification_token)
VALUES (
    'admin@miplan.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYzpLHJ5jSW',
    'Administrador',
    2,
    TRUE,
    NULL
) ON DUPLICATE KEY UPDATE email=email;

-- ============================================
-- Vistas útiles (opcional)
-- ============================================

-- Vista de tareas con información del usuario y tablero
CREATE OR REPLACE VIEW v_tasks_detailed AS
SELECT 
    t.id,
    t.title,
    t.description,
    t.status,
    t.priority,
    t.due_date,
    t.created_at,
    t.updated_at,
    u.id AS user_id,
    u.name AS user_name,
    u.email AS user_email,
    b.id AS board_id,
    b.name AS board_name,
    b.color AS board_color
FROM tasks t
INNER JOIN users u ON t.created_by = u.id
LEFT JOIN boards b ON t.board_id = b.id;

-- Vista de estadísticas por usuario
CREATE OR REPLACE VIEW v_user_stats AS
SELECT 
    u.id AS user_id,
    u.name AS user_name,
    u.email AS user_email,
    COUNT(DISTINCT t.id) AS total_tasks,
    COUNT(DISTINCT CASE WHEN t.status = 'PENDING' THEN t.id END) AS pending_tasks,
    COUNT(DISTINCT CASE WHEN t.status = 'IN_PROGRESS' THEN t.id END) AS in_progress_tasks,
    COUNT(DISTINCT CASE WHEN t.status = 'COMPLETED' THEN t.id END) AS completed_tasks,
    COUNT(DISTINCT b.id) AS total_boards,
    COUNT(DISTINCT n.id) AS total_notifications,
    COUNT(DISTINCT CASE WHEN n.is_read = FALSE THEN n.id END) AS unread_notifications
FROM users u
LEFT JOIN tasks t ON u.id = t.created_by
LEFT JOIN boards b ON u.id = b.user_id
LEFT JOIN notifications n ON u.id = n.user_id
GROUP BY u.id, u.name, u.email;

-- ============================================
-- Procedimientos almacenados (opcional)
-- ============================================

DELIMITER //

-- Procedimiento para crear una tarea con notificación
CREATE PROCEDURE sp_create_task_with_notification(
    IN p_title VARCHAR(255),
    IN p_description TEXT,
    IN p_priority VARCHAR(50),
    IN p_due_date DATETIME,
    IN p_board_id INT,
    IN p_user_id INT
)
BEGIN
    DECLARE v_task_id INT;
    
    -- Insertar tarea
    INSERT INTO tasks (title, description, priority, due_date, board_id, created_by)
    VALUES (p_title, p_description, p_priority, p_due_date, p_board_id, p_user_id);
    
    SET v_task_id = LAST_INSERT_ID();
    
    -- Crear notificación
    INSERT INTO notifications (user_id, task_id, message, type)
    VALUES (p_user_id, v_task_id, CONCAT('Nueva tarea creada: ', p_title), 'TASK_ASSIGNED');
    
    SELECT v_task_id AS task_id;
END //

DELIMITER ;

-- ============================================
-- Triggers (opcional)
-- ============================================

DELIMITER //

-- Trigger para crear notificación cuando una tarea está próxima a vencer
CREATE TRIGGER tr_task_due_soon
AFTER UPDATE ON tasks
FOR EACH ROW
BEGIN
    IF NEW.due_date IS NOT NULL 
       AND NEW.status != 'COMPLETED' 
       AND NEW.due_date <= DATE_ADD(NOW(), INTERVAL 1 DAY)
       AND (OLD.due_date IS NULL OR OLD.due_date != NEW.due_date) THEN
        
        INSERT INTO notifications (user_id, task_id, message, type)
        VALUES (
            NEW.created_by,
            NEW.id,
            CONCAT('La tarea "', NEW.title, '" vence pronto'),
            'TASK_REMINDER'
        );
    END IF;
END //

DELIMITER ;

-- ============================================
-- Información del script
-- ============================================
SELECT 'Base de datos MiPlan creada exitosamente' AS mensaje;
SELECT VERSION() AS mysql_version;
SELECT DATABASE() AS database_name;
SELECT COUNT(*) AS total_tables FROM information_schema.tables WHERE table_schema = 'miplan_db';
