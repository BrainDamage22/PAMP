package de.edu.pamp.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * 
 * @author Tobias
 *
 *         Beschreibung der Entität: Datei
 */
@Entity
public class Datei {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(columnDefinition = "TEXT")
	private String base64;

	@ManyToOne
	@JoinColumn(name = "fk_angebotfile")
	private Angebot angebot;

	private boolean isPDF = false;

	private String name;

	/**
	 * Konstruktor
	 */
	public Datei() {
	}

	/**
	 * Holen der eindeutigen Identifikationsnummer
	 * 
	 * @return eindeutige Identifikationsnummer
	 */
	public long getId() {
		return id;
	}

	/**
	 * Setzen der eindeutigen Identifikationsnummer
	 * 
	 * @param id eindeutige Identifikationsnummer
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Holen des Dateiinhalts
	 * 
	 * @return Dateiinhalt in Base64
	 */
	public String getBase64() {
		return base64;
	}

	/**
	 * Setzen des Dateiinhalt
	 * 
	 * @param base64 Dateiinhalt in Base64
	 */
	public void setBase64(String base64) {
		this.base64 = base64;
	}

	/**
	 * Holen des zugeordneten Angebots
	 * 
	 * @return zugeordnete Angebot
	 */
	public Angebot getAngebot() {
		return angebot;
	}

	/**
	 * Setzen des zugeordneten Angebots
	 * 
	 * @param angebot zugeordnete Angebot
	 */
	public void setAngebot(Angebot angebot) {
		this.angebot = angebot;
	}

	/**
	 * Holen des PFD-Kennzeichens
	 * 
	 * @return PDF-Kennzeichen
	 */
	public boolean isPDF() {
		return isPDF;
	}

	/**
	 * Setzen des PDF-Kennzeichens
	 * 
	 * @param PDF PFD-Kennzeichen (True/False)
	 */
	public void setPDF(boolean PDF) {
		isPDF = PDF;
	}

	/**
	 * Holen des Dateinamen
	 * 
	 * @return Dateiname
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setzen des Dateinamen
	 * 
	 * @param name Dateiname
	 */
	public void setName(String name) {
		this.name = name;
	}
}