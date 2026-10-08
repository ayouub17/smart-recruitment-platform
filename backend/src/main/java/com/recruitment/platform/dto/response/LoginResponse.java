package com.recruitment.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO de réponse renvoyé après un login réussi.
 * Contient le jeton JWT et les informations de base de l'utilisateur connecté.
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private String role;
}
