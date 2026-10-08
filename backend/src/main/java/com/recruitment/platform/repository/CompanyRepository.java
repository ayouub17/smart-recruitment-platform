package com.recruitment.platform.repository;

import com.recruitment.platform.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * REPOSITORY COMPANY
 * Accès aux données de la table "companies".
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * Trouve une entreprise par son nom (sensible à la casse).
     */
    Optional<Company> findByNom(String nom);

    /**
     * Vérifie si une entreprise existe déjà avec ce nom.
     */
    Boolean existsByNom(String nom);
}
