-- ============================================
-- Script de creación de base de datos MiPlan para Railway
-- ============================================

-- Railway ya tiene la base de datos "railway" creada
-- No necesitamos CREATE DATABASE

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
