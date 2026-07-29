-- Amplía el ENUM existente en bases de datos H2 creadas antes del estado QA.
ALTER TABLE tareas ALTER COLUMN estado SET DATA TYPE ENUM('ABIERTO', 'CERRADO', 'DETENIDO', 'PROCESO', 'QA');
