-- Migración para añadir campos de recurrencia a la tabla tasks (MySQL)
-- Fecha: 16 de marzo de 2026

-- Añadir campos de recurrencia (sintaxis MySQL)
ALTER TABLE tasks 
ADD COLUMN recurrence_type VARCHAR(50) DEFAULT 'NONE' NOT NULL,
ADD COLUMN recurrence_interval INT DEFAULT 1 NOT NULL,
ADD COLUMN recurrence_days VARCHAR(50) NULL,
ADD COLUMN recurrence_end_date DATETIME NULL,
ADD COLUMN is_recurring_instance TINYINT(1) DEFAULT 0 NOT NULL,
ADD COLUMN parent_task_id INT NULL;

-- Crear índices para mejorar búsquedas de tareas recurrentes
CREATE INDEX idx_tasks_recurrence_type ON tasks(recurrence_type);
CREATE INDEX idx_tasks_parent_task_id ON tasks(parent_task_id);
CREATE INDEX idx_tasks_is_recurring_instance ON tasks(is_recurring_instance);
