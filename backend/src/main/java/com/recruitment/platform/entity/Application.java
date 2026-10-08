package com.recruitment.platform.entity;

import com.recruitment.platform.entity.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ============================================================
 * ENTITÉ APPLICATION — Table "applications" dans PostgreSQL
 * ============================================================
 *
 * Représente une candidature : un candidat qui postule à une offre.
 * C'est la table de liaison entre Candidate et Job.
 *
 * Contient :
 * - La lettre de motivation (optionnelle)
 * - Le score de matching (calculé par le moteur de recommandation)
 * - Le statut de la candidature (EN_ATTENTE → ACCEPTEE/REFUSEE)
 *
 * RELATIONS :
 * - Candidate (ManyToOne) : le candidat qui postule
 * - Job (ManyToOne)       : l'offre à laquelle il postule
 * - CV (ManyToOne)        : le CV utilisé pour cette candidature
 */
@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Lettre de motivation soumise avec la candidature.
     */
    @Column(name = "lettre_motivation", columnDefinition = "TEXT")
    private String lettreMotivation;

    /**
     * Score de correspondance entre le candidat et l'offre.
     * Calculé par le moteur de recommandation (0 à 100).
     * NUMERIC(5,2) permet des valeurs comme 87.50
     */
    @Column(name = "score_matching", precision = 5, scale = 2)
    private BigDecimal scoreMatching;

    /**
     * Statut de la candidature.
     * Par défaut : EN_ATTENTE (le candidat vient de postuler)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ApplicationStatus statut = ApplicationStatus.EN_ATTENTE;

    @CreationTimestamp
    @Column(name = "date_postulation", nullable = false, updatable = false)
    private LocalDateTime datePostulation;

    @Column(name = "date_entretien")
    private LocalDateTime dateEntretien;

    @Column(name = "message_recruteur", columnDefinition = "TEXT")
    private String messageRecruteur;

    // ============================================
    // RELATIONS
    // ============================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    /**
     * Le CV utilisé pour cette candidature spécifique.
     * Un candidat peut postuler avec différents CV selon l'offre.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id")
    private CV cv;
}
