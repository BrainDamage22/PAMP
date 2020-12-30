package de.edu.pamp.dto;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * 
 * @author Tobias
 *
 *         Eigenschaften der Entität: Verifikationstoken
 */
@Entity
public class VerificationToken {
	private static final int EXPIRATION = 60 * 24;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String token;

	@OneToOne(targetEntity = Nutzer.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "user_id")
	private Nutzer user;

	private Date expiryDate;

	/**
	 * Berechnung des Gültigkeitsdatum. Bis zu diesem Stichtag ist ein
	 * Verifikationstoken gültig
	 * 
	 * @param iv_expiryTimeInMinutes Gültigkeitsdauer
	 * @return Gültigkeitsdatum
	 */
	private Date calculateExpiryDate(int iv_expiryTimeInMinutes) {
		Calendar lo_cal = Calendar.getInstance();
		lo_cal.setTime(new Timestamp(lo_cal.getTime().getTime()));
		lo_cal.add(Calendar.MINUTE, iv_expiryTimeInMinutes);
		return new Date(lo_cal.getTime().getTime());
	}

	/**
	 * Liefert den Maximalen Gültigkeitszeitraum zurück
	 * 
	 * @return maximaler Gültigkeitszeitraum
	 */
	public static int getEXPIRATION() {
		return EXPIRATION;
	}

	/**
	 * Konstruktor
	 */
	public VerificationToken() {
	}

	/**
	 * Konstruktor. Die maximale Gültigkeitsdauer wird automatisch auf 24 Stunden
	 * gesetzt
	 * 
	 * @param iv_token Verifikationstoken
	 * @param io_user  dazugehöriger Nutzer
	 */
	public VerificationToken(String iv_token, Nutzer io_user) {
		this.token = iv_token;
		this.user = io_user;
		this.expiryDate = calculateExpiryDate(EXPIRATION);
	}

	/**
	 * Holen der eindeutigen Identifikationsnummer
	 * 
	 * @return eindeutige Identifikationsnummer
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Setzen der eindeutigen Identifikationsnummer
	 * 
	 * @param iv_id eindeutige Identifikationsnummer
	 */
	public void setId(Long iv_id) {
		this.id = iv_id;
	}

	/**
	 * Holen des Verifikationstoken
	 * 
	 * @return Verifikationstoken
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Setzen des Verifikationstoken
	 * 
	 * @param iv_token Verifikationstoken
	 */
	public void setToken(String iv_token) {
		this.token = iv_token;
	}

	/**
	 * Holen des dazugehörigen Nutzers
	 * 
	 * @return dazugehöriger Nutzer
	 */
	public Nutzer getUser() {
		return user;
	}

	/**
	 * Setzen des dazugehörigen Nutzers
	 * 
	 * @param io_user dazugehöriger Nutzer
	 */
	public void setUser(Nutzer io_user) {
		this.user = io_user;
	}

	/**
	 * Holen des Gültigkeitsdatum
	 * 
	 * @return Gültigkeitsdatum
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * Setzen des Gültigkeitsdatum
	 * 
	 * @param iv_expiryDate Gültigkeitsdatum
	 */
	public void setExpiryDate(Date iv_expiryDate) {
		this.expiryDate = iv_expiryDate;
	}
}