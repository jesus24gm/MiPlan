-- Migración: Agregar columna image_url a tabla tasks
-- Fecha: 2026-02-18

-- Agregar columna image_url
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS image_url VARCHAR(500);

-- Verificar que se agregó correctamente
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name = 'tasks' AND column_name = 'image_url';
