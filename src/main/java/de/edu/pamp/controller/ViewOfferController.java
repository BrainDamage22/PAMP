package de.edu.pamp.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import de.edu.pamp.mail.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import de.edu.pamp.dto.Angebot;
import de.edu.pamp.dto.Datei;
import de.edu.pamp.dto.Message;
import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.dto.Tag;
import de.edu.pamp.services.AngebotService;
import de.edu.pamp.services.AngebotskategorieService;
import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.DateiService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;
import de.edu.pamp.services.TagService;
import de.edu.pamp.validation.NutzerValidation;

/**
 * 
 * @author Tobias
 *
 *         Services rund um die Behandlung der Angebotsanzeige
 */
@Controller
public class ViewOfferController {

	@Autowired
	private AngebotService angebotService;
	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private NutzerValidation nutzerValidation;
	@Autowired
	private TagService tagService;
	@Autowired
	private AngebotskategorieService angebotskategorieService;
	@Autowired
	private DateiService dateiService;
	@Autowired
	private NotificationService notificationService;

	private int offerId;
	private static String pdfType = "pdf";

	/**
	 * Anzeigen eines Angebots
	 * 
	 * @param io_model   aktuelles Model
	 * @param iv_offerId eindeutige Identifikationsnummer
	 * @return Zielseite
	 */
	@RequestMapping(value = "/viewOffer", method = RequestMethod.GET)
	public String showOffer(Model io_model, @RequestParam("offerId") int iv_offerId) {
		offerId = iv_offerId;

		if (nutzerService.getCurrentUser() != angebotService.findOfferById(iv_offerId).getAnbieter()) {
			Angebot angebot = angebotService.findOfferById(iv_offerId);
			angebotService.increaseViews(angebot);
		}

		prepOfferModel(io_model);
		return "viewOffer";
	}

