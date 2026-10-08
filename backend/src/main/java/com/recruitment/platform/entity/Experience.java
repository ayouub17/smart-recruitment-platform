package com.recruitment.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * ============================================================
 * ENTITÉ EXPERIENCE — Table "experiences" dans PostgreSQL
 * ============================================================
 *
 * Représente une expérience professionnelle d'un candidat.
 *
 * Chaque candidat peut avoir plusieurs expériences (OneToMany).
 * Ces données peuvent être extraites automatiquement du CV
 * par le pipeline NLP, ou saisies manuellement.
 */
@Entity
@Table(name = "experiences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre du poste est obligatoire")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titre;

    /**
     * Nom de l'entreprise où l'expérience a été effectuée.
     */
    @Column(length = 150)
    private String entreprise;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    /**
     * Localisation du poste (ville, pays).
     */
    @Column(length = 100)
    private String localisation;

    // ============================================
    // RELATION
    // ============================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;
}
