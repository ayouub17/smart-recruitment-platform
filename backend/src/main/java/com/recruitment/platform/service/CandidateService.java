package com.recruitment.platform.service;

import com.recruitment.platform.dto.response.CandidateProfileResponse;
import com.recruitment.platform.entity.CV;
import com.recruitment.platform.entity.enums.ExperienceLevel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface pour la gestion du profil Candidat et de ses CVs.
 */
public interface CandidateService {

    /**
     * Récupère le profil complet d'un candidat à partir de l'ID User.
     */
    CandidateProfileResponse getProfile(Long userId);

    /**
     * Met à jour les informations du profil du candidat.
     */
    CandidateProfileResponse updateProfile(Long userId, String adresse, String bio, ExperienceLevel niveauExperience);

    /**
     * Enregistre un CV physiquement et en base de données.
     */
    CV uploadCV(Long userId, MultipartFile file);

    /**
     * Définit un CV comme étant le document principal pour postuler.
     */
    void setMainCV(Long userId, Long cvId);

    CandidateProfileResponse uploadPhoto(Long userId, MultipartFile file);

    /**
     * Ajoute ou met à jour les données extraites (JSON) sur un CV.
     * Appelée par le pipeline NLP/Python d'extraction de données.
     */
    void updateCvExtractedData(Long cvId, String jsonResult);
}
