package de.edu.pamp.dto;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * @author Tobias
 *
 *         Beschreibung der Entität: Angebot
 */
@Entity
@Table(name = "angebot")
public class Angebot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int angebotId;

	@NotEmpty
	private String titel;

	@NotEmpty
	private String beschreibung;

	@NotNull
	@Column(name = "angebotsTypId")
	private int offerTypeId;

	@NotEmpty
	private String waehrung;

	@NotEmpty
	private String plz;

	@NotEmpty
	private String stadt;

	@NotNull
	private double betrag;

	@ManyToOne
	@JoinColumn(name = "fk_anbieter")
	private Nutzer anbieter;

	@ManyToOne
	@JoinColumn(name = "fk_kaeufer")
	private Nutzer kaeufer;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fk_angebotsKategorieId")
	private Angebotskategorie angebotsKategorieId;

	@OneToMany(mappedBy = "angebot")
	private List<Tag> tags;

	@OneToMany(mappedBy = "angebot")
	private List<Datei> images;

	@CreationTimestamp
	private Timestamp createdOn;

	@DateTimeFormat
	private Timestamp soldOn;

	@Transient
	private int hits = 0;

	@Transient
	private String distance;

	@Transient
	private long distancelong;

	private boolean disabled = false;

	private int rating = 0;

	private int views = 0;

	/**
	 * Konstruktor
	 */
	public Angebot() {
	}

	/**
	 * Konstruktor
	 * 
	 * @param iv_titel        Titel
	 * @param iv_beschreibung Beschreibung
	 * @param iv_waehrung     Währung
	 * @param iv_betrag       Betrag
	 * @param iv_kategorie    Kategorie
	 * @param iv_typ          Typ
	 */
	public Angebot(@NotEmpty String iv_titel, @NotEmpty String iv_beschreibung, @NotEmpty String iv_waehrung,
			@NotEmpty double iv_betrag, @NotEmpty String iv_kategorie, @NotEmpty int iv_typ) {
		this.titel = iv_titel;
		this.beschreibung = iv_beschreibung;
		this.waehrung = iv_waehrung;
		this.betrag = iv_betrag;
		this.offerTypeId = iv_typ;
	}

	/**
	 * Holen der eindeutigen Identifikationsnummer
	 * 
	 * @return eindeutige Identifikationsnummer
	 */
	public int getAngebotId() {
		return angebotId;
	}

	/**
	 * Setzen der eindeutigen Identifikationsnummer
	 * 
	 * @param iv_angebotId eindeutige Identifikationsnummer
	 */
	public void setAngebotId(int iv_angebotId) {
		this.angebotId = iv_angebotId;
	}

	/**
	 * Holen des Titels
	 * 
	 * @return Titel
	 */
	public String getTitel() {
		return titel;
	}

	/**
	 * Setzen des Titels
	 * 
	 * @param iv_titel Titel
	 */
	public void setTitel(String iv_titel) {
		this.titel = iv_titel;
	}

	/**
	 * Holen der Beschreibung
	 * 
	 * @return Beschreibung
	 */
	public String getBeschreibung() {
		return beschreibung;
	}

	/**
	 * Setzen der Beschreibung
	 * 
	 * @param iv_beschreibung Beschreibung
	 */
	public void setBeschreibung(String iv_beschreibung) {
		this.beschreibung = iv_beschreibung;
	}

	/**
	 * Holen der Währung
	 * 
	 * @return Währung
	 */
	public String getWaehrung() {
		return waehrung;
	}

	/**
	 * Setzen der Währung
	 * 
	 * @param iv_waehrung Währung
	 */
	public void setWaehrung(String iv_waehrung) {
		this.waehrung = iv_waehrung;
	}

	/**
	 * Holen des Betrags
	 * 
	 * @return Betrag
	 */
	public double getBetrag() {
		return betrag;
	}

	/**
	 * Setzen des Betrags
	 * 
	 * @param iv_betrag Betrag
	 */
	public void setBetrag(double iv_betrag) {
		this.betrag = iv_betrag;
	}

	/**
	 * Holen des Anbieters
	 * 
	 * @return Anbieter
	 */
	public Nutzer getAnbieter() {
		return anbieter;
	}

	/**
	 * Setzen des Anbieters
	 * 
	 * @param io_anbieter Anbieter
	 */
	public void setAnbieter(Nutzer io_anbieter) {
		this.anbieter = io_anbieter;
	}

	/**
	 * Holen des Zeitpunkts der Erstellung
	 * 
	 * @return Zeitpunkt der Erstellung
	 */
	public Timestamp getCreatedAt() {
		return this.createdOn;
	}

	/**
	 * Setzen des Zeitpunkts der Erstellung
	 * 
	 * @param iv_createdOn Zeitpunkt der Erstellung
	 */
	public void setCreatedOn(Timestamp iv_createdOn) {
		this.createdOn = iv_createdOn;
	}

	/**
	 * Holen des Zeitpunkts des Verkaufs
	 * 
	 * @return Zeitpunkt des Verkaufs
	 */
	public Timestamp getSoldOn() {
		return this.soldOn;
	}

	/**
	 * Setzen des Zeitpunkts des Verkaufs
	 * 
	 * @param iv_soldOn Zeitpunkt des Verkaufs
	 */
	public void setSoldOn(Timestamp iv_soldOn) {
		this.soldOn = iv_soldOn;
	}

	/**
	 * Holen des Käufers
	 * 
	 * @return Käufer
	 */
	public Nutzer getKaeufer() {
		return this.kaeufer;
	}

	/**
	 * Setzen des Käufers
	 * 
	 * @param io_kaeufer Käufer
	 */
	public void setKaeufer(Nutzer io_kaeufer) {
		this.kaeufer = io_kaeufer;
	}

	/**
	 * Holen aller zugeordneten Tags
	 * 
	 * @return Tagliste
	 */
	public List<Tag> getTags() {
		return tags;
	}

	/**
	 * Setzen der zugeordneten Tags
	 * 
	 * @param tags Tagliste
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * Holen der Trefferanzahl
	 * 
	 * @return Trefferanzahl
	 */
	public int getHits() {
		return hits;
	}

	/**
	 * Setzen der Trefferanzahl
	 * 
	 * @param hits Trefferanzahl
	 */
	public void setHits(int hits) {
		this.hits = hits;
	}

	/**
	 * Holen der zugeordneten Bilder
	 * 
	 * @return zugeordneten Bilder
	 */
	public List<Datei> getImages() {
		return images;
	}

	/**
	 * Setzen der zugeordneten Bilder
	 * 
	 * @param images zugeordnete Bilder
	 */
	public void setImages(List<Datei> images) {
		this.images = images;
	}

	/**
	 * Holen des Zeitpunkts der Erstellung
	 * 
	 * @return Zeitpunkts der Erstellung
	 */
	public Timestamp getCreatedOn() {
		return createdOn;
	}

	/**
	 * Setzen des Inaktiv-Kennzeichens
	 * 
	 * @param if_disable Inaktiv-Kennzeichen (True/False)
	 */
	public void setDisabled(boolean if_disable) {
		this.disabled = if_disable;
	}

	/**
	 * Holen des Inaktiv-Kennzeichens
	 * 
	 * @return Inaktiv-Kennzeichen
	 */
	public boolean getDisabled() {
		return this.disabled;
	}

	/**
	 * Holen des Inaktiv-Kennzeichens
	 * 
	 * @return Inaktiv-Kennzeichen
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Holen des Ratings
	 * 
	 * @return Rating
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * Setzen des Ratings
	 * 
	 * @param rating Rating
	 */
	public void setRating(int rating) {
		this.rating = rating;
	}

	/**
	 * Holen der Entfernung
	 * 
	 * @return Entfernung
	 */
	public long getDistancelong() {
		return distancelong;
	}

	/**
	 * Setzen der Entfernung
	 * 
	 * @param distancelong Entfernung
	 */
	public void setDistancelong(long distancelong) {
		this.distancelong = distancelong;
	}

	/**
	 * Rückgabe des Angebots mit minimalen Informationen
	 */
	@Override
	public String toString() {
		return this.angebotId + " " + this.titel + " " + this.beschreibung + " " + this.betrag + " " + this.waehrung
				+ " " + this.angebotsKategorieId.getBezeichnung() + " " + this.anbieter;
	}

	/**
	 * Holen der Postleitzahl
	 * 
	 * @return Postleitzahl
	 */
	public String getPlz() {
		return plz;
	}

	/**
	 * Setzen der Postleitzahl
	 * 
	 * @param plz Postleitzahl
	 */
	public void setPlz(String plz) {
		this.plz = plz;
	}

	/**
	 * Holen der Entfernung
	 * 
	 * @return Entfernung
	 */
	public String getDistance() {
		return distance;
	}

	/**
	 * Setzen der Entfernung
	 * 
	 * @param distance Entfernung
	 */
	public void setDistance(String distance) {
		this.distance = distance;
	}

	/**
	 * Holen der zugeordneten Angebotskategorie
	 * 
	 * @return zugeordnete Angebotskategorie
	 */
	public Angebotskategorie getAngebotsKategorieId() {
		return angebotsKategorieId;
	}

	/**
	 * Setzen der zugeordneten Angebotskategorie
	 * 
	 * @param angebotsKategorieId zugeordnete Angebotskategorie
	 */
	public void setAngebotsKategorieId(Angebotskategorie angebotsKategorieId) {
		this.angebotsKategorieId = angebotsKategorieId;
	}

	/**
	 * Holen der Stadt
	 * 
	 * @return Stadt
	 */
	public String getStadt() {
		return stadt;
	}

	/**
	 * Setzen der Stadt
	 * 
	 * @param stadt Stadt
	 */
	public void setStadt(String stadt) {
		this.stadt = stadt;
	}

	/**
	 * Holen des Angebotstypen
	 * 
	 * @return Angebotstyp
	 */
	public int getOfferTypeId() {
		return offerTypeId;
	}

	/**
	 * Setzen des Angebotstypen
	 * 
	 * @param offerTypeId Angebotstyp (1/2)
	 */
	public void setOfferTypeId(int offerTypeId) {
		this.offerTypeId = offerTypeId;
	}

	/**
	 * Holen des Aufrufzählers
	 * 
	 * @return Aufrufzähler
	 */
	public int getViews() {
		return views;
	}

	/**
	 * Setzen des Aufrufzählers
	 * 
	 * @param views Aufrufzählerstand
	 */
	public void setViews(int views) {
		this.views = views;
	}
}