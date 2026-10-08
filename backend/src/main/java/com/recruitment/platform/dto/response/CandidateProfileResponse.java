package com.recruitment.platform.dto.response;

import com.recruitment.platform.entity.enums.ExperienceLevel;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO complet de réponse du profil d'un Candidat.
 */
@Data
public class CandidateProfileResponse {
    private Long id;
    private Long userId;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private String photo;
    private LocalDate dateNaissance;
    private String bio;
    private ExperienceLevel niveauExperience;
    private List<SkillDto> skills;
    private List<ExperienceDto> experiences;
    private List<CvDto> cvs;

    @Data
    public static class SkillDto {
        private Long id;
        private String nom;
        private String categorie;
    }

    @Data
    public static class ExperienceDto {
        private Long id;
        private String titre;
        private String entreprise;
        private String description;
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private String localisation;
    }

    @Data
    public static class CvDto {
        private Long id;
        private String nomFichier;
        private String cheminFichier;
        private Boolean estPrincipal;
        private String extractedData;
    }
}
