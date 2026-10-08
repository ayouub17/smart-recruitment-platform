package com.recruitment.platform.dto.response;

import com.recruitment.platform.entity.enums.ExperienceLevel;
import com.recruitment.platform.entity.enums.JobStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour renvoyer une offre d'emploi.
 */
@Data
public class JobResponse {
    private Long id;
    private String titre;
    private String description;
    private String typeContrat;
    private String ville;
    private BigDecimal salaireMin;
    private BigDecimal salaireMax;
    private ExperienceLevel niveauRequis;
    private JobStatus statut;
    private LocalDateTime datePublication;
    private String entrepriseNom;
    private Long companyId;
    private String companyDescription;
    private String companySecteur;
    private String companyEmail;
    private String companySiteWeb;
    private String companyLogoUrl;
}
