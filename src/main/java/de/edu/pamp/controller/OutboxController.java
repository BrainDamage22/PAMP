package de.edu.pamp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;

/**
 * Controller zum Anzeigen des Postausgangs
 *
 * @author Lukas
 */
@Controller
public class OutboxController {

	@Autowired
	private MessageService messageService;
	@Autowired
	private NutzerService nutzerService;

	/**
	 * Anzeigen des Postausgangs
	 *
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/outbox")
	public String retInbox(Model io_model) {
		io_model.addAttribute("sentMessages", messageService.findSentMessages(nutzerService.getCurrentUserId()));

		CommonService.prepModel(io_model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		return "outbox";
	}
}