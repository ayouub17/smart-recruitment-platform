package com.recruitment.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * ENTITÉ COMPANY — Table "companies" dans PostgreSQL
 * ============================================================
 *
 * Représente une entreprise qui recrute.
 *
 * RELATIONS :
 * - Recruiter (OneToMany) : une entreprise peut avoir plusieurs recruteurs
 * - Job (OneToMany)       : une entreprise peut avoir plusieurs offres
 */
@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de l'entreprise est obligatoire")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String secteur;

    @Column(name = "site_web", length = 150)
    private String siteWeb;

    @Column(name = "email_contact", length = 150)
    private String emailContact;

    @Column(length = 20)
    private String telephone;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ============================================
    // RELATIONS
    // ============================================

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Recruiter> recruiters = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Job> jobs = new ArrayList<>();
}
