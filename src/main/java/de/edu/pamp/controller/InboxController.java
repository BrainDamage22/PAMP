package de.edu.pamp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;

/**
 * Controller für die Verarbeitung des Posteingangs
 *
 * @author Lukas
 */
@Controller
public class InboxController {

	@Autowired
	private MessageService messageService;
	@Autowired
	private NutzerService nutzerService;

	/**
	 * Initialisierung des Posteingangs
	 *
	 * @param io_model aktuelles Model
	 * @return verändertes Model (Zielseite)
	 */
	@GetMapping("/inbox")
	public ModelAndView getInbox(Model io_model) {
		ModelAndView lo_mav = new ModelAndView();

		CommonService.prepModel(lo_mav, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		lo_mav.addObject("readMessages", messageService.findReadMessagesByUserId(nutzerService.getCurrentUserId()));
		lo_mav.addObject("unreadMessages", messageService.findUnreadMessagesByUserId(nutzerService.getCurrentUserId()));
		lo_mav.setViewName("inbox");

		return lo_mav;
	}
}