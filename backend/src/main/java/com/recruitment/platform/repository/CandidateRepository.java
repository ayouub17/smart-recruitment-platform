package com.recruitment.platform.repository;

import com.recruitment.platform.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * REPOSITORY CANDIDATE
 * Accès aux données de la table "candidates".
 */
@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    /**
     * Trouve un candidat par l'ID de son User associé.
     * Utilisé après le login pour charger le profil candidat.
     */
    Optional<Candidate> findByUserId(Long userId);

    /**
     * Vérifie si un profil candidat existe pour un User donné.
     */
    Boolean existsByUserId(Long userId);
}
