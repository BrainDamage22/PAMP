package de.edu.pamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.edu.pamp.dto.Role;

/**
 * 
 * @author Tobias
 * 
 *         Schnittstelle für alle Datenbankoperationen für eine Rolle
 */
public interface RoleRepository extends JpaRepository<Role, String> {
}