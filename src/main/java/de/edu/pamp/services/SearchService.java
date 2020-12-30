/**
 * 
 */
package de.edu.pamp.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.maps.model.Distance;

import de.edu.pamp.dto.Angebot;
import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.dto.Tag;
import de.edu.pamp.geolocation.GoogleContext;

@Service
/**
 * @author Tobias
 *
 *         Services rum um die Behandlung der Suche
 */
public class SearchService {

	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private AngebotService angebotService;

	/**
	 * Konstruktor
	 */
	public SearchService() {
	}

	/**
	 * Ermittlung aller Angeboten. In Abhängigkeit der Kriterien wird die
	 * Treffermenge eingeschränkt
	 * 
	 * @param iv_searchString   Schlagwörter
	 * @param iv_radius         Radius in Kilometer
	 * @param iv_searchCategory eindeutige Identifikationsnummer einer
	 *                          Angebotskategorie
	 * @param iv_searchAmountTo maximaler Betrag
	 * @param iv_searchRating   Rating ab
	 * @return Trefferliste
	 */
	public List<Angebot> search(String iv_searchString, int iv_radius, int iv_searchCategory, double iv_searchAmountTo,
			int iv_searchRating) {
		List<Angebot> lt_offers = search(iv_searchString);
		Nutzer lo_currentUser = nutzerService.getCurrentUser();

		for (Iterator<Angebot> lo_iterator = lt_offers.iterator(); lo_iterator.hasNext();) {
			Angebot lo_angebot = lo_iterator.next();

			if ((validCategorie(iv_searchCategory, lo_angebot))
					&& (offerAmount(lo_angebot.getBetrag(), iv_searchAmountTo))
					&& (userRating(iv_searchRating, lo_angebot.getAnbieter().getEmail()))) {

				String lv_orig = lo_currentUser.getAdresse() + ", " + lo_currentUser.getPlz() + " "
						+ lo_currentUser.getStadt();
				String lv_dest = lo_angebot.getPlz() + " " + lo_angebot.getStadt();

				Distance lo_distance = GoogleContext.getDistance(lv_orig, lv_dest);

				if (lo_distance != null) {
					if ((lo_distance.inMeters > (iv_radius * 1000)) && (iv_radius > 0)) {
						// Angebot liegt nicht im gewuenschten Radius
						lo_iterator.remove();
					} else {
						// Angebot mit der aktuellen Entfernung anreichern
						lo_angebot.setDistance(lo_distance.humanReadable);
						lo_angebot.setDistancelong(lo_distance.inMeters);
					}
				} else {
					// GoogleAPI lieferte kein Ergebnis - Evtl. keine gueltigen Daten?
					lo_iterator.remove();
				}
			} else {
				// Angebot nicht gesuchter Kategorie zugeordnet
				lo_iterator.remove();
			}
		}

		return lt_offers;
	}

	/**
	 * Suchfunktion abhängig von einem Suchstring. Mittels einem Punktesystem werden
	 * die Angebote nach Relevanz absteigend sortiert
	 * 
	 * @param iv_searchString Schlagwörter
	 * @return Trefferliste
	 */
	private List<Angebot> search(String iv_searchString) {
		List<Angebot> ergebnis = new ArrayList<>();
		List<Angebot> all = angebotService.getOtherUsersOffers(nutzerService.getCurrentUser());
		String[] split = splitSearchString(iv_searchString);

		for (Angebot angebot : all) {
			for (String s : split) {
				if (angebot.getTitel().toLowerCase().contains(s.toLowerCase())) {
					angebot.setHits(angebot.getHits() + 1);
				}
				List<Tag> tags = angebot.getTags();

				for (Tag tag : tags) {
					if (tag.getTag().toLowerCase().contains(s.toLowerCase())) {
						angebot.setHits(angebot.getHits() + 1);
					}
				}
			}
		}

		for (Angebot angebot : all) {
			if (angebot.getHits() > 0) {
				ergebnis.add(angebot);
			}
		}

		ergebnis.sort(new Comparator<Angebot>() {
			@Override
			public int compare(Angebot o1, Angebot o2) {
				return o1.getHits() - o2.getHits();
			}
		});

		Collections.reverse(ergebnis);

		return ergebnis;
	}

	/**
	 * Trennen der Schlagwörter um diese für die Suche und dem Punktesystem
	 * verwenden zu können
	 * 
	 * @param searchstring Schlagwörter
	 * @return Liste mit Schlagwörter die anhand eines Leerzeichens getrennt wurden
	 */
	private String[] splitSearchString(String searchstring) {
		return searchstring.split(" ");
	}

	/**
	 * Prüft, ob der Betrag des Angebots kleiner gleich des maximalen Suchbetrags
	 * entspricht
	 * 
	 * @param iv_offerAmount    Angebotsbetrag
	 * @param iv_searchAmountTo maximaler Suchbetrag
	 * @return TRUE, wenn Angebotsbetrag kleiner gleich dem maximalen ist. FALSE,
	 *         wenn Angebotsbetrag größer dem maximalen ist.
	 */
	private boolean offerAmount(double iv_offerAmount, double iv_searchAmountTo) {
		if (iv_searchAmountTo == 0) {
			return true;
		} else if (iv_offerAmount <= iv_searchAmountTo) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Prüft, ob die Kategorie des Angebots dem des Suchkriterium entspricht
	 * 
	 * @param iv_searchCategorie eindeutige Identifikationsnummer der
	 *                           Angebotskategorie auf die bei der Suche
	 *                           eingeschränkt wird
	 * @param io_offer           aktuell zu prüfendes Angebot
	 * @return TRUE, wenn Angebotskategorien beider übereinstimmen. FALSE, wenn
	 *         Angebotskategorien beider unterschiedlich sind.
	 */
	private boolean validCategorie(int iv_searchCategorie, Angebot io_offer) {
		if (iv_searchCategorie == 0) {
			// alle Kategorien
			return true;
		} else if (io_offer.getAngebotsKategorieId().getAngebotsKategorieId() == iv_searchCategorie) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Prüft, ob das aktuelle Rating des Inserenten dem des Suchkriterium entspricht
	 * 
	 * @param iv_ratingMin Rating ab
	 * @param iv_email     eindeutige Identifikationsnummer des Inserenten
	 * @return
	 */
	private boolean userRating(int iv_ratingMin, String iv_email) {
		double lv_currentRating = nutzerService.getUserRating(iv_email);

		if (iv_ratingMin == 0) {
			// alle Ratings
			return true;
		}

		// +0.2 Punkte um die Diskrepanz zwischen Darstellung des Ratings und
		// tatsächlichem Wert zu berücksichtigen (3 Sterne: 2,8 - 3,3)
		lv_currentRating += 0.2;

		if (lv_currentRating >= iv_ratingMin) {
			return true;
		} else {
			return false;
		}
	}
}