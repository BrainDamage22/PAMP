package de.edu.pamp.controller;

import java.util.Calendar;
import java.util.UUID;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.dto.VerificationToken;
import de.edu.pamp.mail.NotificationService;
import de.edu.pamp.services.NutzerService;
import de.edu.pamp.validation.NutzerValidation;

/**
 * Controller für die Verarbeitung des Logins
 * 
 * @author Lukas
 *
 */
@Controller
public class LoginController {

	@Autowired
	private NotificationService notificationService;
	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private NutzerValidation nutzerValidation;
	private String token;

	/**
	 * Jegliche Verarbeitung für den Login
	 *
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@RequestMapping("/login")
	public String loginForm(Model io_model) {
		return "loginForm";
	}

	/**
	 * Jegliche Verarbeitung für das Zurücksetzen eines Passworts
	 * 
	 * @param email      E-Mail-Adresse für die das Passwort zurückgesetzt werden
	 *                   soll
	 * @param model      aktuelles Model
	 * @param io_request aktueller HTML-Request
	 * @return Zielseite
	 * @throws MessagingException Nachrichtenfehler
	 */
	@RequestMapping("/reset")
	public String resetPasswort(@RequestParam("reset_email") String email, Model model, WebRequest io_request)
			throws MessagingException {

		Nutzer nutzer = nutzerService.findUserById(email);

		if (nutzerValidation.isEmailEmpty(email)) {
			model.addAttribute("empty", true);
			return "loginForm";
		}

		if (nutzer == null) {
			model.addAttribute("usernotpresent", true);
			return "loginForm";
		}

		String lv_token = UUID.randomUUID().toString();
		String lv_appUrl = io_request.getContextPath();
		String lv_confirmationUrl = lv_appUrl + "resetPassword.html?token=" + lv_token;

		if (notificationService.sendPasswortRecovery(nutzer, lv_confirmationUrl)) {
			nutzerService.createVerificationToken(nutzer, lv_token);
			model.addAttribute("sendsuccess", true);
		} else {
			model.addAttribute("fail", true);
		}

		return "loginForm";
	}

	/**
	 * Registrierungsmail erneut versenden
	 * 
	 * @param email E-Mail-Adresse für die erneut eine Mail versendet werden soll
	 * @param model aktuelles Model
	 * @return Name der Login-Seite
	 */
	@RequestMapping("/resend")
	public String resendLink(@RequestParam("resend_email") String email, Model model) {

		if (nutzerValidation.isEmailEmpty(email)) {
			model.addAttribute("empty", true);
			return "loginForm";
		}

		if (nutzerService.findUserById(email) == null) {
			model.addAttribute("usernotpresent", true);
			return "loginForm";
		}

		if (notificationService.resendRegistration(email)) {
			model.addAttribute("success", true);
		} else {
			model.addAttribute("failure", true);
		}

		return "loginForm";
	}

	/**
	 * Initialisierung der Seite für das Zurücksetzen des Passworts
	 * 
	 * @param io_request HTML-Request
	 * @param io_model   aktuelles Model
	 * @param iv_token   aktueller Verifikationstoken
	 * @return Name der Login-Seite
	 */
	@RequestMapping(value = "/resetPassword.html", method = RequestMethod.GET)
	public String resetPassword(WebRequest io_request, Model io_model, @RequestParam("token") String iv_token) {
		VerificationToken lo_verificationToken = nutzerService.getVerificationToken(iv_token);

		if (lo_verificationToken == null) {
			return "error";
		}

		Nutzer lo_user = lo_verificationToken.getUser();
		Calendar lo_cal = Calendar.getInstance();

		if ((lo_verificationToken.getExpiryDate().getTime() - lo_cal.getTime().getTime()) <= 0) {
			return "error";
		}

		this.token = iv_token;
		io_model.addAttribute("nutzer", lo_user);

		return "resetpassword";
	}

	/**
	 * Verarbeitung nach dem Zurücksetzen des Passworts
	 * 
	 * @param model      aktuelles Model
	 * @param password   neues Passwort
	 * @param passwordRE neues Passwort (nochmal)
	 * @return Name der Login-Seite
	 */
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public String resetPasswordConfirm(Model model, @RequestParam("password") String password,
			@RequestParam("password_re") String passwordRE) {

		boolean lf_oblFields = false;

		if (nutzerValidation.isPasswordEmpty(password)) {
			model.addAttribute("emptyPassword", true);
			lf_oblFields = true;
		}

		if (nutzerValidation.isPasswordEmpty(passwordRE)) {
			model.addAttribute("emptyPasswordRE", true);
			lf_oblFields = true;
		}

		if (lf_oblFields) {
			return "resetpassword";
		}

		if (!nutzerValidation.isPasswordValid(password)) {
			model.addAttribute("invalidPassword", true);
			return "resetpassword";
		}

		if (!nutzerValidation.isPasswordEqual(password, passwordRE)) {
			model.addAttribute("nomatch", true);
			return "resetpassword";
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		VerificationToken verificationToken = nutzerService.getVerificationToken(token);
		Nutzer nutzer = verificationToken.getUser();
		nutzer.setPassword(encoder.encode(password));
		nutzerService.updateNutzerPasswort(nutzer);

		return "loginForm";
	}
}