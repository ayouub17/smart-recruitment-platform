package com.recruitment.platform.entity;

import com.recruitment.platform.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * ============================================================
 * ENTITÉ USER — Table "users" dans PostgreSQL
 * ============================================================
 *
 * C'est l'entité de base pour TOUS les utilisateurs (candidats,
 * recruteurs, admins). Elle contient les informations communes :
 * email, mot de passe, nom, prénom, rôle.
 *
 * Un User peut être lié à :
 * - Un profil Candidate (si rôle = CANDIDATE)
 * - Un profil Recruiter (si rôle = RECRUITER)
 *
 * ANNOTATIONS EXPLIQUÉES :
 * ---------------------------------------------------------
 * @Entity         : Dit à JPA que cette classe = une table SQL
 * @Table          : Nom de la table dans PostgreSQL
 * @Id             : Clé primaire
 * @GeneratedValue : Auto-incrémentation (IDENTITY = SERIAL en PostgreSQL)
 * @Column         : Configuration de la colonne SQL
 * @Enumerated     : Stocke l'enum comme texte (pas comme nombre)
 * @CreationTimestamp : Hibernate met la date/heure automatiquement
 *
 * LOMBOK ANNOTATIONS :
 * ---------------------------------------------------------
 * @Data            : Génère getters, setters, toString, equals, hashCode
 * @NoArgsConstructor : Génère un constructeur sans paramètre (requis par JPA)
 * @AllArgsConstructor : Génère un constructeur avec tous les paramètres
 * @Builder         : Permet de créer un User avec le pattern Builder :
 *                    User.builder().email("...").nom("...").build()
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(name = "mot_de_passe", nullable = false)
    private String password;

    @Column(length = 20)
    private String telephone;

    /**
     * Le rôle détermine ce que l'utilisateur peut faire.
     * EnumType.STRING stocke "CANDIDATE", "RECRUITER" ou "ADMIN"
     * (pas un numéro comme 0, 1, 2 — c'est plus lisible en base)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    /**
     * Compte actif ou non.
     * L'admin peut désactiver un compte (statut = false).
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean statut = true;

    /**
     * Date de création du compte.
     * @CreationTimestamp : Hibernate met automatiquement la date
     * au moment de l'insertion en base.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ============================================
    // RELATIONS
    // ============================================

    /**
     * Relation OneToOne avec Candidate.
     * mappedBy = "user" signifie que c'est l'entité Candidate
     * qui possède la clé étrangère (user_id).
     * cascade = ALL : si on supprime le User, le Candidate est aussi supprimé.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Candidate candidate;

    /**
     * Relation OneToOne avec Recruiter.
     * Même logique que pour Candidate.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Recruiter recruiter;
}
