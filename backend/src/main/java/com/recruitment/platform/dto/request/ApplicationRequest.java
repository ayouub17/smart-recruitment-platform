package com.recruitment.platform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de requête pour soumettre une candidature.
 */
@Data
public class ApplicationRequest {

    @NotNull(message = "L'ID de l'offre est obligatoire")
    private Long jobId;

    private String lettreMotivation;

    /**
     * L'ID du CV à utiliser pour postuler.
     * Si nul, on utilisera le CV principal par défaut du candidat.
     */
    private Long cvId;
}