	/**
	 * Anlage eines Tags zu einem Angebot
	 * 
	 * @param io_model aktuelles Model
	 * @param tag      anzulegender Tag
	 * @return Zielseite
	 */
	@RequestMapping(value = "/viewOffer", method = RequestMethod.POST)
	private ModelAndView addTag(Model io_model, @RequestParam("in_tag") String tag) {
		ModelAndView lo_mav = new ModelAndView();

		CommonService.prepModel(lo_mav, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		Tag newTag = new Tag();
		Angebot currentOffer = angebotService.findOfferById(offerId);

		if (tag.equals("")) {
			lo_mav.addObject("emptytag", true);
			prepOfferModel(lo_mav);
			lo_mav.setViewName("viewOffer");
			return lo_mav;
		}

		if (currentOffer == null) {
			// Tritt ein, wenn ein in der Zwischenzeit das betroffene Angebot geloescht
			// wurde
			lo_mav.addObject("noOfferFound", true);
			lo_mav.setViewName("dashboard");
			return lo_mav;

		} else {
			prepOfferModel(lo_mav);
			newTag.setAngebot(currentOffer);
			newTag.setTag(tag);

			for (Tag tag1 : angebotService.findOfferById(offerId).getTags()) {
				if (tag1.getTag().equals(tag)) {
					lo_mav.addObject("exist", true);
					lo_mav.addObject("tags", tagService.getTagsForAngebot(offerId));
					lo_mav.setViewName("viewOffer");
					return lo_mav;
				}
			}

			if (tagService.getTagsForAngebot(offerId).size() >= 10) {
				lo_mav.addObject("toomanytags", true);
			} else {
				tagService.createTag(newTag);
			}

			prepOfferModel(lo_mav);
			lo_mav.setViewName("viewOffer");
			return lo_mav;
		}
	}

	/**
	 * Anlage eines Produktbildes
	 * 
	 * @param file  Produktbild
	 * @param model aktuelles Model
	 * @return Zielseite
	 * @throws IOException Fehler
	 */
	@RequestMapping(value = "viewOffer", params = "Hochladen", method = RequestMethod.POST)
	public String uploadProductImage(@RequestParam("image") MultipartFile file, Model model) throws IOException {
		CommonService.prepModel(model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		Angebot lo_currentOffer = angebotService.findOfferById(offerId);

		if (lo_currentOffer == null) {
			// Tritt ein, wenn das Angebot zwischenzeitlich geloescht wurde
			model.addAttribute("noOfferFound", true);
			return "dashboard";

		} else {

			try (InputStream input = file.getInputStream()) {
				try {
					ImageIO.read(input).toString();
					// It's an image (only BMP, GIF, JPG and PNG are recognized).
				} catch (Exception e) {
					// It's not an image.
					model.addAttribute("noimage", true);
					prepOfferModel(model);
					return "viewOffer";
				}
			}

			BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

			if (bufferedImage.getHeight() > 800 || bufferedImage.getWidth() > 800) {
				model.addAttribute("tobig", true);
			} else if (dateiService.findProductimagesbyOfferId(offerId).size() >= 4) {
				model.addAttribute("maxcount", true);
			} else {

				byte[] fileContent = file.getBytes();
				String encodedString = Base64.getEncoder().encodeToString(fileContent);

				Datei datei = new Datei();
				datei.setAngebot(lo_currentOffer);
				datei.setBase64("data:image/jpg;base64," + encodedString);
				datei.setName(file.getOriginalFilename());
				dateiService.saveDatei(datei);
			}

			prepOfferModel(model);

			return "viewOffer";
		}
	}

	/**
	 * Anlegen einer Qualifikation
	 * 
	 * @param file  Qualifikation
	 * @param model aktuelles Model
	 * @return Zielfunktion
	 */
	@RequestMapping(value = "viewOffer", params = "Qualifikation", method = RequestMethod.POST)
	public String uploadProductQualification(@RequestParam("quali") MultipartFile file, Model model) {

		CommonService.prepModel(model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		Angebot lo_currentOffer = angebotService.findOfferById(offerId);
		if (lo_currentOffer == null) {
			// Tritt ein, wenn das Angebot zwischenzeitlich geloescht wurde
			model.addAttribute("noOfferFound", true);
			return "dashboard";

		} else {

			if (dateiService.findQualifikationenByOfferId(offerId).size() >= 2) {
				model.addAttribute("toomanyqualis", true);
			} else if (file.isEmpty()) {
				model.addAttribute("emptypdf", true);
			} else if (!file.getContentType().contains(pdfType)) {
				model.addAttribute("nopdf", true);
			} else {

				byte[] fileContent = new byte[0];
				try {
					fileContent = file.getBytes();
				} catch (IOException e) {
					e.printStackTrace();
				}

				String encodedString = Base64.getEncoder().encodeToString(fileContent);
				Datei datei = new Datei();
				datei.setAngebot(lo_currentOffer);
				datei.setBase64("data:application/pdf;base64," + encodedString);
				datei.setPDF(true);
				datei.setName(file.getOriginalFilename());
				dateiService.saveDatei(datei);
			}
			prepOfferModel(model);
			return "viewOffer";
		}
	}

	/**
	 * Angebot bewerten
	 * 
	 * @param model  aktuelles Model
	 * @param rating Rating
	 * @return Zielseite
	 */
	@RequestMapping(value = "/viewOffer", params = "rating", method = RequestMethod.POST)
	public String rateOffer(Model model, @RequestParam("rating") String rating) {
		Angebot angebot = angebotService.findOfferById(offerId);

		if (Character.getNumericValue(rating.charAt(0)) == 27) {
			model.addAttribute("emptyrating", true);
		} else {
			angebot.setRating(Character.getNumericValue(rating.charAt(0)));
			angebotService.updateRating(angebot);
		}

		prepOfferModel(model);

		return "viewOffer";
	}

	/**
	 * Löschen eines Tags
	 * 
	 * @param model aktuelles Model
	 * @param tagId eindeutige Identifikationsnummer
	 * @return Zielseite
	 */
	@RequestMapping(value = "/viewOffer", params = "deltag", method = RequestMethod.POST)
	public String deleteTag(Model model, @RequestParam("tagid") int tagId) {

		tagService.deleteTagbyID(tagId);

		prepOfferModel(model);
		return "viewOffer";
	}

	/**
	 * Sender der Kaufanfrage
	 * 
	 * @param model     aktuelles Model
	 * @param nachricht Nachrichteninhalt
	 * @param password  Passwort des aktuellen Nutzers (Interessenten)
	 * @return Zielseite
	 */
	@RequestMapping(value = "/viewOffer", params = "passwortbuy", method = RequestMethod.POST)
	public String sendBuyRequest(Model model, @RequestParam("nachricht") String nachricht,
			@RequestParam("b_passwort") String password) {

		CommonService.prepModel(model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		Angebot angebot = angebotService.findOfferById(offerId);

		if (angebot == null) {
			// Tritt ein, wenn das Angebot zwischenzeitlich geloescht wurde
			model.addAttribute("noOfferFound", true);
			return "dashboard";

		} else {

			BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
			if (nachricht.trim().isEmpty() || password.trim().isEmpty()) {
				model.addAttribute("emptybuy", true);
			} else if (!bCryptPasswordEncoder.matches(password, nutzerService.getCurrentUser().getPassword())) {
				model.addAttribute("nomatchbuy", true);
			} else {
				Message message = new Message();
				Nutzer anbieter = nutzerService.findUserById(angebot.getAnbieter().getEmail());
				Nutzer anfrager = nutzerService.getCurrentUser();
				message.setSenderName(anfrager.getVorname() + " " + anfrager.getNachname());
				message.setReceiverName(anbieter.getVorname() + " " + anbieter.getNachname());
				message.setMessageTo(anbieter.getEmail());
				message.setMessageFrom(anfrager.getEmail());
				message.setNachricht(nachricht);
				message.setBuyRequest(true);
				message.setOfferId(angebot);
				message.setTitle(anfrager.getVorname() + " " + anfrager.getNachname() + " möchte Ihr Angebot "
						+ angebot.getTitel() + " kaufen!");
				if (notificationService.sendBuyRequestMessage(nutzerService.getCurrentUser(), angebot)) {
					model.addAttribute("buysent", true);
					messageService.createMessage(message);
				} else {
					model.addAttribute("buysentfailed", true);
				}
			}
			prepOfferModel(model);
			return "viewOffer";
		}
	}

	/**
	 * Änderung eines Angebots
	 * 
	 * @param model          aktuelles Model
	 * @param u_titel        geänderter Titel
	 * @param u_betrag       geänderter Betrag
	 * @param u_beschreibung geänderte Beschreibung
	 * @param u_kategorie    geänderte Kategorie
	 * @param u_plz          geänderte Postleitzahl
	 * @param u_stadt        geänderte Stadt
	 * @return Zielseite
	 */
	@RequestMapping(value = "/viewOffer", params = "change", method = RequestMethod.POST)
	public String changeOffer(Model model, @RequestParam("u_titel") String u_titel,
			@RequestParam("u_betrag") String u_betrag, @RequestParam("u_beschreibung") String u_beschreibung,
			@RequestParam("u_kategorie") int u_kategorie, @RequestParam("u_plz") String u_plz,
			@RequestParam("u_stadt") String u_stadt) {

		if (u_titel.trim().isEmpty() || u_beschreibung.trim().isEmpty() || u_kategorie == 0 || u_betrag.trim().isEmpty()
				|| u_plz.trim().isEmpty() || u_stadt.trim().isEmpty()) {
			model.addAttribute("emptychange", true);

		} else if (!nutzerValidation.isPostalcodeValid(u_plz)) {
			model.addAttribute("invalidPLZ", true);

		} else {
			Angebot angebot = angebotService.findOfferById(offerId);
			angebot.setTitel(u_titel);
			angebot.setBeschreibung(u_beschreibung);
			angebot.setBetrag(Double.parseDouble(u_betrag));
			angebot.setAngebotsKategorieId(angebotskategorieService.findOfferCategorieById(u_kategorie));
			angebot.setPlz(u_plz);
			angebot.setStadt(u_stadt);
			angebotService.changeOffer(angebot);
		}

		prepOfferModel(model);
		return "viewOffer";
	}

	/**
	 * Vorbereiten des aktuellen Models
	 * 
	 * @param io_model aktuelles Model
	 */
	private void prepOfferModel(Model io_model) {
		CommonService.prepModel(io_model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		io_model.addAttribute("allOfferTypes", angebotskategorieService.findAllOfferCategories());
		io_model.addAttribute("qualifikationen", dateiService.findQualifikationenByOfferId(offerId));
		io_model.addAttribute("images", dateiService.findProductimagesbyOfferId(offerId));
		io_model.addAttribute("angebot", angebotService.findOfferById(offerId));
		io_model.addAttribute("nutzer", nutzerService.findUserById(nutzerService.getCurrentUserId()));
		io_model.addAttribute("tags", tagService.getTagsForAngebot(offerId));
	}

	/**
	 * Vorbereiten des aktuellen Models
	 * 
	 * @param io_mav aktuelles Model
	 */
	private void prepOfferModel(ModelAndView io_mav) {
		CommonService.prepModel(io_mav, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		io_mav.addObject("allOfferTypes", angebotskategorieService.findAllOfferCategories());
		io_mav.addObject("qualifikationen", dateiService.findQualifikationenByOfferId(offerId));
		io_mav.addObject("images", dateiService.findProductimagesbyOfferId(offerId));
		io_mav.addObject("angebot", angebotService.findOfferById(offerId));
		io_mav.addObject("nutzer", nutzerService.findUserById(nutzerService.getCurrentUserId()));
		io_mav.addObject("tags", tagService.getTagsForAngebot(offerId));
	}
}