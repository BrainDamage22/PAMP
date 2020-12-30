/**
 * 
 */
package de.edu.pamp.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.edu.pamp.dto.Datei;
import de.edu.pamp.repository.DateiRepository;

/**
 * 
 * @author Tobias
 *
 *         Service rund um die Behandlung von Dateien
 */
@Service
public class DateiService {

	@Autowired
	private DateiRepository dateiRepository;

	/**
	 * Speichern einer Datei
	 * 
	 * @param datei zu speichernde Datei
	 */
	public void saveDatei(Datei datei) {
		dateiRepository.save(datei);
	}

	/**
	 * Ermittlung aller Produktbilder zu einem bestimmten Angebot
	 * 
	 * @param offerId eindeutige Identifikationsnummer des Angebots
	 * @return Treffermenge
	 */
	public List<Datei> findProductimagesbyOfferId(int offerId) {
		List<Datei> all = dateiRepository.findAll();
		List<Datei> ret = new ArrayList<>();
		for (Datei datei : all) {
			if (offerId == datei.getAngebot().getAngebotId() && !datei.isPDF()) {
				ret.add(datei);
			}
		}
		return ret;
	}

	/**
	 * Ermittlung aller Dateien zu einem bestimmten Angbot
	 * 
	 * @param offerId eindeutige Identifikationsnummer des Angebots
	 * @return Treffermenge
	 */
	public List<Datei> findAllDateienbyOfferId(int offerId) {
		List<Datei> all = dateiRepository.findAll();
		List<Datei> ret = new ArrayList<>();
		for (Datei datei : all) {
			if (offerId == datei.getAngebot().getAngebotId()) {
				ret.add(datei);
			}
		}
		return ret;
	}

	/**
	 * Löschen aller Dateien zu einem bestimmten Angebot
	 * 
	 * @param iv_offerId eindeutige Identifikationsnummer des Angebots
	 */
	public void deleteDateienByOfferId(int iv_offerId) {
		for (Datei lo_datei : findAllDateienbyOfferId(iv_offerId)) {
			dateiRepository.delete(lo_datei);
		}
	}

	/**
	 * Ermittlung aller Qualifikationen zu einem bestimmten Angebot
	 * 
	 * @param offerId eindeutige Identifikationsnummer des Angebots
	 * @return Treffermenge
	 */
	public List<Datei> findQualifikationenByOfferId(int offerId) {
		List<Datei> all = dateiRepository.findAll();
		List<Datei> ret = new ArrayList<>();
		for (Datei datei : all) {
			if (offerId == datei.getAngebot().getAngebotId() && datei.isPDF()) {
				ret.add(datei);
			}
		}
		return ret;
	}
}