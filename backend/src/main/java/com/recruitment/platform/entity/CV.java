package com.recruitment.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * ============================================================
 * ENTITÉ CV — Table "cvs" dans PostgreSQL
 * ============================================================
 *
 * Représente un fichier CV uploadé par un candidat.
 *
 * Stocke :
 * - Les métadonnées du fichier (nom, chemin, type)
 * - Les données extraites par le pipeline NLP (en JSON)
 * - Un flag pour indiquer le CV principal
 *
 * Le champ extractedData contiendra les informations extraites
 * automatiquement par le pipeline Python (compétences, expériences,
 * diplômes, langues, etc.) au format JSON.
 */
@Entity
@Table(name = "cvs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom du fichier original (ex: "mon_cv_2024.pdf")
     */
    @Column(name = "nom_fichier", nullable = false, length = 255)
    private String nomFichier;

    /**
     * Chemin de stockage sur le serveur (ex: "/uploads/cvs/abc123.pdf")
     */
    @Column(name = "chemin_fichier", nullable = false, length = 255)
    private String cheminFichier;

    /**
     * Type de fichier : PDF, DOCX, etc.
     */
    @Column(name = "type_fichier", length = 20)
    private String typeFichier;

    /**
     * Données extraites par le pipeline NLP.
     * Stocké en JSON (type TEXT dans PostgreSQL).
     * Exemple : {"competences": ["Java", "Python"], "experience": 5, ...}
     */
    @Column(name = "extracted_data", columnDefinition = "TEXT")
    private String extractedData;

    /**
     * Indique si c'est le CV principal du candidat.
     * Un candidat peut avoir plusieurs CV mais un seul principal.
     */
    @Column(name = "est_principal", nullable = false)
    @Builder.Default
    private Boolean estPrincipal = false;

    @CreationTimestamp
    @Column(name = "date_upload", nullable = false, updatable = false)
    private LocalDateTime dateUpload;

    // ============================================
    // RELATION
    // ============================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;
}
