package com.recruitment.platform.dto.response;

import com.recruitment.platform.entity.enums.ApplicationStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour les candidatures.
 */
@Data
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitre;
    private String entrepriseNom;
    private Long candidateId;
    private String candidateNom;
    private String candidatePrenom;
    private String candidateEmail;
    private ApplicationStatus statut;
    private BigDecimal scoreMatching;
    private LocalDateTime datePostulation;
    private LocalDateTime dateEntretien;
    private String messageRecruteur;
    private String lettreMotivation;
    private String cvFichierNom;
    private String cvFichierChemin;
}
