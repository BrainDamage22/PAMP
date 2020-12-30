/**
 * 
 */
package de.edu.pamp.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import de.edu.pamp.dto.Angebot;
import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.dto.Role;
import de.edu.pamp.dto.VerificationToken;
import de.edu.pamp.repository.NutzerRepository;
import de.edu.pamp.repository.VerificationTokenRepository;

/**
 * 
 * @author Tobias
 *
 *         Services rund um die Behandlung eines Nutzers
 */
@Service
public class NutzerService {

	@Autowired
	private NutzerRepository userRepository;
	@Autowired
	private VerificationTokenRepository tokenRepository;
	@Autowired
	private AngebotService angebotService;

	/**
	 * Anlage eines Nutzers. Dieser erhält die Rolle "USER". Die Rolle "USER" wird
	 * bei der erstmaligen Anlage eines Nutzers mit angelegt.
	 *
	 * @param io_nutzer: neu anzulegender Nutzer
	 */
	public void createNutzer(Nutzer io_nutzer) {
		io_nutzer.setPassword(new BCryptPasswordEncoder().encode(io_nutzer.getPassword()));
		Role userRole = new Role("USER");
		List<Role> roles = new ArrayList<>();
		roles.add(userRole);
		io_nutzer.setRoles(roles);
		userRepository.save(io_nutzer);
	}

	/**
	 * Ändern des Vornamen eines Nutzers
	 * 
	 * @param io_nutzer zu ändernder Nutzer
	 */
	public void updateNutzerVorname(Nutzer io_nutzer) {
		Nutzer nutzerToUpdate = userRepository.getOne(io_nutzer.getEmail());
		nutzerToUpdate.setVorname(io_nutzer.getVorname());
		userRepository.save(nutzerToUpdate);
	}

	/**
	 * Ändern des Nachnamen eines Nutzers
	 * 
	 * @param io_nutzer zu ändernder Nutzer
	 */
	public void updateNutzerNachname(Nutzer io_nutzer) {
		Nutzer nutzerToUpdate = userRepository.getOne(io_nutzer.getEmail());
		nutzerToUpdate.setNachname(io_nutzer.getNachname());
		userRepository.save(nutzerToUpdate);
	}

	/**
	 * Ändern des Passworts eines Nutzers
	 * 
	 * @param io_nutzer zu ändernder Nutzer
	 */
	public void updateNutzerPasswort(Nutzer io_nutzer) {
		Nutzer nutzerToUpdate = userRepository.getOne(io_nutzer.getEmail());
		nutzerToUpdate.setPassword(io_nutzer.getPassword());
		userRepository.save(nutzerToUpdate);
	}

	/**
	 * Ändern des Geburtsdatum eines Nutzers
	 * 
	 * @param io_nutzer zu ändernder Nutzer
	 */
	public void updateNutzerGeburtsdatum(Nutzer io_nutzer) {
		Nutzer nutzerToUpdate = userRepository.getOne(io_nutzer.getEmail());
		nutzerToUpdate.setGeburtsdatum(io_nutzer.getGeburtsdatum());
		userRepository.save(nutzerToUpdate);
	}

	/**
	 * Nutzer sperren und die dazugehörige Rolle "LOCKED" setzen. Bei der
	 * erstmaligen Sperre eines Benutzers wird die Rolle "LOCKED" angelegt.
	 * 
	 * @param io_user zu sperrender Nutzer
	 */
	public void setNutzerLocked(Nutzer io_user) {
		Role lo_role = new Role();

		lo_role.setName("LOCKED");
		io_user.getRoles().add(lo_role);
		io_user.setSperre(true);
		io_user.setLockedBy(getCurrentUser());
		userRepository.save(io_user);
	}

	/**
	 * Nutzer entsperren und die Rolle entfernen
	 * 
	 * @param io_user Nutzer der entsperrt werden soll
	 */
	public void setNutzerUnLocked(Nutzer io_user) {
		Iterator<Role> lo_iterator = io_user.getRoles().iterator();

		while (lo_iterator.hasNext()) {
			Role lo_role = lo_iterator.next();

			if (lo_role.getName().equals("LOCKED")) {
				lo_iterator.remove();
			}
		}
		io_user.setSperre(false);
		io_user.setLockedBy(null);
		userRepository.save(io_user);
	}

	/**
	 * Ändern der Adressdaten eines Nutzers
	 * 
	 * @param io_nutzer zu ändernder Nutzer
	 */
	public void updateNutzerAdresse(Nutzer io_nutzer) {
		Nutzer nutzerToUpdate = userRepository.getOne(io_nutzer.getEmail());
		nutzerToUpdate.setPlz(io_nutzer.getPlz());
		nutzerToUpdate.setStadt(io_nutzer.getStadt());
		nutzerToUpdate.setAdresse(io_nutzer.getAdresse());
		userRepository.save(nutzerToUpdate);
	}

