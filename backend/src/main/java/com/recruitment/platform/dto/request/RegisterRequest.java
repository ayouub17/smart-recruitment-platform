package com.recruitment.platform.dto.request;

import com.recruitment.platform.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO d'inscription envoyé par le frontend.
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format de l'email invalide")
    @Size(max = 150, message = "L'email ne doit pas dépasser 150 caractères")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, max = 100, message = "Le mot de passe doit contenir entre 6 et 100 caractères")
    private String password;

    private String telephone;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role; // CANDIDATE ou RECRUITER

    // --- Champs spécifiques au Recruteur ---
    private String entrepriseNom; // Si le recruteur crée une nouvelle entreprise
    private String departement;
    private String poste;
}
