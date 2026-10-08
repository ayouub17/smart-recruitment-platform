package com.recruitment.platform.repository;

import com.recruitment.platform.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY SKILL
 * Accès aux données de la table "skills".
 */
@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    /**
     * Trouve une compétence par son nom exact (ex: "Java").
     */
    Optional<Skill> findByNom(String nom);

    /**
     * Liste les compétences par catégorie (ex: "Technique").
     */
    List<Skill> findByCategorie(String categorie);
}
