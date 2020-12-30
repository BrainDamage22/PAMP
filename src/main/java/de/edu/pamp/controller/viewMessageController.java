package de.edu.pamp.controller;

import de.edu.pamp.mail.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.edu.pamp.dto.Angebot;
import de.edu.pamp.dto.Message;
import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.services.AngebotService;
import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;

/**
 * 
 * @author Tobias
 *
 *         Services rund um die Behandlung der Nachrichtenanzeige
 */
@Controller
public class viewMessageController {

	@Autowired
	private MessageService messageService;
	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private AngebotService angebotService;
	@Autowired
	private NotificationService notificationService;

	private Message message;

	private int offerID;

	/**
	 * Anzeige einer Nachricht
	 * 
	 * @param model        aktuelles Model
	 * @param iv_messageId eindeutige Identifikationsnummer
	 * @return Zielseite
	 */
	@GetMapping("/viewMessage")
	public ModelAndView viewMessage(Model model, @RequestParam("messageId") int iv_messageId) {
		Nutzer lo_currentUser = nutzerService.getCurrentUser();
		Nutzer lo_lockedBy = lo_currentUser.getLockedBy();
		Message lo_message = messageService.findMessageById(iv_messageId);
		this.message = lo_message;

		if (lo_message.getOfferId() != null) {
			this.offerID = lo_message.getOfferId().getAngebotId();
		}

		ModelAndView lo_mav = new ModelAndView();

		lo_mav.setViewName("viewMessage");

		if (lo_currentUser.getMessagesReceived().contains(lo_message)) {
			lo_mav.addObject("message", lo_message);
			lo_mav.addObject("sender", nutzerService.getName(lo_message.getMessageFrom()));
			lo_message.setRead(true);
			messageService.updateMessage(lo_message);
			lo_mav.addObject("currentUser", lo_currentUser);
			lo_mav.addObject("userLockedBy", getEmail(lo_lockedBy));
			lo_mav.addObject("ownRequest", false);
		} else if (lo_currentUser.getMessagesSent().contains(lo_message)) {
			lo_mav.addObject("message", lo_message);
			lo_mav.addObject("sender", lo_message.getMessageFrom());
			lo_mav.addObject("currentUser", lo_currentUser);
			lo_mav.addObject("userLockedBy", getEmail(lo_lockedBy));
			lo_mav.addObject("ownRequest", isOwnRequest(lo_message));
		} else {
			lo_mav.addObject("message", new Message());

		}

		CommonService.prepModel(lo_mav, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		return lo_mav;
	}

	/**
	 * Liefert die E-Mail Adresse desjenigen Nutzers zurueck, welcher die Sperre
	 * durchgefuehrt hat
	 * 
	 * @param io_user Nutzer
	 * @return E-Mail Adresse oder "" falls keine Sperrung vorliegt
	 */
	private String getEmail(Nutzer io_user) {
		String rv_email = "";

		if (io_user != null) {
			rv_email = io_user.getEmail();
		}

		return rv_email;
	}

	/**
	 * Prüft, ob es sich bei der Kaufanfrage um die eigene handelt. Nötig für die
	 * UI-Steuerung (Kaufanfrage bestätigen)
	 * 
	 * @param io_message Nachricht
	 * @return TRUE, falls es eigene Nachricht ist. FALSE, falls es eine fremde
	 *         Nachricht ist
	 */
	private boolean isOwnRequest(Message io_message) {
		if (io_message.getMessageFrom().equals(nutzerService.getCurrentUserId())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Kaufanfrage akzeptieren
	 * 
	 * @param model    aktuelles Model
	 * @param password Passwort des Inserenten
	 * @return Zielseite
	 */
	@RequestMapping(value = "/viewMessage", params = "accept", method = RequestMethod.POST)
	public String acceptBuy(Model model, @RequestParam("password") String password) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (password.equals("")) {
			model.addAttribute("emptypassword", true);
			model.addAttribute("message", message);
			CommonService.prepModel(model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
					nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
					nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));
			return "viewMessage";
		}

		if (!encoder.matches(password, nutzerService.getCurrentUser().getPassword())) {
			model.addAttribute("nomatch", true);
			model.addAttribute("message", message);
			CommonService.prepModel(model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
					nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
					nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

			return "viewMessage";
		}

		Angebot angebot = angebotService.findOfferById(offerID);
		Nutzer kaufer = nutzerService.findUserById(message.getMessageFrom());

		angebot.setKaeufer(kaufer);
		angebotService.updateOfferToSold(angebot);
		message.setBuyRequest(false);
		messageService.updateMessage(message);

		Message accept = new Message();
		accept.setSenderName("System");
		accept.setReceiverName(message.getSenderName());
		accept.setMessageFrom(message.getMessageTo());
		accept.setMessageTo(message.getMessageFrom());
		accept.setTitle("Kaufanfrage wurde bestätigt! - '" + angebot.getTitel() + "'");
		accept.setNachricht("Bitte antworten Sie dem Verkäufer auf diese Nachricht um die Kaufabwicklung zu klären.");

		if (notificationService.sendBuyAcceptDeniedMessage(kaufer, nutzerService.getCurrentUser())) {
			messageService.createMessage(accept);
		} else {
			CommonService.prepModel(model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
					nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
					nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));
			model.addAttribute("message", message);
			model.addAttribute("fail", true);
			return "viewMessage";
		}

		model.addAttribute("accepted", true);
		model.addAttribute("nutzer", nutzerService.getCurrentUser());
		CommonService.prepModel(model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));
		return "dashboard";
	}

	/**
	 * Kaufanfrage ablehnen
	 * 
	 * @param model aktuelles Model
	 * @return Zielseite
	 */
	@RequestMapping(value = "/viewMessage", params = "dismiss", method = RequestMethod.POST)
	public String dismissBuy(Model model) {
		Angebot angebot = angebotService.findOfferById(offerID);

		Nutzer kaufer = nutzerService.findUserById(message.getMessageFrom());

		message.setBuyRequest(false);
		messageService.updateMessage(message);
		Message deny = new Message();
		deny.setSenderName("System");
		deny.setReceiverName(message.getSenderName());
		deny.setMessageFrom(message.getMessageTo());
		deny.setMessageTo(message.getMessageFrom());
		deny.setTitle("Kaufanfrage wurde abgelehnt! - '" + angebot.getTitel() + "'");
		deny.setNachricht("Leider hat der Verkäufer ihre Kaufanfrage abgelehnt.");

		if (notificationService.sendBuyAcceptDeniedMessage(kaufer, nutzerService.getCurrentUser())) {
			messageService.createMessage(deny);
		} else {
			CommonService.prepModel(model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
					nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
					nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));
			model.addAttribute("message", message);
			model.addAttribute("fail", true);
			return "viewMessage";
		}

		model.addAttribute("denied", true);
		model.addAttribute("nutzer", nutzerService.getCurrentUser());
		CommonService.prepModel(model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));
		return "dashboard";
	}
}