-- Migração MySQL/MariaDB. Faça backup antes de executar em produção.
ALTER TABLE weekly_progress
    ADD COLUMN count_animo INT NOT NULL DEFAULT 0,
    ADD COLUMN count_feliz INT NOT NULL DEFAULT 0,
    ADD COLUMN count_apatia INT NOT NULL DEFAULT 0,
    ADD COLUMN count_raiva INT NOT NULL DEFAULT 0,
    ADD COLUMN count_triste INT NOT NULL DEFAULT 0;

-- Preserva o humor único registrado pelo modelo antigo como uma ocorrência.
UPDATE weekly_progress SET count_animo = 1 WHERE mood_state = 'ANIMO';
UPDATE weekly_progress SET count_feliz = 1 WHERE mood_state = 'FELIZ';
UPDATE weekly_progress SET count_apatia = 1 WHERE mood_state = 'APATIA';
UPDATE weekly_progress SET count_raiva = 1 WHERE mood_state = 'RAIVA';
UPDATE weekly_progress SET count_triste = 1 WHERE mood_state = 'TRISTE';

ALTER TABLE weekly_progress DROP COLUMN mood_state;
