package com.recruitment.platform.controller;

import com.recruitment.platform.dto.response.CandidateProfileResponse;
import com.recruitment.platform.entity.CV;
import com.recruitment.platform.entity.enums.ExperienceLevel;
import com.recruitment.platform.security.UserPrincipal;
import com.recruitment.platform.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller pour la gestion des candidats connectés de manière sécurisée.
 */
@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    /**
     * Récupère le profil complet du candidat connecté à partir du contexte de sécurité.
     */
    @GetMapping("/profile")
    public ResponseEntity<CandidateProfileResponse> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(candidateService.getProfile(userPrincipal.getId()));
    }

    /**
     * Met à jour le profil du candidat connecté.
     */
    @PutMapping("/profile")
    public ResponseEntity<CandidateProfileResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) ExperienceLevel niveauExperience) {
        return ResponseEntity.ok(candidateService.updateProfile(userPrincipal.getId(), adresse, bio, niveauExperience));
    }

    /**
     * Téléverse un CV (PDF, DOCX...) pour le candidat connecté.
     */
    @PostMapping("/cv/upload")
    public ResponseEntity<?> uploadCV(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file) {
        try {
            CV cv = candidateService.uploadCV(userPrincipal.getId(), file);
            return new ResponseEntity<>(cv, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/photo/upload")
    public ResponseEntity<?> uploadPhoto(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(candidateService.uploadPhoto(userPrincipal.getId(), file));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Définir le CV principal pour les candidatures.
     */
    @PutMapping("/cv/{cvId}/main")
    public ResponseEntity<?> setMainCV(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long cvId) {
        try {
            candidateService.setMainCV(userPrincipal.getId(), cvId);
            return ResponseEntity.ok("CV principal mis à jour avec succès !");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
