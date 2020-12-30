package de.edu.pamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.edu.pamp.dto.Angebot;

/**
 * 
 * @author Tobias
 * 
 *         Schnittstelle für alle Datenbankoperationen für ein Angebot
 */
@Repository
public interface AngebotRepository extends JpaRepository<Angebot, Integer> {
}