package com.recruitment.platform.service.impl;

import com.recruitment.platform.dto.response.CandidateProfileResponse;
import com.recruitment.platform.entity.CV;
import com.recruitment.platform.entity.Candidate;
import com.recruitment.platform.entity.enums.ExperienceLevel;
import com.recruitment.platform.repository.CVRepository;
import com.recruitment.platform.repository.CandidateRepository;
import com.recruitment.platform.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implémentation du service CandidateService.
 */
@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final CVRepository cvRepository;

    // Dossier local temporaire pour stocker les CVs
    private final String UPLOAD_DIR = "uploads/cvs/";
    private final String PHOTO_UPLOAD_DIR = "uploads/photos/";

    @Override
    @Transactional(readOnly = true)
    public CandidateProfileResponse getProfile(Long userId) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profil candidat introuvable !"));
        return mapToProfileResponse(candidate);
    }

    @Override
    @Transactional
    public CandidateProfileResponse updateProfile(Long userId, String adresse, String bio, ExperienceLevel niveauExperience) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profil candidat introuvable !"));

        candidate.setAdresse(adresse);
        candidate.setBio(bio);
        candidate.setNiveauExperience(niveauExperience);

        return mapToProfileResponse(candidateRepository.save(candidate));
    }

    @Override
    @Transactional
    public CV uploadCV(Long userId, MultipartFile file) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profil candidat introuvable !"));

        if (file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide !");
        }

        try {
            // Créer le dossier d'upload s'il n'existe pas
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique pour éviter les collisions
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(uniqueFilename);

            // Copier le fichier physiquement
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Si c'est le premier CV, on le met principal par défaut
            boolean hasCvs = !cvRepository.findByCandidateId(candidate.getId()).isEmpty();

            CV cv = CV.builder()
                    .nomFichier(originalFilename)
                    .cheminFichier(filePath.toString())
                    .typeFichier(file.getContentType())
                    .estPrincipal(!hasCvs)
                    .candidate(candidate)
                    .build();

            return cvRepository.save(cv);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement physique du CV : " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void setMainCV(Long userId, Long cvId) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profil candidat introuvable !"));

        // Récupérer tous les CVs du candidat
        java.util.List<CV> cvs = cvRepository.findByCandidateId(candidate.getId());
        
        boolean cvFound = false;
        for (CV cv : cvs) {
            if (cv.getId().equals(cvId)) {
                cv.setEstPrincipal(true);
                cvFound = true;
            } else {
                cv.setEstPrincipal(false);
            }
        }

        if (!cvFound) {
            throw new RuntimeException("Ce CV n'existe pas ou ne vous appartient pas !");
        }

        cvRepository.saveAll(cvs);
    }

    @Override
    @Transactional
    public CandidateProfileResponse uploadPhoto(Long userId, MultipartFile file) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profil candidat introuvable !"));
        if (file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new RuntimeException("Veuillez choisir une image valide.");
        }
        try {
            Path uploadPath = Paths.get(PHOTO_UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            Path filePath = uploadPath.resolve(UUID.randomUUID() + extension);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            candidate.setPhoto("uploads/photos/" + filePath.getFileName());
            return mapToProfileResponse(candidateRepository.save(candidate));
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de la photo : " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateCvExtractedData(Long cvId, String jsonResult) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV introuvable avec l'ID: " + cvId));
        cv.setExtractedData(jsonResult);
        cvRepository.save(cv);
    }

    private CandidateProfileResponse mapToProfileResponse(Candidate candidate) {
        CandidateProfileResponse response = new CandidateProfileResponse();
        response.setId(candidate.getId());
        response.setUserId(candidate.getUser().getId());
        response.setNom(candidate.getUser().getNom());
        response.setPrenom(candidate.getUser().getPrenom());
        response.setEmail(candidate.getUser().getEmail());
        response.setTelephone(candidate.getUser().getTelephone());
        response.setAdresse(candidate.getAdresse());
        response.setPhoto(candidate.getPhoto());
        response.setDateNaissance(candidate.getDateNaissance());
        response.setBio(candidate.getBio());
        response.setNiveauExperience(candidate.getNiveauExperience());

        // Mappage des skills
        response.setSkills(candidate.getSkills().stream().map(s -> {
            CandidateProfileResponse.SkillDto dto = new CandidateProfileResponse.SkillDto();
            dto.setId(s.getId());
            dto.setNom(s.getNom());
            dto.setCategorie(s.getCategorie());
            return dto;
        }).collect(Collectors.toList()));

        // Mappage des expériences
        response.setExperiences(candidate.getExperiences().stream().map(exp -> {
            CandidateProfileResponse.ExperienceDto dto = new CandidateProfileResponse.ExperienceDto();
            dto.setId(exp.getId());
            dto.setTitre(exp.getTitre());
            dto.setEntreprise(exp.getEntreprise());
            dto.setDescription(exp.getDescription());
            dto.setDateDebut(exp.getDateDebut());
            dto.setDateFin(exp.getDateFin());
            dto.setLocalisation(exp.getLocalisation());
            return dto;
        }).collect(Collectors.toList()));

        // Mappage des CVs
        response.setCvs(candidate.getCvs().stream().map(cv -> {
            CandidateProfileResponse.CvDto dto = new CandidateProfileResponse.CvDto();
            dto.setId(cv.getId());
            dto.setNomFichier(cv.getNomFichier());
            dto.setCheminFichier(cv.getCheminFichier());
            dto.setEstPrincipal(cv.getEstPrincipal());
            dto.setExtractedData(cv.getExtractedData());
            return dto;
        }).collect(Collectors.toList()));

        return response;
    }
}
