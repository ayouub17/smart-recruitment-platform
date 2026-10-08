package com.recruitment.platform.service.impl;

import com.recruitment.platform.dto.request.RegisterRequest;
import com.recruitment.platform.dto.request.LoginRequest;
import com.recruitment.platform.dto.response.LoginResponse;
import com.recruitment.platform.entity.Candidate;
import com.recruitment.platform.entity.Company;
import com.recruitment.platform.entity.Recruiter;
import com.recruitment.platform.entity.User;
import com.recruitment.platform.entity.enums.Role;
import com.recruitment.platform.repository.CandidateRepository;
import com.recruitment.platform.repository.CompanyRepository;
import com.recruitment.platform.repository.RecruiterRepository;
import com.recruitment.platform.repository.UserRepository;
import com.recruitment.platform.security.JwtTokenProvider;
import com.recruitment.platform.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation complète et sécurisée du service d'authentification connectée à JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final RecruiterRepository recruiterRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé !");
        }

        // 1. Création de l'utilisateur de base
        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telephone(request.getTelephone())
                .role(request.getRole())
                .statut(true)
                .build();

        User savedUser = userRepository.save(user);

        // 2. Création du profil spécifique selon le rôle
        if (request.getRole() == Role.CANDIDATE) {
            Candidate candidate = Candidate.builder()
                    .user(savedUser)
                    .build();
            candidateRepository.save(candidate);
        } else if (request.getRole() == Role.RECRUITER) {
            Company company = null;
            if (request.getEntrepriseNom() != null && !request.getEntrepriseNom().isBlank()) {
                company = companyRepository.findByNom(request.getEntrepriseNom())
                        .orElseGet(() -> companyRepository.save(Company.builder().nom(request.getEntrepriseNom()).build()));
            }

            Recruiter recruiter = Recruiter.builder()
                    .user(savedUser)
                    .poste(request.getPoste())
                    .departement(request.getDepartement())
                    .company(company)
                    .build();
            recruiterRepository.save(recruiter);
        }

        return savedUser;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // Authentification de l'utilisateur avec Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Génération du token JWT réel
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        return new LoginResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getNom(),
                user.getPrenom(),
                user.getRole().name()
        );
    }
}
