package com.recruitment.platform.entity.enums;

/**
 * ============================================================
 * ENUM JOB STATUS — Statut d'une offre d'emploi
 * ============================================================
 *
 * Cycle de vie d'une offre :
 *
 * BROUILLON → l'offre est créée mais pas encore visible
 * ACTIVE    → l'offre est publiée et visible par les candidats
 * FERMEE    → l'offre n'accepte plus de candidatures
 * ARCHIVEE  → l'offre est archivée
 */
public enum JobStatus {
    BROUILLON,
    ACTIVE,
    FERMEE,
    ARCHIVEE
}
