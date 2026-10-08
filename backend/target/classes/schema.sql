-- La base existante avait été créée avant l'ajout du statut ENTRETIEN_PROGRAMME.
-- Cette migration est idempotente : elle peut être exécutée à chaque démarrage.
ALTER TABLE applications DROP CONSTRAINT IF EXISTS applications_statut_check;

ALTER TABLE applications
    ADD CONSTRAINT applications_statut_check
    CHECK (statut IN (
        'EN_ATTENTE',
        'EN_COURS',
        'ENTRETIEN_PROGRAMME',
        'ACCEPTEE',
        'REFUSEE',
        'ARCHIVEE'
    ));
