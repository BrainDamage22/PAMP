/**
 *
 */
package de.edu.pamp.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.edu.pamp.dto.Angebot;
import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.repository.AngebotRepository;

/**
 *
 * @author Tobias
 *
 *         Services rund um die Behandlung eines Angebots
 */
@Service
public class AngebotService {

	@Autowired
	private AngebotRepository angebotRepository;
	@Autowired
	private TagService tagService;
	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private DateiService dateiService;

	/**
	 * Anlage eines Angebots. Dabei wird der aktuelle Zeitstempel als
	 * Erstellungszeitpunkt verwendet.
	 *
	 * @param io_offer anzulegendes Angebot
	 */
	public void createOffer(Angebot io_offer) {
		io_offer.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		io_offer.setDisabled(false);
		angebotRepository.save(io_offer);
	}

	/**
	 * Angebot als verkauft markieren. Käufer und aktueller Zeitstempel als
	 * Verkaufszeitpunkt werden dem Angebot angehängt.
	 * 
	 * @param io_offer Aenderungen des Angebots
	 */
	public void updateOfferToSold(Angebot io_offer) {
		Angebot angebotToUpdate = angebotRepository.getOne(io_offer.getAngebotId());
		angebotToUpdate.setKaeufer(io_offer.getKaeufer());
		angebotToUpdate.setSoldOn(new Timestamp(System.currentTimeMillis()));
		angebotRepository.save(angebotToUpdate);
	}

	/**
	 * Lesen aller Angebote
	 */
	private List<Angebot> getAllOffers() {
		return angebotRepository.findAll();
	}

	/**
	 * Deaktivieren aller noch nicht verkauften Angeboten eines Nutzers
	 * 
	 * @param io_user Nutzer, dessen Angebote deaktiviert werden sollen
	 */
	public void disableAllOffers(Nutzer io_user) {
		Iterator<Angebot> lo_iterator = getUserOffers(io_user).iterator();

		while (lo_iterator.hasNext()) {
			Angebot lo_offer = lo_iterator.next();

			if (lo_offer.getKaeufer() == null) {
				lo_offer.setDisabled(true);
				angebotRepository.save(lo_offer);
			}
		}
	}

	/**
	 * Aktivieren aller noch nicht verkauften Angeboten eines Nutzers
	 * 
	 * @param io_user Nutzer, dessen Angebote deaktiviert werden sollen
	 */
	public void enableAllOffers(Nutzer io_user) {
		Iterator<Angebot> lo_iterator = getUserOffers(io_user).iterator();

		while (lo_iterator.hasNext()) {
			Angebot lo_offer = lo_iterator.next();

			if (lo_offer.getKaeufer() == null) {
				lo_offer.setDisabled(false);
				angebotRepository.save(lo_offer);
			}
		}
	}

	/**
	 * Rückgabe der Liste aller Angebote die NICHT von einem bestimmten Nutzer
	 * stammen, NICHT bereits verkauft und NICHT deaktiviert sind
	 * 
	 * @param io_nutzer Nutzer
	 * @return Liste der Inserate oder NULL
	 */
	public List<Angebot> getOtherUsersOffers(Nutzer io_nutzer) {
		List<Angebot> lt_offers = new ArrayList<Angebot>();
		Iterator<Angebot> lo_offerIterator = getAllOffers().iterator();

		while (lo_offerIterator.hasNext()) {
			Angebot lo_offer = lo_offerIterator.next();

			if ((!lo_offer.getAnbieter().getEmail().equals(io_nutzer.getEmail())) && (lo_offer.getKaeufer() == null)
					&& (!lo_offer.getDisabled())) {
				lt_offers.add(lo_offer);
			}
		}
		return lt_offers;
	}

	/**
	 * Ermittlung aller Angebote deren Titel identisch den Schlagwörtern entsprechen
	 *
	 * @param iv_searchString Schlagwörter
	 * @return Treffermenge
	 */
	public List<Angebot> findOfferBySearchString(String iv_searchString) {
		List<Angebot> lt_offers = getOtherUsersOffers(nutzerService.getCurrentUser());

		if (iv_searchString.trim().isEmpty()) {
			return lt_offers;
		}

		List<Angebot> lt_offersFound = new ArrayList<Angebot>();

		for (Angebot lo_offer : lt_offers) {
			if (lo_offer.getTitel().toUpperCase().contains(iv_searchString.toUpperCase())) {
				lt_offersFound.add(lo_offer);
			}
		}

		return lt_offersFound;
	}

