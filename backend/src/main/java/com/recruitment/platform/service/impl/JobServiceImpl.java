package com.recruitment.platform.service.impl;

import com.recruitment.platform.dto.request.JobRequest;
import com.recruitment.platform.dto.response.JobResponse;
import com.recruitment.platform.entity.Job;
import com.recruitment.platform.entity.Recruiter;
import com.recruitment.platform.entity.enums.ExperienceLevel;
import com.recruitment.platform.entity.enums.JobStatus;
import com.recruitment.platform.repository.JobRepository;
import com.recruitment.platform.repository.RecruiterRepository;
import com.recruitment.platform.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service JobService.
 */
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final RecruiterRepository recruiterRepository;

    @Override
    @Transactional
    public JobResponse createJob(JobRequest request, Long userId) {
        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profil recruteur introuvable pour l'utilisateur connecté !"));

        Job job = Job.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .typeContrat(request.getTypeContrat())
                .ville(request.getVille())
                .salaireMin(request.getSalaireMin())
                .salaireMax(request.getSalaireMax())
                .niveauRequis(request.getNiveauRequis())
                .statut(JobStatus.BROUILLON) // Par défaut en brouillon
                .company(recruiter.getCompany())
                .recruiter(recruiter)
                .build();

        Job savedJob = jobRepository.save(job);
        return mapToResponse(savedJob);
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long jobId, JobRequest request, Long userId) {
        Job job = getJobAndValidateOwnership(jobId, userId);
        
        job.setTitre(request.getTitre());
        job.setDescription(request.getDescription());
        job.setTypeContrat(request.getTypeContrat());
        job.setVille(request.getVille());
        job.setSalaireMin(request.getSalaireMin());
        job.setSalaireMax(request.getSalaireMax());
        job.setNiveauRequis(request.getNiveauRequis());
        
        return mapToResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId, Long userId) {
        Job job = getJobAndValidateOwnership(jobId, userId);
        jobRepository.delete(job);
    }

    @Override
    @Transactional
    public JobResponse publishJob(Long jobId, Long userId) {
        Job job = getJobAndValidateOwnership(jobId, userId);
        job.setStatut(JobStatus.ACTIVE);
        return mapToResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public JobResponse closeJob(Long jobId, Long userId) {
        Job job = getJobAndValidateOwnership(jobId, userId);
        job.setStatut(JobStatus.FERMEE);
        return mapToResponse(jobRepository.save(job));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getAllActiveJobs() {
        return jobRepository.findByStatut(JobStatus.ACTIVE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getJobsByCompany(Long companyId) {
        return jobRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> searchJobs(String ville, ExperienceLevel experienceLevel) {
        // Logique de recherche simple, configurable et améliorable
        if (ville != null && experienceLevel != null) {
            return jobRepository.findByVilleAndStatut(ville, JobStatus.ACTIVE).stream()
                    .filter(j -> j.getNiveauRequis() == experienceLevel)
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } else if (ville != null) {
            return jobRepository.findByVilleAndStatut(ville, JobStatus.ACTIVE).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } else if (experienceLevel != null) {
            return jobRepository.findByNiveauRequisAndStatut(experienceLevel, JobStatus.ACTIVE).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        return getAllActiveJobs();
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre d'emploi introuvable avec l'ID: " + id));
        return mapToResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getMyJobs(Long userId) {
        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profil recruteur introuvable pour l'utilisateur connecté !"));

        return jobRepository.findByRecruiterId(recruiter.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère l'offre et vérifie si le recruteur connecté en est l'auteur.
     */
    private Job getJobAndValidateOwnership(Long jobId, Long userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Offre d'emploi introuvable !"));
        
        if (!job.getRecruiter().getUser().getId().equals(userId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier cette offre d'emploi !");
        }
        return job;
    }

    /**
     * Mappe l'entité Job vers le DTO JobResponse.
     */
    private JobResponse mapToResponse(Job job) {
        JobResponse response = new JobResponse();
        response.setId(job.getId());
        response.setTitre(job.getTitre());
        response.setDescription(job.getDescription());
        response.setTypeContrat(job.getTypeContrat());
        response.setVille(job.getVille());
        response.setSalaireMin(job.getSalaireMin());
        response.setSalaireMax(job.getSalaireMax());
        response.setNiveauRequis(job.getNiveauRequis());
        response.setStatut(job.getStatut());
        response.setDatePublication(job.getDatePublication());
        if (job.getCompany() != null) {
            response.setEntrepriseNom(job.getCompany().getNom());
            response.setCompanyId(job.getCompany().getId());
            response.setCompanyDescription(job.getCompany().getDescription());
            response.setCompanySecteur(job.getCompany().getSecteur());
            response.setCompanyEmail(job.getCompany().getEmailContact());
            response.setCompanySiteWeb(job.getCompany().getSiteWeb());
            response.setCompanyLogoUrl(job.getCompany().getLogoUrl());
        }
        return response;
    }
}
