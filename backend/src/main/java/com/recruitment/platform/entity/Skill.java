package com.recruitment.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * ============================================================
 * ENTITÉ SKILL — Table "skills" dans PostgreSQL
 * ============================================================
 *
 * Représente une compétence (ex: "Java", "Python", "Gestion de projet").
 *
 * Relation ManyToMany avec Candidate :
 * - Un candidat peut avoir plusieurs compétences
 * - Une compétence peut appartenir à plusieurs candidats
 * - La table intermédiaire "candidate_skills" est gérée par JPA
 *
 * La catégorie permet de classer les compétences :
 * - "Technique" : Java, Python, SQL, etc.
 * - "Soft Skill" : Communication, Leadership, etc.
 * - "Langue" : Français, Anglais, etc.
 */
@Entity
@Table(name = "skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la compétence est obligatoire")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nom;

    /**
     * Catégorie de la compétence : Technique, Soft Skill, Langue, etc.
     */
    @Column(length = 50)
    private String categorie;

    /**
     * Côté inverse de la relation ManyToMany.
     * mappedBy = "skills" fait référence au champ skills dans Candidate.
     */
    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Candidate> candidates = new HashSet<>();
}
