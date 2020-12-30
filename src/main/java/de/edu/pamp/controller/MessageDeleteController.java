package de.edu.pamp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.edu.pamp.dto.Message;
import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;

/**
 * Controller für das Löschen von Nachrichten
 * 
 * @author Tobias
 *
 */
@Controller
public class MessageDeleteController {

	@Autowired
	NutzerService nutzerService;
	@Autowired
	MessageService messageService;

	/**
	 * Löschen einer Nachricht
	 * 
	 * @param iv_messageId ID der zu löschenden Nachricht
	 * @return Zielseite
	 */
	@PostMapping("/deleteReceivedMessage")
	public ModelAndView deleteReceivedMessage(@RequestParam("messageId") int iv_messageId) {
		ModelAndView lo_mav = new ModelAndView();
		Message lo_message = messageService.findMessageById(iv_messageId);
		Nutzer lo_currentUser = nutzerService.getCurrentUser();
		boolean lf_foundInbox = false;
		boolean lf_foundOutbox = false;

		CommonService.prepModel(lo_mav, messageService.getCountUnreadMessages(lo_currentUser.getEmail()),
				nutzerService.isUserLocked(lo_currentUser.getEmail()),
				nutzerService.isUserAdmin(lo_currentUser.getEmail()));

		if (lo_currentUser.getMessagesReceived().contains(lo_message)) {
			messageService.deleteMessageFromInbox(lo_message);
			lf_foundInbox = true;
		}

		if (lo_currentUser.getMessagesSent().contains(lo_message)) {
			messageService.deleteMessageFromOutbox(lo_message);
			lf_foundOutbox = true;
		}

		if (lf_foundInbox) {
			lo_mav.setViewName("redirect:inbox");
		} else if (lf_foundOutbox) {
			lo_mav.setViewName("redirect:outbox");
		} else {
			lo_mav.addObject("message", lo_message);
			lo_mav.addObject("sender", lo_message.getMessageFrom());
			lo_mav.addObject("messageId", iv_messageId);
			lo_mav.addObject("noOwnership", true);
			lo_mav.setViewName("viewMessage");
		}

		return lo_mav;
	}
}
