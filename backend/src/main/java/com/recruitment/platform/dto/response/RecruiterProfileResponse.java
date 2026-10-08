package com.recruitment.platform.dto.response;

import lombok.Data;

@Data
public class RecruiterProfileResponse {
    private Long id;
    private Long userId;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    
    // Recruiter specific
    private String poste;
    private String departement;
    
    // Company specific
    private Long companyId;
    private String companyNom;
    private String companyDescription;
    private String companySecteur;
    private String companyEmail;
    private String companySiteWeb;
    private String companyLogoUrl;
}
