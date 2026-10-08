package com.recruitment.platform.controller;

import com.recruitment.platform.dto.request.ApplicationRequest;
import com.recruitment.platform.dto.response.ApplicationResponse;
import com.recruitment.platform.entity.enums.ApplicationStatus;
import com.recruitment.platform.security.UserPrincipal;
import com.recruitment.platform.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;

/**
 * REST Controller pour la gestion des candidatures sécurisées.
 */
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * Soumet une nouvelle candidature (accès candidat).
     */
    @PostMapping
    public ResponseEntity<?> apply(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ApplicationRequest request) {
        try {
            ApplicationResponse response = applicationService.apply(request, userPrincipal.getId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Récupère la liste des candidatures soumises par le candidat connecté.
     */
    @GetMapping("/my-applications")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(applicationService.getApplicationsByCandidate(userPrincipal.getId()));
    }

    /**
     * Récupère la liste des candidatures reçues pour une offre spécifique (accès recruteur).
     */
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByJob(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId, userPrincipal.getId()));
    }

    /**
     * Met à jour le statut d'une candidature (accès recruteur).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        try {
            ApplicationResponse response = applicationService.updateStatus(id, status, userPrincipal.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/interview")
    public ResponseEntity<?> scheduleInterview(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestParam LocalDateTime dateEntretien,
            @RequestParam(required = false) String message) {
        try {
            return ResponseEntity.ok(applicationService.scheduleInterview(id, dateEntretien, message, userPrincipal.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
