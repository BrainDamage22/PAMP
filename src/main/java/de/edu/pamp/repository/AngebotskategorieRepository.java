package de.edu.pamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.edu.pamp.dto.Angebotskategorie;

/**
 * @author Tobias
 * 
 *         Schnittstelle für alle Datenbankoperationen für eine
 *         Angebotskategorie
 *
 */

@Repository
public interface AngebotskategorieRepository extends JpaRepository<Angebotskategorie, Integer> {
}