package com.recruitment.platform.repository;

import com.recruitment.platform.entity.User;
import com.recruitment.platform.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * REPOSITORY USER — Accès aux données de la table "users"
 * ============================================================
 *
 * JpaRepository<User, Long> fournit GRATUITEMENT ces méthodes :
 * - findAll()         : récupérer tous les utilisateurs
 * - findById(Long id) : trouver un utilisateur par son ID
 * - save(User user)   : créer ou mettre à jour un utilisateur
 * - deleteById(Long)  : supprimer un utilisateur
 * - count()           : compter le nombre d'utilisateurs
 * - existsById(Long)  : vérifier si un utilisateur existe
 *
 * Les méthodes ci-dessous sont PERSONNALISÉES.
 * Spring Data JPA génère les requêtes SQL automatiquement
 * à partir du nom de la méthode ! Par exemple :
 * findByEmail → SELECT * FROM users WHERE email = ?
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son email.
     * Utilisé pour le login et la vérification d'unicité.
     * Optional = peut retourner un résultat vide (pas d'exception)
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà en base.
     * Utilisé lors de l'inscription pour éviter les doublons.
     */
    Boolean existsByEmail(String email);

    /**
     * Trouve tous les utilisateurs ayant un rôle spécifique.
     * Ex: findByRole(Role.CANDIDATE) → tous les candidats
     */
    List<User> findByRole(Role role);

    /**
     * Trouve les utilisateurs par statut (actif/inactif).
     * Utile pour l'admin qui gère les comptes.
     */
    List<User> findByStatut(Boolean statut);
}