	public List<Angebot> findAll() {
		return angebotRepository.findAll();
	}

	/**
	 * Rückgabe der Liste aller Angebote die von einem Benutzer inseriert wurden
	 * 
	 * @param io_nutzer Nutzer
	 * @return absteigende Liste der vom gesuchten Nutzer inserierten Angebote
	 */
	public List<Angebot> getUserOffers(Nutzer io_nutzer) {
		Nutzer currentUser = nutzerService.getCurrentUser();
		List<Angebot> userOffers = new ArrayList<Angebot>();

		if (io_nutzer.getEmail().equals(currentUser.getEmail())) {
			userOffers.addAll(currentUser.getOfferCreated());

		} else {
			for (Iterator<Angebot> lo_offerIterator = getAllOffers().iterator(); lo_offerIterator.hasNext();) {
				Angebot lo_offer = lo_offerIterator.next();
				if (lo_offer.getAnbieter().getEmail().equals(io_nutzer.getEmail())) {
					userOffers.add(lo_offer);
				}
			}
		}

		userOffers.sort(new Comparator<Angebot>() {
			@Override
			public int compare(Angebot o1, Angebot o2) {
				if (o1.getCreatedOn() == null || o2.getCreatedOn() == null) {
					return 0;
				}
				return o2.getCreatedOn().compareTo(o1.getCreatedOn());
			}
		});

		return userOffers;
	}

	/**
	 * Lesen eines bestimmten Angebots
	 * 
	 * @param iv_offerId eindeutige Identifikationsnummer des Angebots
	 * @return Angebot oder NULL
	 */
	public Angebot findOfferById(int iv_offerId) {
		return angebotRepository.findById(iv_offerId).orElse(null);
	}

	/**
	 * Löschen eines Angebots. Dabei werden auch die abhängigen Entitäte mitgelöscht
	 *
	 * @param iv_offerId eindeutige Identifikationsnummer des zu löschenden Angebots
	 */
	public void deleteOfferbyId(int iv_offerId) {
		dateiService.deleteDateienByOfferId(iv_offerId);
		tagService.deleteTagsByOfferId(iv_offerId);
		angebotRepository.deleteById(iv_offerId);
	}

	/**
	 * Setzen der Bewertung für ein Angebot
	 *
	 * @param io_offer zu änderndes Angeobt
	 */
	public void updateRating(Angebot io_offer) {
		Angebot angebotToUpdate = angebotRepository.getOne(io_offer.getAngebotId());
		angebotToUpdate.setRating(io_offer.getRating());
		angebotRepository.save(angebotToUpdate);
	}

	/**
	 * Ermittlung aller verkauften Angeboten eines Nutzers
	 *
	 * @param iv_email eindeutige Identifikationsnummer des Nutzers
	 * @return Trefferliste
	 */
	public List<Angebot> getOfferSoldByUserId(String iv_email) {
		List<Angebot> lt_offerSold = nutzerService.findUserById(iv_email).getOfferCreated();

		for (Iterator<Angebot> lo_iterator = lt_offerSold.iterator(); lo_iterator.hasNext();) {
			Angebot lo_offer = lo_iterator.next();

			if (lo_offer.getKaeufer() == null) {
				lo_iterator.remove();
			}
		}

		return lt_offerSold;
	}

	/**
	 * Aenderung folgender Eigenschaften eines Angebots: Titel und Beschreibung,
	 * Adressdaten, Betrag und Kategorie
	 *
	 * @param angebot geändertes Angebot
	 */
	public void changeOffer(Angebot angebot) {
		Angebot angebotToUpdate = angebotRepository.getOne(angebot.getAngebotId());
		angebotToUpdate.setStadt(angebot.getStadt());
		angebotToUpdate.setPlz(angebot.getPlz());
		angebotToUpdate.setTitel(angebot.getTitel());
		angebotToUpdate.setBetrag(angebot.getBetrag());
		angebotToUpdate.setBeschreibung(angebot.getBeschreibung());
		angebotToUpdate.setAngebotsKategorieId(angebot.getAngebotsKategorieId());
		angebotRepository.save(angebotToUpdate);
	}

	/**
	 * Zählen der Angebotsaufrufe
	 * 
	 * @param angebot aufgerufene Angebot
	 */
	public void increaseViews(Angebot angebot) {
		Angebot angebotToUpdate = angebotRepository.getOne(angebot.getAngebotId());
		angebotToUpdate.setViews(angebot.getViews() + 1);
		angebotRepository.save(angebotToUpdate);
	}
}