package de.edu.pamp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import de.edu.pamp.services.AngebotService;
import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;

/**
 * 
 * @author Tobias
 *
 *         Services rund um die Listdarstellung von Angeboten eines Nutzers
 */
@Controller
public class UserOfferListController {

	@Autowired
	private AngebotService angebotService;
	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private MessageService messageService;

	/**
	 * Ausgabe aller Angebote des aktuellen Benutzers
	 *
	 * @param io_model aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/displayUserOfferList")
	public String showUserOfferList(Model io_model) {
		io_model.addAttribute("offers", angebotService.getUserOffers(nutzerService.getCurrentUser()));

		CommonService.prepModel(io_model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		return "displayUserOfferList";
	}
}