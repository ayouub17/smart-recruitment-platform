package com.recruitment.platform.repository;

import com.recruitment.platform.entity.Job;
import com.recruitment.platform.entity.enums.ExperienceLevel;
import com.recruitment.platform.entity.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * REPOSITORY JOB
 * Accès aux données de la table "jobs".
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    /**
     * Récupère les offres d'emploi selon leur statut (ex: ACTIVE).
     */
    List<Job> findByStatut(JobStatus statut);

    /**
     * Récupère les offres publiées par une entreprise spécifique.
     */
    List<Job> findByCompanyId(Long companyId);

    List<Job> findByRecruiterId(Long recruiterId);

    /**
     * Récupère les offres d'emploi par ville et statut.
     */
    List<Job> findByVilleAndStatut(String ville, JobStatus statut);

    /**
     * Récupère les offres d'emploi par niveau requis et statut.
     */
    List<Job> findByNiveauRequisAndStatut(ExperienceLevel niveauRequis, JobStatus statut);

}
