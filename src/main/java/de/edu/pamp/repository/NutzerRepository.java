package de.edu.pamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.edu.pamp.dto.Nutzer;

/**
 * 
 * @author Tobias
 * 
 *         Schnittstelle für alle Datenbankoperationen für einen Nutzer
 */
@Repository
public interface NutzerRepository extends JpaRepository<Nutzer, String> {
}