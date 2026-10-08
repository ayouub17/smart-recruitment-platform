package com.recruitment.platform.controller;

import com.recruitment.platform.entity.Company;
import com.recruitment.platform.entity.Recruiter;
import com.recruitment.platform.repository.CompanyRepository;
import com.recruitment.platform.repository.RecruiterRepository;
import com.recruitment.platform.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final RecruiterRepository recruiterRepository;
    private final String LOGO_UPLOAD_DIR = "uploads/photos/";

    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        return companyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/logo/upload")
    public ResponseEntity<?> uploadLogo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file) {
        
        Recruiter recruiter = recruiterRepository.findByUserId(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Profil recruteur introuvable !"));
                
        Company company = recruiter.getCompany();
        if (company == null) {
            return ResponseEntity.badRequest().body("Vous n'êtes associé à aucune entreprise.");
        }

        if (file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("Veuillez choisir une image valide.");
        }
        try {
            Path uploadPath = Paths.get(LOGO_UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            Path filePath = uploadPath.resolve(UUID.randomUUID() + extension);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            company.setLogoUrl("uploads/photos/" + filePath.getFileName());
            return ResponseEntity.ok(companyRepository.save(company));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'enregistrement de l'image : " + e.getMessage());
        }
    }
}
