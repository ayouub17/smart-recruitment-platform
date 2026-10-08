package com.recruitment.platform.entity.enums;

/**
 * ============================================================
 * ENUM ROLE — Définit les 3 rôles d'utilisateur
 * ============================================================
 *
 * Un enum est un type qui ne peut prendre que des valeurs prédéfinies.
 * Ici, chaque utilisateur a UN rôle parmi les 3 :
 *
 * - CANDIDATE : peut uploader son CV, rechercher et postuler aux offres
 * - RECRUITER : peut créer des offres, voir et évaluer les candidats
 * - ADMIN     : peut gérer tous les utilisateurs et superviser la plateforme
 *
 * Dans la base de données, ce sera stocké comme une chaîne de caractères
 * (ex: "CANDIDATE") grâce à @Enumerated(EnumType.STRING) dans l'entité User.
 */
public enum Role {
    CANDIDATE,
    RECRUITER,
    ADMIN
}
