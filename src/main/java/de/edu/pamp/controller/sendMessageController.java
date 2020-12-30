package de.edu.pamp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.edu.pamp.dto.Message;
import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.mail.NotificationService;
import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;

/**
 * 
 * @author Tobias
 * 
 *         Services rund um den Nachrichtenversand
 */
@Controller
public class sendMessageController {

	@Autowired
	private MessageService messageService;
	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private NotificationService notificationService;
	private String messageTo;

	/**
	 * Anzeige einer leeren Nachricht
	 * 
	 * @param model      aktuelles Model
	 * @param receiverId eindeutige Identifikationsnummer des Empfängers
	 * @return Zielseite
	 */
	@GetMapping("/sendMessage")
	public String retMessage(Model model, @RequestParam("receiverId") String receiverId) {
		Nutzer lo_currentUser = nutzerService.getCurrentUser();
		Nutzer lo_receiverUser = nutzerService.findUserById(receiverId);
		messageTo = receiverId;

		model.addAttribute("message", new Message());
		model.addAttribute("receiver", lo_receiverUser);

		CommonService.prepModel(model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		if (lo_receiverUser == null) {
			model.addAttribute("receiverNotExists", true);

		} else if (nutzerService.isUserLocked(lo_currentUser.getEmail())) {
			if (lo_receiverUser.getEmail().equals(lo_currentUser.getLockedBy().getEmail())) {
				model.addAttribute("replyAdminLock", true);
			} else {
				model.addAttribute("replyAdminLock", false);
			}
		}
		return "sendMessage";
	}

	/**
	 * Senden einer Nachricht
	 * 
	 * @param io_message       Nachricht
	 * @param io_bindingResult systemseitige Validierungen
	 * @param io_model         aktuelles Model
	 * @return Zielseite
	 */
	@PostMapping("/sendMessage")
	public String sendMessage(@Valid Message io_message, BindingResult io_bindingResult, Model io_model) {
		CommonService.prepModel(io_model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		Nutzer lo_receiver = nutzerService.findUserById(messageTo);
		boolean replyAdminLock = false;

		if (nutzerService.isUserLocked(nutzerService.getCurrentUserId())) {
			if (lo_receiver.getEmail().equals(nutzerService.getCurrentUser().getLockedBy().getEmail())) {
				replyAdminLock = true;
			} else {
				replyAdminLock = false;
			}
		}

		if (io_bindingResult.hasErrors()) {
			io_model.addAttribute("receiver", nutzerService.findUserById(messageTo));

			if (replyAdminLock) {
				io_model.addAttribute("replyAdminLock", replyAdminLock);
			}

			return "sendMessage";
		}

		io_message.setMessageFrom(nutzerService.getCurrentUserId());
		io_message.setMessageTo(messageTo);
		io_message.setRead(false);
		io_message.setSenderName(
				nutzerService.getCurrentUser().getVorname() + " " + nutzerService.getCurrentUser().getNachname());
		io_message.setReceiverName(nutzerService.getName(messageTo));

		if (notificationService.sendMessageReceivedInformation(messageTo, nutzerService.getCurrentUserId(),
				io_message)) {
			messageService.createMessage(io_message);
			CommonService.updateModel(io_model,
					messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()));
			return "dashboard";
		}

		io_model.addAttribute("receiver", nutzerService.findUserById(messageTo));
		io_model.addAttribute("replyAdminLock", replyAdminLock);
		io_model.addAttribute("fail", true);
		return "sendMessage";
	}
}