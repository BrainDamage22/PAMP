/**
 *
 */
package de.edu.pamp.services;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.dto.VerificationToken;
import de.edu.pamp.repository.VerificationTokenRepository;

/**
 * 
 * @author Tobias
 * 
 *         Services rund um die Behandlung von Verifikationstoken
 *
 */
@Service
public class VerificationTokenService {

	@Autowired
	private VerificationTokenRepository verificationTokenRepository;

	/**
	 * Suchen eines Verifikationstoken anhand einer eindeutigen
	 * Identifikationsnummer
	 * 
	 * @param id eindeutige Identifikationsnummer
	 * @return passender Verifikationstoken oder NULL
	 */
	public VerificationToken findbyId(Long id) {
		return verificationTokenRepository.getOne(id);
	}

	/**
	 * Suchen eines Verifikationstoken zu einem Nutzer
	 * 
	 * @param nutzer Nutzer
	 * @return passender Verifikationstoken oder NULL
	 */
	public VerificationToken findbyUser(Nutzer nutzer) {
		return verificationTokenRepository.findByUser(nutzer);
	}

	/**
	 * Aktualisierung eines Verifikationstoken
	 * 
	 * @param id eindeutige Identifikationsnummer
	 */
	public void updateToken(Long id) {
		VerificationToken verificationToken = findbyId(id);
		verificationToken.setExpiryDate(calculateExpiryDate());
		verificationTokenRepository.save(verificationToken);
	}

	/**
	 * Berechnung des Gültigkeitsdatum. Bis zu diesem Stichtag ist ein
	 * Verifikationstoken gültig
	 * 
	 * @return Gültigkeitsdatum
	 */
	private Date calculateExpiryDate() {
		Calendar lo_cal = Calendar.getInstance();
		lo_cal.setTime(new Timestamp(lo_cal.getTime().getTime()));
		lo_cal.add(Calendar.MINUTE, 1440);
		return new Date(lo_cal.getTime().getTime());
	}
}