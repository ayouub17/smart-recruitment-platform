package com.recruitment.platform.entity;

import com.recruitment.platform.entity.enums.ExperienceLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ============================================================
 * ENTITÉ CANDIDATE — Table "candidates" dans PostgreSQL
 * ============================================================
 *
 * Profil détaillé d'un candidat. Chaque Candidate est lié
 * à exactement UN User (relation OneToOne).
 *
 * Contient :
 * - Informations personnelles (adresse, date de naissance, bio)
 * - Niveau d'expérience
 * - Photo de profil
 * - Liens vers ses CV, candidatures, compétences, expériences
 *
 * RELATIONS :
 * - User (OneToOne)        : le compte utilisateur
 * - CV (OneToMany)         : les CV uploadés
 * - Application (OneToMany): les candidatures soumises
 * - Skill (ManyToMany)     : les compétences
 * - Experience (OneToMany) : les expériences professionnelles
 */
@Entity
@Table(name = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relation OneToOne avec User.
     * @JoinColumn crée la colonne "user_id" dans la table candidates.
     * unique = true garantit qu'un User ne peut avoir qu'un seul profil Candidate.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 255)
    private String adresse;

    @Column(length = 255)
    private String photo;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     * Niveau d'expérience du candidat.
     * Stocké comme texte : "DEBUTANT", "INTERMEDIAIRE", "CONFIRME", "EXPERT"
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_experience", length = 20)
    private ExperienceLevel niveauExperience;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ============================================
    // RELATIONS
    // ============================================

    /**
     * Liste des CV uploadés par ce candidat.
     * OneToMany : un candidat peut avoir plusieurs CV.
     * orphanRemoval = true : si on retire un CV de la liste, il est supprimé de la base.
     */
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CV> cvs = new ArrayList<>();

    /**
     * Liste des candidatures de ce candidat.
     */
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    /**
     * Compétences du candidat (relation ManyToMany).
     * @JoinTable crée une table intermédiaire "candidate_skills"
     * avec deux colonnes : candidate_id et skill_id.
     * Pourquoi ManyToMany ? Un candidat peut avoir plusieurs compétences,
     * et une compétence peut appartenir à plusieurs candidats.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "candidate_skills",
        joinColumns = @JoinColumn(name = "candidate_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();

    /**
     * Expériences professionnelles du candidat.
     */
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Experience> experiences = new ArrayList<>();
}
