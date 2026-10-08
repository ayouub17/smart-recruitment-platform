package com.recruitment.platform.controller;

import com.recruitment.platform.dto.response.RecruiterProfileResponse;
import com.recruitment.platform.entity.Company;
import com.recruitment.platform.entity.Recruiter;
import com.recruitment.platform.repository.RecruiterRepository;
import com.recruitment.platform.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiters")
@RequiredArgsConstructor
public class RecruiterController {

    private final RecruiterRepository recruiterRepository;

    @GetMapping("/profile")
    public ResponseEntity<RecruiterProfileResponse> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Recruiter recruiter = recruiterRepository.findByUserId(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Profil recruteur introuvable !"));
                
        RecruiterProfileResponse response = new RecruiterProfileResponse();
        response.setId(recruiter.getId());
        response.setUserId(recruiter.getUser().getId());
        response.setNom(recruiter.getUser().getNom());
        response.setPrenom(recruiter.getUser().getPrenom());
        response.setEmail(recruiter.getUser().getEmail());
        response.setTelephone(recruiter.getUser().getTelephone());
        response.setPoste(recruiter.getPoste());
        response.setDepartement(recruiter.getDepartement());
        
        Company company = recruiter.getCompany();
        if (company != null) {
            response.setCompanyId(company.getId());
            response.setCompanyNom(company.getNom());
            response.setCompanyDescription(company.getDescription());
            response.setCompanySecteur(company.getSecteur());
            response.setCompanyEmail(company.getEmailContact());
            response.setCompanySiteWeb(company.getSiteWeb());
            response.setCompanyLogoUrl(company.getLogoUrl());
        }
        
        return ResponseEntity.ok(response);
    }
}