	/**
	 * Aktivierung des Nutzers
	 *
	 * @param io_nutzer: zu aktivierender Nutzer
	 */
	public void enableNutzer(Nutzer io_nutzer) {
		userRepository.save(io_nutzer);
	}

	/**
	 * Ermittlung eines Nutzers anhand dessen eindeutigen E-Mail Adresse
	 * 
	 * @param iv_email: E-Mail Adresse des gesuchten Nutzers
	 * @return gefundener Nutzer als Objekt oder NULL
	 */
	public Nutzer findUserById(String iv_email) {
		return userRepository.findById(iv_email).orElse(null);
	}

	/**
	 * . Pruefen, ob ein Nutzer anhand der gesuchten E-Mail Adresse existiert
	 *
	 * @param iv_email: E-Mail Adresse des gesuchten Nutzers
	 * @return TRUE bei einem Treffer sonst FALSE
	 */
	public boolean isUserPresent(String iv_email) {
		if (findUserById(iv_email) != null)
			return true;
		return false;
	}

	/**
	 * Pruefe, ob ein Nutzer die Rolle "ADMIN" besitzt.
	 * 
	 * @param iv_email Eindeutige Identifikation eines Nutzers
	 * @return TRUE = Nutzer besitzt die Rolle "ADMIN" - FALSE = Nutzer besitzt die
	 *         Rolle "ADMIN" nicht
	 */
	public boolean isUserAdmin(String iv_email) {
		if (!iv_email.equals("anonymousUser")) {
			List<Role> lt_userRoles = findUserById(iv_email).getRoles();

			for (Role role : lt_userRoles) {
				if (role.getName().equals("ADMIN")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Pruefe, ob ein Nutzer die Rolle "LOCKED" besitzt
	 * 
	 * @param iv_email Eindeutige Identifikation eines Nutzers
	 * @return TRUE = Nutzer besitzt die Rolle "LOCKED" - FALSE = Nutzer besitzt die
	 *         Rolle "LOCKED" nicht
	 */
	public boolean isUserLocked(String iv_email) {
		if (!iv_email.equals("anonymousUser")) {
			List<Role> lt_userRoles = findUserById(iv_email).getRoles();

			for (Role role : lt_userRoles) {
				if (role.getName().equals("LOCKED")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Generierung und Speicherung des Verifikationstoken fuer einen Nutzer
	 *
	 * @param io_nutzer: Nutzer
	 * @param iv_token: Token
	 */
	public void createVerificationToken(Nutzer io_nutzer, String iv_token) {
		tokenRepository.save(new VerificationToken(iv_token, io_nutzer));
	}

	/**
	 * Ermittlung eines bestimmten Verifikationstokens
	 *
	 * @param iv_verificationToken: gesuchter Verifikationstoken
	 * @return Verifikationstoken
	 */
	public VerificationToken getVerificationToken(String iv_verificationToken) {
		return tokenRepository.findByToken(iv_verificationToken);
	}

	/**
	 * Liefert die ID des aktuell angemeldeten Benutzer zurueck
	 * 
	 * @return eindeutige E-Mail Adresse des aktuell angemeldeten Benutzers
	 */
	public String getCurrentUserId() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	/**
	 * Liefert den aktuell angemeldeten Benutzer zurueck
	 * 
	 * @return Nutzer als Objekt oder NULL
	 */
	public Nutzer getCurrentUser() {
		return findUserById(getCurrentUserId());
	}

	/**
	 * Liste mit allen im System vorhandenen Benutzern
	 * 
	 * @return Objektliste mit Benutzern
	 */
	public List<Nutzer> getAllUsers() {
		return userRepository.findAll();
	}

	/**
	 * *Liefert den Namen eines Benutzers als String zurueck
	 * 
	 * @param userId eindeutige Identifikationsnummer des Nutzers
	 * @return String Vorname und Nachname des Nutzers
	 */

	public String getName(String userId) {
		return findUserById(userId).getVorname() + " " + findUserById(userId).getNachname();
	}

	/**
	 * Ändern des Profilbild eines Nutzers
	 * 
	 * @param io_nutzer zu ändernder Nutzer
	 */
	public void updateProfilbild(Nutzer io_nutzer) {
		Nutzer nutzerToUpdate = userRepository.getOne(io_nutzer.getEmail());
		nutzerToUpdate.setProfilbild(io_nutzer.getProfilbild());
		userRepository.save(nutzerToUpdate);
	}

	/**
	 * Ermittlung des aktuellen Ratings eines Nutzers
	 * 
	 * @param iv_email eindeutige Identifikationsnummer eines Nutzers
	 * @return aktuelles Rating zwischen 0 und 5
	 */
	public double getUserRating(String iv_email) {
		List<Angebot> lt_offer = angebotService.getOfferSoldByUserId(iv_email);
		double lv_ratingAll = 0;

		for (Angebot lo_offer : lt_offer) {
			lv_ratingAll += lo_offer.getRating();
		}

		if (lt_offer.size() != 0) {
			return lv_ratingAll / lt_offer.size();
		} else {
			return 0;
		}
	}
}