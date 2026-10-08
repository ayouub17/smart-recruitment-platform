package com.recruitment.platform.dto.request;

import com.recruitment.platform.entity.enums.ExperienceLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de création/mise à jour d'une offre d'emploi.
 */
@Data
public class JobRequest {

    @NotBlank(message = "Le titre de l'offre est obligatoire")
    @Size(max = 150)
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotBlank(message = "Le type de contrat est obligatoire")
    @Size(max = 50)
    private String typeContrat; // CDI, CDD, Stage...

    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100)
    private String ville;

    private BigDecimal salaireMin;
    private BigDecimal salaireMax;

    @NotNull(message = "Le niveau d'expérience requis est obligatoire")
    private ExperienceLevel niveauRequis;

    private LocalDate dateExpiration;
}
