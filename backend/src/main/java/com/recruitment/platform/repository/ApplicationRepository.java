package com.recruitment.platform.repository;

import com.recruitment.platform.entity.Application;
import com.recruitment.platform.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY APPLICATION
 * Accès aux données de la table "applications".
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * Liste toutes les candidatures d'un candidat.
     */
    List<Application> findByCandidateId(Long candidateId);

    /**
     * Liste toutes les candidatures pour une offre spécifique.
     */
    List<Application> findByJobId(Long jobId);

    /**
     * Trouve une candidature spécifique d'un candidat pour une offre donnée.
     * Utile pour éviter de postuler deux fois à la même offre.
     */
    Optional<Application> findByCandidateIdAndJobId(Long candidateId, Long jobId);

    /**
     * Liste les candidatures d'une offre filtrées par leur statut.
     */
    List<Application> findByJobIdAndStatut(Long jobId, ApplicationStatus statut);
}
