package com.recruitment.platform.service;

import com.recruitment.platform.dto.request.RegisterRequest;
import com.recruitment.platform.dto.request.LoginRequest;
import com.recruitment.platform.dto.response.LoginResponse;
import com.recruitment.platform.entity.User;

/**
 * Interface pour la logique d'authentification.
 */
public interface AuthService {
    
    /**
     * Inscrit un nouvel utilisateur (Candidat ou Recruteur) en base de données.
     */
    User register(RegisterRequest request);

    /**
     * Authentifie un utilisateur et retourne un jeton JWT de session.
     */
    LoginResponse login(LoginRequest request);
}
