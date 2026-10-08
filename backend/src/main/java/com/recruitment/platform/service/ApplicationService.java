package com.recruitment.platform.service;

import com.recruitment.platform.dto.request.ApplicationRequest;
import com.recruitment.platform.dto.response.ApplicationResponse;
import com.recruitment.platform.entity.enums.ApplicationStatus;

import java.util.List;
import java.time.LocalDateTime;

/**
 * Interface pour la logique métier des candidatures.
 */
public interface ApplicationService {

    /**
     * Permet à un candidat de postuler à une offre.
     */
    ApplicationResponse apply(ApplicationRequest request, Long userId);

    /**
     * Modifie le statut d'une candidature (ex: de EN_ATTENTE à ACCEPTEE).
     */
    ApplicationResponse updateStatus(Long applicationId, ApplicationStatus status, Long userId);

    ApplicationResponse scheduleInterview(Long applicationId, LocalDateTime dateEntretien, String message, Long userId);

    /**
     * Récupère toutes les candidatures d'un candidat.
     */
    List<ApplicationResponse> getApplicationsByCandidate(Long userId);

    /**
     * Récupère toutes les candidatures pour une offre (accès recruteur).
     */
    List<ApplicationResponse> getApplicationsByJob(Long jobId, Long userId);
}
