package de.edu.pamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.dto.VerificationToken;

/**
 * 
 * @author Tobias
 * 
 *         Schnittstelle für alle Datenbankoperationen für einen
 *         Verifikationstoken
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	/**
	 * Ermittlung eines bestimmten Verifikationstoken
	 * 
	 * @param iv_token eindeutige Identifikationsnummer des Verifikationstokens
	 * @return Verifikationstoken oder NULL
	 */
	VerificationToken findByToken(String iv_token);

	/**
	 * Ermittlung eines bestimmten Verifikationstoken
	 * 
	 * @param io_user Nutzer
	 * @return Verifikationstoken oder NULL
	 */
	VerificationToken findByUser(Nutzer io_user);
}