package de.edu.pamp.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.geolocation.GoogleModel;
import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;
import de.edu.pamp.validation.NutzerValidation;

/**
 * Controller zum Anzeigen und Verwalten seines eigenen Profils
 *
 * @author Lukas
 */
@Controller
public class ProfileController {

	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private NutzerValidation nutzerValidation;
	@Autowired
	private MessageService messageService;
	private Nutzer lo_currentUser;

	/**
	 * Anzeigen des Profils
	 *
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/profile")
	public String showProfile(Model io_model) {
		lo_currentUser = nutzerService.findUserById(nutzerService.getCurrentUserId());
		io_model.addAttribute("nutzer", lo_currentUser);

		prepModel(io_model);

		return "profile";
	}

	/**
	 * Methode zum Ändern des Vornamens
	 *
	 * @param vorname  Eingabe des neuen Vornamens
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@RequestMapping(value = "/profile", params = "vorname", method = RequestMethod.POST)
	public String alterVorname(@RequestParam("u_vorname") String vorname, Model io_model) {
		if (vorname.equals("")) {
			io_model.addAttribute("emptyvorname", true);
		} else {
			lo_currentUser.setVorname(vorname);
			nutzerService.updateNutzerVorname(lo_currentUser);
		}
		io_model.addAttribute("nutzer", nutzerService.findUserById(nutzerService.getCurrentUserId()));

		prepModel(io_model);

		return "profile";
	}

	/**
	 * Methode zum Ändern des Nachnamens
	 *
	 * @param nachname Eingabe des neuen Nachnamens
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@RequestMapping(value = "/profile", params = "nachname", method = RequestMethod.POST)
	public String alterNachname(@RequestParam("u_nachname") String nachname, Model io_model) {
		if (nachname.equals("")) {
			io_model.addAttribute("emptynachname", true);
		} else {
			lo_currentUser.setNachname(nachname);
			nutzerService.updateNutzerNachname(lo_currentUser);
		}
		io_model.addAttribute("nutzer", nutzerService.findUserById(nutzerService.getCurrentUserId()));

		prepModel(io_model);

		return "profile";
	}

	/**
	 * Methode zum Ändern des Geburtsdatums
	 *
	 * @param geburtsdatum Eingabe des neuen Geburtsdatums
	 * @param io_model     Aktuelles Model
	 * @return Zielseite
	 */
	@RequestMapping(value = "/profile", params = "geburtsdatum", method = RequestMethod.POST)
	public String alterGeburtsdatum(@RequestParam("u_geburtsdatum") String geburtsdatum, Model io_model) {

		if (geburtsdatum.isEmpty()) {
			io_model.addAttribute("emptydate", true);
		} else {
			try {
				SimpleDateFormat lo_dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
				java.util.Date lv_geburtsdatum = lo_dateFormat.parse(geburtsdatum);
				java.sql.Date lo_sqlDate = new java.sql.Date(lv_geburtsdatum.getTime());

				lo_currentUser.setGeburtsdatum(lo_sqlDate);
				nutzerService.updateNutzerGeburtsdatum(lo_currentUser);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		io_model.addAttribute("nutzer", nutzerService.findUserById(nutzerService.getCurrentUserId()));

		prepModel(io_model);

		return "profile";
	}

	/**
	 * Methode zum Ändern der Adresse
	 *
	 * @param plz      Eingabe der neuen Postleitzahl
	 * @param stadt    Eingabe der neuen Stadt
	 * @param adresse  Eingabe der neuen Adresse
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@RequestMapping(value = "/profile", params = "adresse", method = RequestMethod.POST)
	public String alterAdresse(@RequestParam("u_plz") String plz, @RequestParam("u_stadt") String stadt,
			@RequestParam("u_adresse") String adresse, Model io_model) {

		if (nutzerValidation.isPostalcodeEmpty(plz) || nutzerValidation.isCityEmpty(stadt)
				|| nutzerValidation.isStreetEmpty(adresse)) {
			io_model.addAttribute("emptyadresse", true);

		} else if (!nutzerValidation.isPostalcodeValid(plz)) {
			io_model.addAttribute("invalidPLZ", true);

		} else {
			lo_currentUser.setPlz(plz);
			lo_currentUser.setStadt(stadt);
			lo_currentUser.setAdresse(adresse);
			nutzerService.updateNutzerAdresse(lo_currentUser);
		}
		io_model.addAttribute("nutzer", nutzerService.findUserById(nutzerService.getCurrentUserId()));

		prepModel(io_model);

		return "profile";
	}

	/**
	 * Methode zum Ändern des Passworts
	 *
	 * @param passwortalt   Eingabe des alten Passworts
	 * @param passwortneu   Eingabe des neuen Passworts
	 * @param passwortneure Wiederholte Eingabe des neuen Passworts
	 * @param io_model      Aktuelles Model
	 * @return Zielseite
	 */
	@RequestMapping(value = "/profile", params = "passwort", method = RequestMethod.POST)
	public String alterPassword(@RequestParam("u_passwortalt") String passwortalt,
			@RequestParam("u_passwortneu") String passwortneu, @RequestParam("u_passwortneure") String passwortneure,
			Model io_model) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		if (nutzerValidation.isPasswordEmpty(passwortalt) || nutzerValidation.isPasswordEmpty(passwortneu)
				|| nutzerValidation.isPasswordEmpty(passwortneure)) {
			io_model.addAttribute("emptypassword", true);

		} else if (!encoder.matches(passwortalt, lo_currentUser.getPassword())) {
			io_model.addAttribute("invalidPasswordOld", true);

		} else if (!nutzerValidation.isPasswordValid(passwortneu)) {
			io_model.addAttribute("invalidPasswordNew", true);

		} else if (!nutzerValidation.isPasswordEqual(passwortneu, passwortneure)) {
			io_model.addAttribute("unequalPasswords", true);

		} else {
			lo_currentUser.setPassword(encoder.encode(passwortneu));
			nutzerService.updateNutzerPasswort(lo_currentUser);
		}
		io_model.addAttribute("nutzer", nutzerService.findUserById(nutzerService.getCurrentUserId()));

		prepModel(io_model);

		return "profile";
	}

	/**
	 * Methode zum Hochladen eines (neuen) Profilbilds
	 *
	 * @param file  Hochgeladenes Profilbild
	 * @param model Aktuelles Model
	 * @return Zielseite
	 * @throws IOException Fehler beim Hochladen des Bildes
	 */
	@RequestMapping(value = "profile", params = "Hochladen", method = RequestMethod.POST)
	public String uploadProfilImage(@RequestParam("image") MultipartFile file, Model model) throws IOException {

		try (InputStream input = file.getInputStream()) {
			try {
				ImageIO.read(input).toString();
				// It's an image (only BMP, GIF, JPG and PNG are recognized).
			} catch (Exception e) {
				// It's not an image.
				model.addAttribute("noimage", true);
				model.addAttribute("nutzer", nutzerService.findUserById(nutzerService.getCurrentUserId()));

				prepModel(model);

				return "profile";
			}
		}

		BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

		if (bufferedImage.getHeight() > 800 || bufferedImage.getWidth() > 800) {
			model.addAttribute("tobig", true);
			model.addAttribute("nutzer", nutzerService.findUserById(nutzerService.getCurrentUserId()));

			prepModel(model);

			return "profile";
		}

		byte[] fileContent = file.getBytes();

		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		Nutzer nutzer = nutzerService.getCurrentUser();

		nutzer.setProfilbild("data:image/jpg;base64," + encodedString);
		nutzerService.updateProfilbild(nutzer);

		model.addAttribute("nutzer", nutzerService.findUserById(nutzerService.getCurrentUserId()));

		prepModel(model);

		return "profile";
	}

	/**
	 * Vorbereiten des Models
	 *
	 * @param io_model Aktuelles Model
	 */
	private void prepModel(Model io_model) {
		CommonService.prepModel(io_model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		GoogleModel lo_googleModel = new GoogleModel();
		lo_googleModel.setApiKey(io_model);
		lo_googleModel.showUserOnMap(io_model, lo_currentUser);

		io_model.addAttribute("currentRating", nutzerService.getUserRating(lo_currentUser.getEmail()));
	}
}