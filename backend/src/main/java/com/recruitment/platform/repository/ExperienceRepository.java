package com.recruitment.platform.repository;

import com.recruitment.platform.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * REPOSITORY EXPERIENCE
 * Accès aux données de la table "experiences".
 */
@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    /**
     * Récupère le parcours professionnel (expériences) d'un candidat dans l'ordre chronologique.
     */
    List<Experience> findByCandidateIdOrderByDateDebutDesc(Long candidateId);
}
