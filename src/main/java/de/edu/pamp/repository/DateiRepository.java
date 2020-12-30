package de.edu.pamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.edu.pamp.dto.Datei;

/**
 * 
 * @author Tobias
 * 
 *         Schnittstelle für alle Datenbankoperationen für eine Datei
 *
 */
@Repository
public interface DateiRepository extends JpaRepository<Datei, Long> {
}