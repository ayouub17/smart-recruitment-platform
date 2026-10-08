package com.recruitment.platform.entity;

import com.recruitment.platform.entity.enums.ExperienceLevel;
import com.recruitment.platform.entity.enums.JobStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * ENTITÉ JOB — Table "jobs" dans PostgreSQL
 * ============================================================
 *
 * Représente une offre d'emploi créée par un recruteur.
 *
 * Cycle de vie :
 * BROUILLON → ACTIVE (publiée) → FERMEE (clôturée) → ARCHIVEE
 *
 * RELATIONS :
 * - Company (ManyToOne)      : l'entreprise qui propose l'offre
 * - Recruiter (ManyToOne)    : le recruteur qui a créé l'offre
 * - Application (OneToMany)  : les candidatures reçues pour cette offre
 */
@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre de l'offre est obligatoire")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titre;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    /**
     * Type de contrat : CDI, CDD, Stage, Freelance, etc.
     */
    @Column(name = "type_contrat", length = 50)
    private String typeContrat;

    @Column(length = 100)
    private String ville;

    @Column(name = "salaire_min", precision = 10, scale = 2)
    private BigDecimal salaireMin;

    @Column(name = "salaire_max", precision = 10, scale = 2)
    private BigDecimal salaireMax;

    /**
     * Niveau d'expérience requis pour cette offre.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_requis", length = 20)
    private ExperienceLevel niveauRequis;

    /**
     * Statut de l'offre (BROUILLON, ACTIVE, FERMEE, ARCHIVEE).
     * Par défaut, une offre est créée en BROUILLON.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private JobStatus statut = JobStatus.BROUILLON;

    @CreationTimestamp
    @Column(name = "date_publication", updatable = false)
    private LocalDateTime datePublication;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    // ============================================
    // RELATIONS
    // ============================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private Recruiter recruiter;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();
}
