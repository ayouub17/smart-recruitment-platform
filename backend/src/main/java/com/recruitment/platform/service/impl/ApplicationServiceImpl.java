package com.recruitment.platform.service.impl;

import com.recruitment.platform.dto.request.ApplicationRequest;
import com.recruitment.platform.dto.response.ApplicationResponse;
import com.recruitment.platform.entity.*;
import com.recruitment.platform.entity.enums.ApplicationStatus;
import com.recruitment.platform.repository.ApplicationRepository;
import com.recruitment.platform.repository.CVRepository;
import com.recruitment.platform.repository.CandidateRepository;
import com.recruitment.platform.repository.JobRepository;
import com.recruitment.platform.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implémentation du service ApplicationService.
 */
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final CVRepository cvRepository;

    /**
     * Stop-words français à ignorer lors du calcul de correspondance.
     */
    private static final Set<String> STOP_WORDS = Set.of(
            "avec", "dans", "pour", "plus", "aussi", "comme", "mais", "sont", "nous",
            "vous", "leur", "cette", "etre", "être", "avoir", "fait", "faire", "tout",
            "tous", "très", "tres", "bien", "peut", "même", "meme", "sans", "elle",
            "elles", "il", "ils", "une", "des", "les", "aux", "par", "sur", "sous",
            "entre", "vers", "chez", "dont", "sera", "seront", "notre", "votre",
            "autre", "autres", "après", "apres", "avant", "encore", "alors", "ainsi",
            "depuis", "quand", "qui", "que", "quel", "quelle", "quels", "quelles",
            "comment", "chaque", "toute", "toutes", "moins", "selon", "lors", "doit",
            "doivent", "afin", "celle", "ceux", "cela", "poste", "profil", "recherche",
            "offre", "emploi", "candidat", "entreprise", "travail", "experience", "niveau"
    );

    @Override
    @Transactional
    public ApplicationResponse apply(ApplicationRequest request, Long userId) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profil candidat introuvable !"));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Offre d'emploi introuvable !"));

        // Vérifier si le candidat a déjà postulé à cette offre
        if (applicationRepository.findByCandidateIdAndJobId(candidate.getId(), job.getId()).isPresent()) {
            throw new RuntimeException("Vous avez déjà postulé à cette offre d'emploi !");
        }

        // Récupérer le CV à associer
        CV cv = null;
        if (request.getCvId() != null) {
            cv = cvRepository.findById(request.getCvId())
                    .orElseThrow(() -> new RuntimeException("CV spécifié introuvable !"));
            if (!cv.getCandidate().getId().equals(candidate.getId())) {
                throw new RuntimeException("Ce CV ne vous appartient pas !");
            }
        } else {
            // Utiliser le CV principal par défaut
            cv = cvRepository.findByCandidateIdAndEstPrincipalTrue(candidate.getId())
                    .orElseThrow(() -> new RuntimeException("Veuillez téléverser un CV ou spécifier un CV principal avant de postuler !"));
        }

        // ============================================================
        // CALCUL DU SCORE DE CORRESPONDANCE (MATCHING) — MULTI-CRITÈRES
        // ============================================================
        // Axe 1 : Niveau d'expérience (30%)
        // Axe 2 : Compétences et mots-clés du profil (40%)
        // Axe 3 : Données extraites du CV (30%)

        double scoreExperience = 0.0;
        double scoreKeywords = 0.0;
        double scoreCvData = 0.0;

        // --- AXE 1 : Correspondance du niveau d'expérience (max 30 points) ---
        if (job.getNiveauRequis() != null && candidate.getNiveauExperience() != null) {
            int diff = candidate.getNiveauExperience().ordinal() - job.getNiveauRequis().ordinal();
            if (diff >= 0) {
                scoreExperience = 30.0; // Niveau suffisant ou supérieur
            } else if (diff == -1) {
                scoreExperience = 18.0; // Un niveau en dessous
            } else {
                scoreExperience = 8.0;  // Deux niveaux ou plus en dessous
            }
        } else {
            scoreExperience = 15.0; // Niveau inconnu → score neutre
        }

        // --- Préparation des données textuelles de l'offre ---
        String jobText = (job.getTitre() + " " + (job.getDescription() != null ? job.getDescription() : "")).toLowerCase();
        Set<String> jobKeywords = extractKeywords(jobText);

        // --- AXE 2 : Compétences et mots-clés du profil candidat (max 40 points) ---
        // Construire le texte du profil (bio + skills + titres d'expériences)
        StringBuilder profileTextBuilder = new StringBuilder();
        Set<String> candidateSkillNames = new HashSet<>();

        if (candidate.getBio() != null) {
            profileTextBuilder.append(candidate.getBio()).append(" ");
        }
        if (candidate.getSkills() != null) {
            for (Skill skill : candidate.getSkills()) {
                String skillName = skill.getNom().toLowerCase().trim();
                candidateSkillNames.add(skillName);
                profileTextBuilder.append(skill.getNom()).append(" ");
                if (skill.getCategorie() != null) {
                    profileTextBuilder.append(skill.getCategorie()).append(" ");
                }
            }
        }
        if (candidate.getExperiences() != null) {
            for (Experience exp : candidate.getExperiences()) {
                if (exp.getTitre() != null) profileTextBuilder.append(exp.getTitre()).append(" ");
                if (exp.getDescription() != null) profileTextBuilder.append(exp.getDescription()).append(" ");
                if (exp.getEntreprise() != null) profileTextBuilder.append(exp.getEntreprise()).append(" ");
            }
        }

        Set<String> profileKeywords = extractKeywords(profileTextBuilder.toString().toLowerCase());

        // Calcul de la correspondance mots-clés
        if (!jobKeywords.isEmpty()) {
            int matched = 0;
            for (String keyword : jobKeywords) {
                if (profileKeywords.contains(keyword)) {
                    matched++;
                }
                // Bonus : vérifier si un skill du candidat contient ce mot-clé
                for (String skill : candidateSkillNames) {
                    if (skill.contains(keyword) || keyword.contains(skill)) {
                        matched++;
                        break;
                    }
                }
            }
            double ratio = Math.min((double) matched / jobKeywords.size(), 1.0);
            scoreKeywords = ratio * 40.0;
        }

        // --- AXE 3 : Données extraites du CV (max 30 points) ---
        if (cv.getExtractedData() != null && !cv.getExtractedData().isBlank()) {
            String extractedText = cv.getExtractedData().toLowerCase();

            // Tenter de parser le JSON pour extraire les compétences structurées
            Set<String> cvKeywords = new HashSet<>();
            try {
                // Extraction simple des valeurs depuis le JSON (sans dépendance Jackson)
                // Cherche les patterns "competences": [...], "skills": [...], etc.
                cvKeywords.addAll(extractJsonArrayValues(extractedText, "competences"));
                cvKeywords.addAll(extractJsonArrayValues(extractedText, "skills"));
                cvKeywords.addAll(extractJsonArrayValues(extractedText, "langues"));
                cvKeywords.addAll(extractJsonArrayValues(extractedText, "formations"));
                cvKeywords.addAll(extractJsonArrayValues(extractedText, "diplomes"));
            } catch (Exception e) {
                // En cas d'erreur de parsing, utiliser le texte brut
            }

            // Ajouter aussi les mots-clés extraits du texte brut du CV
            cvKeywords.addAll(extractKeywords(extractedText));

            if (!jobKeywords.isEmpty() && !cvKeywords.isEmpty()) {
                int cvMatched = 0;
                for (String keyword : jobKeywords) {
                    if (cvKeywords.contains(keyword)) {
                        cvMatched++;
                    }
                }
                double cvRatio = Math.min((double) cvMatched / jobKeywords.size(), 1.0);
                scoreCvData = cvRatio * 30.0;
            }
        } else {
            // Pas de données CV extraites → on redistribue une partie sur les keywords
            scoreCvData = scoreKeywords > 0 ? (scoreKeywords / 40.0) * 10.0 : 0.0;
        }

        // --- Score final ---
        double totalScore = Math.min(scoreExperience + scoreKeywords + scoreCvData, 100.0);
        // Arrondir à 2 décimales
        totalScore = Math.round(totalScore * 100.0) / 100.0;

        BigDecimal calculatedScore = BigDecimal.valueOf(totalScore);
        Application application = Application.builder()
                .candidate(candidate)
                .job(job)
                .cv(cv)
                .lettreMotivation(request.getLettreMotivation())
                .statut(ApplicationStatus.EN_ATTENTE)
                .scoreMatching(calculatedScore)
                .build();

        Application savedApplication = applicationRepository.save(application);
        return mapToResponse(savedApplication);
    }

    /**
     * Extrait les mots-clés significatifs d'un texte en filtrant les stop-words.
     */
    private Set<String> extractKeywords(String text) {
        String[] words = text.split("[^a-zà-ÿ0-9+#.]+");
        Set<String> keywords = new HashSet<>();
        for (String word : words) {
            if (word.length() > 2 && !STOP_WORDS.contains(word)) {
                keywords.add(word);
            }
        }
        return keywords;
    }

    /**
     * Extraction simplifiée des valeurs d'un tableau JSON à partir d'une clé.
     * Ex: "competences": ["java", "python"] → retourne {"java", "python"}
     */
    private Set<String> extractJsonArrayValues(String json, String key) {
        Set<String> values = new HashSet<>();
        String searchPattern = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchPattern);
        if (keyIndex == -1) return values;

        int bracketStart = json.indexOf('[', keyIndex);
        if (bracketStart == -1) return values;

        int bracketEnd = json.indexOf(']', bracketStart);
        if (bracketEnd == -1) return values;

        String arrayContent = json.substring(bracketStart + 1, bracketEnd);
        String[] items = arrayContent.split(",");
        for (String item : items) {
            String cleaned = item.trim().replaceAll("[\"'\\s]", "").toLowerCase();
            if (!cleaned.isEmpty() && cleaned.length() > 1) {
                values.add(cleaned);
                // Aussi ajouter les sous-mots pour les compétences composées
                String[] subWords = cleaned.split("[^a-zà-ÿ0-9+#.]+");
                for (String sub : subWords) {
                    if (sub.length() > 2) values.add(sub);
                }
            }
        }
        return values;
    }

    @Override
    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, ApplicationStatus status, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable !"));

        // Vérifier que le recruteur connecté est bien l'auteur de l'offre
        if (!application.getJob().getRecruiter().getUser().getId().equals(userId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier le statut de cette candidature !");
        }

        application.setStatut(status);
        return mapToResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional
    public ApplicationResponse scheduleInterview(Long applicationId, LocalDateTime dateEntretien, String message, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable !"));
        if (!application.getJob().getRecruiter().getUser().getId().equals(userId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à gérer cette candidature !");
        }
        if (dateEntretien == null || dateEntretien.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La date de l'entretien doit être dans le futur.");
        }
        application.setDateEntretien(dateEntretien);
        application.setMessageRecruteur(message);
        application.setStatut(ApplicationStatus.ENTRETIEN_PROGRAMME);
        return mapToResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByCandidate(Long userId) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profil candidat introuvable !"));
        
        return applicationRepository.findByCandidateId(candidate.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByJob(Long jobId, Long userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Offre d'emploi introuvable !"));

        // Vérifier l'accès recruteur
        if (!job.getRecruiter().getUser().getId().equals(userId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à voir les candidatures de cette offre !");
        }

        return applicationRepository.findByJobId(jobId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ApplicationResponse mapToResponse(Application app) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(app.getId());
        response.setJobId(app.getJob().getId());
        response.setJobTitre(app.getJob().getTitre());
        if (app.getJob().getCompany() != null) {
            response.setEntrepriseNom(app.getJob().getCompany().getNom());
        }
        response.setCandidateId(app.getCandidate().getId());
        response.setCandidateNom(app.getCandidate().getUser().getNom());
        response.setCandidatePrenom(app.getCandidate().getUser().getPrenom());
        response.setCandidateEmail(app.getCandidate().getUser().getEmail());
        response.setStatut(app.getStatut());
        response.setScoreMatching(app.getScoreMatching());
        response.setDatePostulation(app.getDatePostulation());
        response.setDateEntretien(app.getDateEntretien());
        response.setMessageRecruteur(app.getMessageRecruteur());
        response.setLettreMotivation(app.getLettreMotivation());
        if (app.getCv() != null) {
            response.setCvFichierNom(app.getCv().getNomFichier());
            response.setCvFichierChemin(app.getCv().getCheminFichier());
        }
        return response;
    }
}
