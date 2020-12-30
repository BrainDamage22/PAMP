/**
 * 
 */
package de.edu.pamp.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

/**
 * @author Tobias
 *
 *         Beschreibung der Entität: Angebotskategorie
 */
@Entity
public class Angebotskategorie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int angebotsKategorieId;
	@NotEmpty
	private String bezeichnung;

	/**
	 * Konstruktor
	 */
	public Angebotskategorie() {
	}

	/**
	 * Holen der eindeutigen Identifikationsnummer
	 * 
	 * @return eindeutige Identifikationsnummer
	 */
	public int getAngebotsKategorieId() {
		return angebotsKategorieId;
	}

	/**
	 * Setzen der eindeutigen Identifikationsnummer
	 * 
	 * @param angebotsKategorieId eindeutige Identifikationsnummer
	 */
	public void setAngebotsKategorieId(int angebotsKategorieId) {
		this.angebotsKategorieId = angebotsKategorieId;
	}

	/**
	 * Holen der Bezeichnung
	 * 
	 * @return Bezeichnung
	 */
	public String getBezeichnung() {
		return bezeichnung;
	}

	/**
	 * Setzen der Bezeichnung
	 * 
	 * @param bezeichnung Bezeichung
	 */
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	/**
	 * Prüfen, ob eine Angebotgskategorie an der UI ausgewählt ist. Dies wird bei
	 * der Vorbelegung der Dropdown bei der Angebotsänderung benötigt.
	 * 
	 * @param angebotsKategorieId eindeutige Identifikationsnummer
	 * @return TRUE, wenn die Angebotskategorie ausgewählt ist. FALSE, wenn die
	 *         Angebotskategorie nicht ausgewählt ist.
	 */
	public boolean isSelected(int angebotsKategorieId) {
		if (getAngebotsKategorieId() == angebotsKategorieId) {
			return true;
		} else {
			return false;
		}
	}
}