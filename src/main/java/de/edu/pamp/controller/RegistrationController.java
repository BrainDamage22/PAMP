package de.edu.pamp.controller;

import java.util.Calendar;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
 * 
 * @author Tobias
 *
 *         Services rund um die Registrierung
 */
@Controller
public class RegistrationController {

	private JavaMailSender mailSender;
	@Autowired
	private NotificationService notificationService = new NotificationService(mailSender);
	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private NutzerValidation nutzerValidation;

	/**
	 * Darstellung der Registrierung eines neuen Nutzers
	 * 
	 * @param io_model aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/register")
	public String registrationForm(Model io_model) {
		io_model.addAttribute("nutzer", new Nutzer());
		return "registrationForm";
	}

	/**
	 * Ausführung der Registrierung eines neuen Nutzers
	 * 
	 * @param checked          AGB-Kennzeichen (True/False)
	 * @param io_user          neu anzulegender Nutzer
	 * @param io_bindingResult systemseitige Validierungen
	 * @param io_model         aktuelles Model
	 * @param io_request       aktueller WebRequest
	 * @return Zielseite
	 * @throws MessagingException Nachrichtenfehler
	 */
	@PostMapping("/register")
	public String registerUser(@RequestParam(value = "checked", required = false) String checked, @Valid Nutzer io_user,
			BindingResult io_bindingResult, Model io_model, WebRequest io_request) throws MessagingException {

		// Hier Fehler setzen damit diese immer in Verbindung mit allen anderen "darf
		// nicht leer sein"
		if (nutzerValidation.isEmailEmpty(io_user.getEmailRE())) {
			io_model.addAttribute("emptyEmailRE", true);
		}

		if (nutzerValidation.isPasswordEmpty(io_user.getPasswordRE())) {
			io_model.addAttribute("emptyPasswordRE", true);
		}

		if (io_bindingResult.hasErrors() || (nutzerValidation.isPasswordEmpty(io_user.getPasswordRE()))
				|| (nutzerValidation.isEmailEmpty(io_user.getEmailRE()))) {
			return "registrationForm";
		}

		// E-Mail
		if (!nutzerValidation.isEmailValid(io_user.getEmail())) {
			io_model.addAttribute("invalidEmail", true);
			return "registrationForm";
		}

		if (!nutzerValidation.isEmailEqual(io_user.getEmail(), io_user.getEmailRE())) {
			io_model.addAttribute("emailnomatch", true);
			return "registrationForm";
		}

		// Passwort
		if (!nutzerValidation.isPasswordValid(io_user.getPassword())) {
			io_model.addAttribute("invalidPassword", true);
			return "registrationForm";
		}

		if (!nutzerValidation.isPasswordEqual(io_user.getPassword(), io_user.getPasswordRE())) {
			io_model.addAttribute("passwordnomatch", true);
			return "registrationForm";
		}

		// Postleitzahl
		if (!nutzerValidation.isPostalcodeValid(io_user.getPlz())) {
			io_model.addAttribute("invalidPLZ", true);
			return "registrationForm";
		}

		if (io_user.getGeburtsdatum() == null) {
			io_model.addAttribute("emptydate", true);
			return "registrationForm";
		}

		if (checked == null) {
			io_model.addAttribute("agb", true);
			return "registrationForm";
		}

		// Immer letzte Pruefung!!
		if (nutzerService.isUserPresent(io_user.getEmail())) {
			io_model.addAttribute("userExists", true);
			return "registrationForm";
		}

		String lv_token = UUID.randomUUID().toString();
		String lv_appUrl = io_request.getContextPath();
		String lv_confirmationUrl = lv_appUrl + "registrationConfirm.html?token=" + lv_token;
		if (notificationService.sendRegistrationConfirmation(io_user, lv_confirmationUrl)) {
			nutzerService.createNutzer(io_user);
			nutzerService.createVerificationToken(io_user, lv_token);
		} else {
			io_model.addAttribute("fail", true);
			return "registrationForm";
		}
		io_model.addAttribute("activate", true);
		return "loginForm";
	}

	/**
	 * Behandlung der Registrierungsbestätigung
	 * 
	 * @param io_request aktueller WebRequest
	 * @param io_model   aktuelles Model
	 * @param iv_token   Validierungstoken
	 * @return Zielseite
	 */
	@RequestMapping(value = "/registrationConfirm.html", method = RequestMethod.GET)
	public String confirmRegistration(WebRequest io_request, Model io_model, @RequestParam("token") String iv_token) {
		VerificationToken lo_verificationToken = nutzerService.getVerificationToken(iv_token);

		if (lo_verificationToken == null) {
			return "error";
		}

		Nutzer lo_user = lo_verificationToken.getUser();
		Calendar lo_cal = Calendar.getInstance();
		if ((lo_verificationToken.getExpiryDate().getTime() - lo_cal.getTime().getTime()) <= 0) {
			return "error";
		}
		lo_user.setEnabled(true);
		nutzerService.enableNutzer(lo_user);
		return "loginForm";
	}
}