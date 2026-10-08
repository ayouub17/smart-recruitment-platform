package com.recruitment.platform.repository;

import com.recruitment.platform.entity.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY CV
 * Accès aux données de la table "cvs".
 */
@Repository
public interface CVRepository extends JpaRepository<CV, Long> {

    /**
     * Récupère tous les CVs d'un candidat.
     */
    List<CV> findByCandidateId(Long candidateId);

    /**
     * Trouve le CV principal configuré par un candidat.
     */
    Optional<CV> findByCandidateIdAndEstPrincipalTrue(Long candidateId);
}
