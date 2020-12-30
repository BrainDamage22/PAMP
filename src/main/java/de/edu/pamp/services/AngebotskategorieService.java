/**
 * 
 */
package de.edu.pamp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.edu.pamp.dto.Angebotskategorie;
import de.edu.pamp.repository.AngebotskategorieRepository;

@Service
/**
 * @author Tobias
 *
 *         Service rund um die Behandlung der Angebotskategorie
 */
public class AngebotskategorieService {
	@Autowired
	AngebotskategorieRepository mo_angeobtskategorieRepository;

	/**
	 * Konstruktor
	 */
	public AngebotskategorieService() {
	}

	/**
	 * Ermittlung einer gesuchten Angebotskategorie
	 * 
	 * @param iv_offerCategoryId eindeutige Identifikationsnummer der
	 *                           Angebotskategorie
	 * @return Angbotskategorie oder NULL
	 */
	public Angebotskategorie findOfferCategorieById(int iv_offerCategoryId) {
		return mo_angeobtskategorieRepository.findById(iv_offerCategoryId).orElse(null);
	}

	/**
	 * Ermittlung aller existierender Angebotskategorien
	 * 
	 * @return Trefferliste
	 */
	public List<Angebotskategorie> findAllOfferCategories() {
		return mo_angeobtskategorieRepository.findAll();
	}

	/**
	 * Anlage einer Angebotskategorie
	 * 
	 * @param io_offerCategory anzulegende Angebotskategorie
	 */
	public void createOfferCategory(Angebotskategorie io_offerCategory) {
		mo_angeobtskategorieRepository.save(io_offerCategory);
	}

	/**
	 * Löschen einer Angebotskategorie
	 * 
	 * @param iv_offerCategoryId eindeutige Identifikationsnummer der zu löschenden
	 *                           Angebotskategorie
	 */
	public void deleteOfferCategory(int iv_offerCategoryId) {
		mo_angeobtskategorieRepository.deleteById(iv_offerCategoryId);
	}
}