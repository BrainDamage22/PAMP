/**
 * 
 */
package de.edu.pamp.validation;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

/**
 * @author Tobias
 *
 *         Hilfsklasse für die Validierung eines Benutzers
 */
@Service
public class NutzerValidation {
	private Pattern mo_ptr_email = Pattern
			.compile("^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$");

	private Pattern mo_ptr_pwd = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,14}$");

	private Pattern mo_ptr_plz = Pattern.compile("^[0-9]{5}$");

	/**
	 * Prüft, ob eine angegebene Postleitzahl ein gültiges Format hat
	 * 
	 * @param iv_plz Postleitzahl
	 * @return True oder False
	 */
	public boolean isPostalcodeValid(String iv_plz) {
		if (!isPostalcodeEmpty(iv_plz)) {
			return mo_ptr_plz.matcher(iv_plz).matches();
		} else {
			return true;
		}
	}

	/**
	 * Prüft, ob eine angegebene Stadt leer ist
	 * 
	 * @param iv_city Stadt
	 * @return FALSE, wenn Eingabe leer
	 */
	public boolean isCityEmpty(String iv_city) {
		return iv_city.trim().isEmpty();
	}

	/**
	 * Prüft, ob eine angegebene Straße leer ist
	 * 
	 * @param iv_street Straße
	 * @return FALSE, wenn Eingabe leer
	 */
	public boolean isStreetEmpty(String iv_street) {
		return iv_street.trim().isEmpty();
	}

	/**
	 * Prüft, ob eine angegebene Postleitzahl leer ist
	 * 
	 * @param iv_plz Postleitzahl
	 * @return FALSE, wenn Eingabe leer
	 */
	public boolean isPostalcodeEmpty(String iv_plz) {
		return iv_plz.trim().isEmpty();
	}

	/**
	 * Prüft, ob eine angegebene E-Mail Adresse einen gültigen Aufbau besitzt
	 * 
	 * @param iv_email Email
	 * @return True oder False
	 */
	public boolean isEmailValid(String iv_email) {
		if (!isEmailEmpty(iv_email)) {
			return mo_ptr_email.matcher(iv_email).matches();
		} else {
			return true;
		}
	}

	/**
	 * Prüft, ob beide E-Mail Adressen identisch sind
	 * 
	 * @param iv_email Email
	 * @param iv_emailRE Email wiederholt
	 * @return TRUE, wenn Emails übereinstimmen
	 */
	public boolean isEmailEqual(String iv_email, String iv_emailRE) {
		if (!isEmailEmpty(iv_email) && !isEmailEmpty(iv_emailRE)) {
			return iv_email.equals(iv_emailRE);
		} else {
			return true;
		}
	}

	/**
	 * Prüft, ob die angegebene E-Mail Adresse leer ist
	 * 
	 * @param iv_emailRE Wiederholte Eingabe der Email
	 * @return FALSE, wenn Eingabe leer
	 */
	public boolean isEmailEmpty(String iv_emailRE) {
		return iv_emailRE.trim().isEmpty();
	}

	/**
	 * Prüft, ob die angegebenes Passwort leer ist
	 * 
	 * @param iv_passwordRE Wiederholte Eingabe des Passwortes
	 * @return FALSE, wenn Eingabe leer
	 */
	public boolean isPasswordEmpty(String iv_passwordRE) {
		return iv_passwordRE.trim().isEmpty();
	}

	/**
	 * Prüft, ob beide Passwörter identisch sind
	 * 
	 * @param iv_password Eingabe Passwort
	 * @param iv_passwordRE Wiederholte Eingabe des Passworts
	 * @return TRUE, wenn Passwoerter uebereinstimmen
	 */
	public boolean isPasswordEqual(String iv_password, String iv_passwordRE) {
		if (!isPasswordEmpty(iv_password) && !isPasswordEmpty(iv_passwordRE)) {
			return iv_password.equals(iv_passwordRE);
		} else {
			return true;
		}
	}

	/**
	 * Prüft, ob das Passwort folgende Kriterien erfüllt: Mindestens ein
	 * Großbuchstabe - Mindestens ein Kleinbuchstabe - Mindestens eine Zahl -
	 * Mindestens 8 Zeichen - Maximal 14 Zeichen
	 * 
	 * @param iv_password Eingabe des Passwortes
	 * @return TRUE, wenn Passwort gültig
	 */
	public boolean isPasswordValid(String iv_password) {
		if (!isPasswordEmpty(iv_password)) {
			return mo_ptr_pwd.matcher(iv_password).matches();
		} else {
			return true;
		}
	}
}
