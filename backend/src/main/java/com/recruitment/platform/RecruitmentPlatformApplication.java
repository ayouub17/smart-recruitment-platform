package com.recruitment.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================
 * CLASSE PRINCIPALE - Point d'entrée de l'application
 * ============================================================
 *
 * @SpringBootApplication combine 3 annotations :
 * - @Configuration    : Cette classe peut définir des beans Spring
 * - @EnableAutoConfiguration : Spring configure automatiquement les composants
 *                              (ex: détecte PostgreSQL et configure la connexion)
 * - @ComponentScan    : Spring scanne ce package et ses sous-packages
 *                       pour trouver les @Controller, @Service, @Repository, etc.
 *
 * Quand on lance cette classe, Spring Boot :
 * 1. Démarre le serveur Tomcat intégré (port 8080)
 * 2. Se connecte à PostgreSQL
 * 3. Crée les tables à partir des entités JPA
 * 4. Enregistre tous les controllers, services, repositories
 * 5. L'API REST est prête à recevoir des requêtes !
 */
@SpringBootApplication
public class RecruitmentPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitmentPlatformApplication.class, args);
    }

}
