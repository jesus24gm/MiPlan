-- Migración para añadir campos de recurrencia a la tabla tasks
-- Fecha: 16 de marzo de 2026

-- Añadir campos de recurrencia
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS recurrence_type VARCHAR(50) DEFAULT 'NONE';
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS recurrence_interval INTEGER DEFAULT 1;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS recurrence_days VARCHAR(50);
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS recurrence_end_date TIMESTAMP;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS is_recurring_instance BOOLEAN DEFAULT FALSE;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS parent_task_id INTEGER;

-- Crear índice para mejorar búsquedas de tareas recurrentes
CREATE INDEX IF NOT EXISTS idx_tasks_recurrence_type ON tasks(recurrence_type);
CREATE INDEX IF NOT EXISTS idx_tasks_parent_task_id ON tasks(parent_task_id);
CREATE INDEX IF NOT EXISTS idx_tasks_is_recurring_instance ON tasks(is_recurring_instance);

-- Comentarios para documentación
COMMENT ON COLUMN tasks.recurrence_type IS 'Tipo de recurrencia: NONE, DAILY, WEEKLY, MONTHLY, YEARLY';
COMMENT ON COLUMN tasks.recurrence_interval IS 'Intervalo de recurrencia (ej: cada 2 semanas)';
COMMENT ON COLUMN tasks.recurrence_days IS 'Días de la semana para recurrencia semanal (ej: 1,3,5 para lun,mié,vie)';
COMMENT ON COLUMN tasks.recurrence_end_date IS 'Fecha límite para dejar de generar instancias recurrentes';
COMMENT ON COLUMN tasks.is_recurring_instance IS 'Indica si esta tarea es una instancia generada automáticamente';
COMMENT ON COLUMN tasks.parent_task_id IS 'ID de la tarea padre si es una instancia recurrente';
