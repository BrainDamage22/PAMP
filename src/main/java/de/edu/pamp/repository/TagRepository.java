package de.edu.pamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.edu.pamp.dto.Tag;

/**
 * 
 * @author Tobias
 * 
 *         Schnittstelle für alle Datenbankoperationen für einen Tag
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
}