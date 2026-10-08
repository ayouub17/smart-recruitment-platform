package com.recruitment.platform.repository;

import com.recruitment.platform.entity.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY RECRUITER
 * Accès aux données de la table "recruiters".
 */
@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {

    Optional<Recruiter> findByUserId(Long userId);

    Boolean existsByUserId(Long userId);

    /**
     * Trouve tous les recruteurs d'une entreprise donnée.
     */
    List<Recruiter> findByCompanyId(Long companyId);
}
