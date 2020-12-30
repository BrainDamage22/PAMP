package de.edu.pamp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.edu.pamp.dto.Angebot;
import de.edu.pamp.dto.Message;
import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.mail.NotificationService;
import de.edu.pamp.services.AngebotService;
import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.DateiService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;
import de.edu.pamp.services.TagService;
import de.edu.pamp.validation.NutzerValidation;

/**
 * Controller zum Löschen von Angeboten durch Nutzer und Admins
 *
 * @author Lukas
 */
@Controller
public class OfferDeleteController {
	@Autowired
	private AngebotService angebotService;
	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private NutzerValidation nutzerValidation;
	@Autowired
	private NotificationService benachrichtigungsService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private DateiService dateiService;
	@Autowired
	private TagService tagService;

	/**
	 * Löschen eines Angebots durch den Ersteller
	 *
	 * @param io_model   Aktuelles Model
	 * @param password   eingegebenes Passwort
	 * @param iv_offerId ID des zu löschenden Angebots
	 * @return Zielseite
	 */
	@PostMapping("/deleteOffer")
	public ModelAndView deleteSingleOffer(Model io_model, @RequestParam("u_passwort") String password,
			@RequestParam("offerId") int iv_offerId) {

		ModelAndView lo_mav = new ModelAndView();
		Angebot lo_offer = angebotService.findOfferById(iv_offerId);
		boolean lf_error = false;

		CommonService.prepModel(lo_mav, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		if (!new BCryptPasswordEncoder().matches(password, nutzerService.getCurrentUser().getPassword())
				|| nutzerValidation.isPasswordEmpty(password)) {
			lf_error = true;
			lo_mav.addObject("passworderror", true);
		}

		if (lo_offer.getKaeufer() != null) {
			lf_error = true;
			lo_mav.addObject("isSold", true);
		}

		if (!lo_offer.getAnbieter().getEmail().equals(nutzerService.getCurrentUserId())) {
			lf_error = true;
			lo_mav.addObject("noAuthority", true);
		}

		if (lf_error) {
			lo_mav.addObject("angebot", angebotService.findOfferById(iv_offerId));
			lo_mav.addObject("nutzer", nutzerService.findUserById(nutzerService.getCurrentUserId()));
			lo_mav.addObject("tags", tagService.getTagsForAngebot(iv_offerId));
			lo_mav.addObject("qualifikationen", dateiService.findQualifikationenByOfferId(iv_offerId));
			lo_mav.addObject("offerId", iv_offerId);
			lo_mav.setViewName("viewOffer");
		} else {
			angebotService.deleteOfferbyId(iv_offerId);
			lo_mav.setViewName("redirect:dashboard");
		}

		return lo_mav;
	}

	/**
	 * Löschen eines Angebots durch einen Administrator
	 *
	 * @param iv_reason  Grund für die Löschung
	 * @param iv_offerId ID des zu löschenden Angebots
	 * @return Zielseite
	 */
	@PostMapping("/deleteOfferAdmin")
	private ModelAndView deleteOfferByAdmin(@RequestParam("u_reason") String iv_reason,
			@RequestParam("offerId") int iv_offerId) {
		ModelAndView lo_mav = new ModelAndView();
		Angebot lo_offer = angebotService.findOfferById(iv_offerId);
		Nutzer lo_currentUser = nutzerService.getCurrentUser();

		CommonService.prepModel(lo_mav, messageService.getCountUnreadMessages(lo_currentUser.getEmail()),
				nutzerService.isUserLocked(lo_currentUser.getEmail()),
				nutzerService.isUserAdmin(lo_currentUser.getEmail()));

		if (iv_reason.trim().isEmpty()) {
			lo_mav.addObject("noReason", true);

			lo_mav.addObject("angebot", lo_offer);
			lo_mav.addObject("nutzer", lo_currentUser);
			lo_mav.addObject("tags", tagService.getTagsForAngebot(iv_offerId));
			lo_mav.addObject("offerId", iv_offerId);
			lo_mav.addObject("qualifikationen", dateiService.findQualifikationenByOfferId(iv_offerId));
			lo_mav.setViewName("viewOffer");
		} else {
			Message lo_message = new Message();

			lo_message.setTitle("Angebot '" + lo_offer.getTitel() + "' entfernt");
			lo_message.setMessageFrom(lo_currentUser.getEmail());
			lo_message.setMessageTo(lo_offer.getAnbieter().getEmail());
			lo_message.setNachricht(iv_reason);
			lo_message.setRead(false);
			lo_message.setSenderName(lo_currentUser.getVorname() + " " + lo_currentUser.getNachname());
			lo_message
					.setReceiverName(lo_offer.getAnbieter().getVorname() + " " + lo_offer.getAnbieter().getNachname());

			try {
				benachrichtigungsService.sendMessageOfferDeletedByAdmin(lo_message);
				messageService.createMessage(lo_message);
				angebotService.deleteOfferbyId(iv_offerId);

				lo_mav.setViewName("redirect:dashboard");

			} catch (MailException e) {
				lo_mav.addObject("MessageFailed", e.getMessage());
				lo_mav.addObject("sendMessageFailed", true);

				lo_mav.addObject("angebot", lo_offer);
				lo_mav.addObject("nutzer", lo_currentUser);
				lo_mav.addObject("tags", tagService.getTagsForAngebot(iv_offerId));
				lo_mav.addObject("offerId", iv_offerId);
				lo_mav.addObject("qualifikationen", dateiService.findQualifikationenByOfferId(iv_offerId));

				lo_mav.setViewName("viewOffer");
			}

		}

		return lo_mav;
	}
}