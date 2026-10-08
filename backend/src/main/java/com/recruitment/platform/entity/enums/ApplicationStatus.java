package com.recruitment.platform.entity.enums;

/**
 * ============================================================
 * ENUM APPLICATION STATUS — Statut d'une candidature
 * ============================================================
 *
 * Cycle de vie d'une candidature :
 *
 * EN_ATTENTE → le candidat vient de postuler, en attente de réponse
 * EN_COURS   → le recruteur examine la candidature
 * ACCEPTEE   → la candidature est acceptée (entretien programmé, etc.)
 * REFUSEE    → la candidature est refusée
 * ARCHIVEE   → la candidature est archivée (terminée)
 */
public enum ApplicationStatus {
    EN_ATTENTE,
    EN_COURS,
    ENTRETIEN_PROGRAMME,
    ACCEPTEE,
    REFUSEE,
    ARCHIVEE
}
