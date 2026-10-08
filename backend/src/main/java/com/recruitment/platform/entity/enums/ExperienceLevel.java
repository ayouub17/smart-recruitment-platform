package com.recruitment.platform.entity.enums;

/**
 * ============================================================
 * ENUM EXPERIENCE LEVEL — Niveau d'expérience du candidat
 * ============================================================
 *
 * Utilisé dans le profil du candidat et dans les offres d'emploi
 * pour filtrer par niveau d'expérience requis.
 *
 * DEBUTANT      → 0-1 ans d'expérience (Junior)
 * INTERMEDIAIRE → 2-4 ans d'expérience (Mid-level)
 * CONFIRME      → 5-7 ans d'expérience (Senior)
 * EXPERT        → 8+ ans d'expérience (Lead/Expert)
 */
public enum ExperienceLevel {
    DEBUTANT,
    INTERMEDIAIRE,
    CONFIRME,
    EXPERT
}
