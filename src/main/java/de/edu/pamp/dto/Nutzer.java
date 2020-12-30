package de.edu.pamp.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * @author Tobias
 *
 *         Beschreibung der Entität: Nutzer
 */
@Entity
@Table(name = "benutzer")
public class Nutzer {

	@Id
	@Column(unique = true)
	@NotEmpty
	private String email;

	@Transient
	private String emailRE;

	@NotEmpty
	private String vorname;
	@NotEmpty
	private String nachname;
	@NotEmpty
	private String adresse;
	@NotEmpty
	private String plz;
	@NotEmpty
	private String stadt;

	@NotEmpty
	private String password;

	@Transient
	private String passwordRE;

	@DateTimeFormat(pattern = "yyyy-mm-dd")
	private Date geburtsdatum;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "USER_ROLES", joinColumns = {
			@JoinColumn(name = "USER_EMAIL", referencedColumnName = "email") }, inverseJoinColumns = {
					@JoinColumn(name = "ROLE_NAME", referencedColumnName = "name") })
	private List<Role> roles;

	@OneToMany(mappedBy = "anbieter") // mappedBy = Angabe der korrespondierenden Referenzvariable des DTO-Objekts
	private List<Angebot> offerCreated;

	@OneToMany(mappedBy = "kaeufer")
	private List<Angebot> offerBought;

	@OneToMany(mappedBy = "messageFrom")
	private List<Message> messagesSent;

	@OneToMany(mappedBy = "messageTo")
	private List<Message> messagesReceived;

	private boolean sperre = false;

	@Column(name = "enabled")
	private boolean enabled;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fk_lockedBy")
	private Nutzer lockedBy;

	@Column(columnDefinition = "TEXT")
	private String profilbild;

	/**
	 * Konstruktor
	 */
	public Nutzer() {
	}

	/**
	 * Konstruktor
	 * 
	 * @param iv_email        eindeutige Identifikationsnummer
	 * @param iv_vorname      Vorname
	 * @param iv_nachname     Nachname
	 * @param iv_adresse      Straße
	 * @param iv_plz          Postleitzahl
	 * @param iv_stadt        Stadt
	 * @param iv_password     Passwort
	 * @param iv_geburtsdatum Geburtsdatum
	 */
	public Nutzer(@Email @NotEmpty String iv_email, @NotEmpty String iv_vorname, @NotEmpty String iv_nachname,
			@NotEmpty String iv_adresse, @NotEmpty String iv_plz, @NotEmpty String iv_stadt,
			@Size(min = 8) String iv_password, @NotEmpty Date iv_geburtsdatum) {
		this.email = iv_email;
		this.vorname = iv_vorname;
		this.nachname = iv_nachname;
		this.adresse = iv_adresse;
		this.plz = iv_plz;
		this.stadt = iv_stadt;
		this.password = iv_password;
		this.geburtsdatum = iv_geburtsdatum;
		this.sperre = false;
	}

	/**
	 * Holen des Vornamen
	 * 
	 * @return Vorname
	 */
	public String getVorname() {
		return vorname;
	}

	/**
	 * Setzen des Vornamne
	 * 
	 * @param iv_vorname Vorname
	 */
	public void setVorname(String iv_vorname) {
		this.vorname = iv_vorname;
	}

	/**
	 * Holen des Nachnamen
	 * 
	 * @return Nachname
	 */
	public String getNachname() {
		return nachname;
	}

	/**
	 * Setzen des Nachnamen
	 * 
	 * @param iv_nachname Nachname
	 */
	public void setNachname(String iv_nachname) {
		this.nachname = iv_nachname;
	}

	/**
	 * Holen der eindeutigen Identifikationsnummer
	 * 
	 * @return eindeutige Identifikationsnummer
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Setzen der eindeutigen Identifikationsnummer
	 * 
	 * @param iv_email eindeutige Identifikationsnummer
	 */
	public void setEmail(String iv_email) {
		this.email = iv_email;
	}

	/**
	 * Holen der Straße
	 * 
	 * @return Straße
	 */
	public String getAdresse() {
		return adresse;
	}

	/**
	 * Setzen der Straße
	 * 
	 * @param iv_adresse Straße
	 */
	public void setAdresse(String iv_adresse) {
		this.adresse = iv_adresse;
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
	 * Holen des Geburtsdatum als String
	 * 
	 * @return Geburtsdatum
	 */
	public String getFormattedGeburtsdatum() {
		// liefert Geburtsdatum als formatierten String zurück
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		return formatter.format(geburtsdatum);
	}

	/**
	 * Holen des Geburtsdatum als Date
	 * 
	 * @return Geburtsdatum
	 */
	public Date getGeburtsdatum() {
		return geburtsdatum;
	}

	/**
	 * Setzen des Geburtsdatum
	 * 
	 * @param iv_geburtsdatum Geburtsdatum
	 */
	public void setGeburtsdatum(Date iv_geburtsdatum) {
		this.geburtsdatum = iv_geburtsdatum;
	}

	/**
	 * Holen Sperrkennzeichen, ob Nutzer gesperrt ist
	 * 
	 * @return Sperrkennzeichen
	 */
	public boolean isSperre() {
		return sperre;
	}

	/**
	 * Setzen Sperrkennzeichen, ob Nutzer gesperrt ist
	 * 
	 * @param iv_sperre Sperrkennzeichen (TRUE/FALSE)
	 */
	public void setSperre(boolean iv_sperre) {
		this.sperre = iv_sperre;
	}

	/**
	 * Holen des Passworts
	 * 
	 * @return 256-bit verschlüsselte Passwort
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Setzen des 256-bit verschlüsselte Passworts
	 * 
	 * @param iv_password 256-bit verschlüsselte Passwort
	 */
	public void setPassword(String iv_password) {
		this.password = iv_password;
	}

	/**
	 * Holen des Passworts (erneute Eingabe)
	 * 
	 * @return Passwort (erneute Eingabe)
	 */
	public String getPasswordRE() {
		return passwordRE;
	}

	/**
	 * Setzen des Passworts (erneute Eingabe)
	 * 
	 * @param iv_passwordRE Passwort (erneute Eingabe)
	 */
	public void setPasswordRE(String iv_passwordRE) {
		this.passwordRE = iv_passwordRE;
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
	 * @param iv_plz Postleitzahl
	 */
	public void setPlz(String iv_plz) {
		this.plz = iv_plz;
	}

	/**
	 * Setzen der Stadt
	 * 
	 * @param iv_stadt Stadt
	 */
	public void setStadt(String iv_stadt) {
		this.stadt = iv_stadt;
	}

	/**
	 * Holen der eindeutigen Identifikationsnummer (erneute Eingabe)
	 * 
	 * @return eindeutige Identifikationsnummer (erneute Eingabe
	 */
	public String getEmailRE() {
		return emailRE;
	}

	/**
	 * Setzen der eindeutigen Identifikationsnummer (erneute Eingabe)
	 * 
	 * @param iv_emailRE eindeutige Identifikationsnummer (erneute Eingabe)
	 */
	public void setEmailRE(String iv_emailRE) {
		this.emailRE = iv_emailRE;
	}

	/**
	 * Holen der zugeordneten Rollen
	 * 
	 * @return Rollen
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * Setzen der zugeordneten Rollen
	 * 
	 * @param it_roles Rollen
	 */
	public void setRoles(List<Role> it_roles) {
		this.roles = it_roles;
	}

	/**
	 * Holen des Aktiv-Kennzeichens
	 * 
	 * @return Aktiv-Kennzeichens
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Setzen des Aktiv-Kennzeichens
	 * 
	 * @param iv_enabled Aktiv-Kennzeichens (True/False)
	 */
	public void setEnabled(boolean iv_enabled) {
		this.enabled = iv_enabled;
	}

	/**
	 * Holen der angelegten Angebote
	 * 
	 * @return Angebotsliste
	 */
	public List<Angebot> getOfferCreated() {
		return this.offerCreated;
	}

	/**
	 * Holen der gekauften Angebote
	 * 
	 * @return Angebotsliste
	 */
	public List<Angebot> getOfferBought() {
		return this.offerBought;
	}

	/**
	 * Holen der gesendeten Nachrichten
	 * 
	 * @return Nachrichtenliste
	 */
	public List<Message> getMessagesSent() {
		return this.messagesSent;
	}

	/**
	 * Holen der empfangenen Nachrichten
	 * 
	 * @return Nachrichtenliste
	 */
	public List<Message> getMessagesReceived() {
		return this.messagesReceived;
	}

	/**
	 * Setzen der angelegten Angebote
	 * 
	 * @param offerCreated Angebotsliste
	 */
	public void setOfferCreated(List<Angebot> offerCreated) {
		this.offerCreated = offerCreated;
	}

	/**
	 * Setzen der gekauften Angebote
	 * 
	 * @param offerBought Angebotsliste
	 */
	public void setOfferBought(List<Angebot> offerBought) {
		this.offerBought = offerBought;
	}

	/**
	 * Setzen der gesendeten Nachrichten
	 * 
	 * @param messagesSent Angebotsliste
	 */
	public void setMessagesSent(List<Message> messagesSent) {
		this.messagesSent = messagesSent;
	}

	/**
	 * Setzen der empfangenen Nachrichten
	 * 
	 * @param messagesReceived Angebotsliste
	 */
	public void setMessagesReceived(List<Message> messagesReceived) {
		this.messagesReceived = messagesReceived;
	}

	/**
	 * Holen des Profilbilds
	 * 
	 * @return Profilbild
	 */
	public String getProfilbild() {
		return profilbild;
	}

	/**
	 * Setzen des Profilbilds
	 * 
	 * @param profilbild Profilbild
	 */
	public void setProfilbild(String profilbild) {
		this.profilbild = profilbild;
	}

	/**
	 * Rückgabe eines Nutzers mit minimalen Informationen
	 */
	@Override
	public String toString() {
		return this.vorname + " " + this.nachname + " " + this.email + " " + this.adresse + " " + this.plz + " "
				+ this.stadt + " " + this.geburtsdatum;
	}

	/**
	 * Holen des Nutzers der die Sperrung vollzog
	 * 
	 * @return lockedBy Nutzer (Admin)
	 */
	public Nutzer getLockedBy() {
		return lockedBy;
	}

	/**
	 * Setzen des Nutzers der die Sperrung vollzog
	 * 
	 * @param lockedBy Nutzer (Admin)
	 */
	public void setLockedBy(Nutzer lockedBy) {
		this.lockedBy = lockedBy;
	}
}