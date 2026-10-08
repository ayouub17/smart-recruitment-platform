package com.recruitment.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * ============================================================
 * ENTITÉ RECRUITER — Table "recruiters" dans PostgreSQL
 * ============================================================
 *
 * Profil d'un recruteur. Lié à UN User et à UNE Company.
 *
 * Un recruteur peut :
 * - Créer et publier des offres d'emploi
 * - Consulter et évaluer les candidats
 * - Programmer des entretiens
 *
 * RELATIONS :
 * - User (OneToOne)    : le compte utilisateur
 * - Company (ManyToOne): l'entreprise du recruteur
 *   (plusieurs recruteurs peuvent appartenir à la même entreprise)
 */
@Entity
@Table(name = "recruiters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Département du recruteur au sein de l'entreprise.
     * Ex: "Ressources Humaines", "IT", "Marketing"
     */
    @Column(length = 100)
    private String departement;

    @Column(length = 100)
    private String poste;

    /**
     * Relation ManyToOne avec Company.
     * Plusieurs recruteurs peuvent appartenir à la même entreprise.
     * @JoinColumn crée la colonne "company_id" dans la table recruiters.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
