package com.recruitment.platform.service;

import com.recruitment.platform.dto.request.JobRequest;
import com.recruitment.platform.dto.response.JobResponse;
import com.recruitment.platform.entity.Job;
import com.recruitment.platform.entity.enums.ExperienceLevel;

import java.util.List;

/**
 * Interface pour la logique métier des offres d'emploi.
 */
public interface JobService {

    /**
     * Crée une offre d'emploi sous statut BROUILLON (associée au recruteur connecté).
     */
    JobResponse createJob(JobRequest request, Long userId);

    /**
     * Modifie une offre d'emploi.
     */
    JobResponse updateJob(Long jobId, JobRequest request, Long userId);

    /**
     * Supprime une offre d'emploi.
     */
    void deleteJob(Long jobId, Long userId);

    /**
     * Publie une offre d'emploi (statut passe à ACTIVE).
     */
    JobResponse publishJob(Long jobId, Long userId);

    /**
     * Ferme une offre d'emploi (statut passe à FERMEE).
     */
    JobResponse closeJob(Long jobId, Long userId);

    /**
     * Récupère toutes les offres d'emploi actives pour les candidats.
     */
    List<JobResponse> getAllActiveJobs();

    /**
     * Récupère les offres d'emploi d'une entreprise spécifique.
     */
    List<JobResponse> getJobsByCompany(Long companyId);

    /**
     * Recherche avancée d'offres actives.
     */
    List<JobResponse> searchJobs(String ville, ExperienceLevel experienceLevel);

    /**
     * Récupère une offre par son ID.
     */
    JobResponse getJobById(Long id);

    /** Offres crÃ©Ã©es par le recruteur connectÃ©, quel que soit leur statut. */
    List<JobResponse> getMyJobs(Long userId);
}
