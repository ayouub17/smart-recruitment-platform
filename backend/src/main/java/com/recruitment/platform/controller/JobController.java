package com.recruitment.platform.controller;

import com.recruitment.platform.dto.request.JobRequest;
import com.recruitment.platform.dto.response.JobResponse;
import com.recruitment.platform.entity.enums.ExperienceLevel;
import com.recruitment.platform.security.UserPrincipal;
import com.recruitment.platform.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller pour la gestion des offres d'emploi.
 */
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    /**
     * Récupère toutes les offres d'emploi actives (accès public).
     */
    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllActiveJobs() {
        return ResponseEntity.ok(jobService.getAllActiveJobs());
    }

    @GetMapping("/recruiter/offers")
    public ResponseEntity<List<JobResponse>> getMyJobs(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(jobService.getMyJobs(userPrincipal.getId()));
    }

    /**
     * Récupère une offre spécifique par son ID (accès public).
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    /**
     * Recherche multicritère d'offres actives (accès public).
     */
    @GetMapping("/search")
    public ResponseEntity<List<JobResponse>> searchJobs(
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) ExperienceLevel niveauExperience) {
        return ResponseEntity.ok(jobService.searchJobs(ville, niveauExperience));
    }

    /**
     * Crée une offre d'emploi en BROUILLON (accès recruteur).
     */
    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody JobRequest request) {
        return new ResponseEntity<>(jobService.createJob(request, userPrincipal.getId()), HttpStatus.CREATED);
    }

    /**
     * Modifie une offre d'emploi.
     */
    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.updateJob(id, request, userPrincipal.getId()));
    }

    /**
     * Supprime une offre d'emploi.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        jobService.deleteJob(id, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Publie une offre d'emploi (BROUILLON -> ACTIVE).
     */
    @PutMapping("/{id}/publish")
    public ResponseEntity<JobResponse> publishJob(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        return ResponseEntity.ok(jobService.publishJob(id, userPrincipal.getId()));
    }

    /**
     * Ferme une offre d'emploi (ACTIVE -> FERMEE).
     */
    @PutMapping("/{id}/close")
    public ResponseEntity<JobResponse> closeJob(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        return ResponseEntity.ok(jobService.closeJob(id, userPrincipal.getId()));
    }

    /**
     * Récupère les offres d'une entreprise spécifique.
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<JobResponse>> getJobsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(jobService.getJobsByCompany(companyId));
    }
}
