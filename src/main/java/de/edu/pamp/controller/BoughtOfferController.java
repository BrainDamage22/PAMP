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
 * @author Lukas Dieser Controller zeigt dem User alle gekauften Angebote
 */
@Controller
public class BoughtOfferController {

	@Autowired
	private MessageService messageService;
	@Autowired
	private NutzerService nutzerService;

	/**
	 * Ausgabe aller gekauften Angebote eines Users
	 *
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/boughtOffers")
	public ModelAndView showAllOffers(Model io_model) {
		ModelAndView lo_mav = new ModelAndView();

		CommonService.prepModel(lo_mav, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		lo_mav.addObject("offers", nutzerService.getCurrentUser().getOfferBought());
		lo_mav.setViewName("boughtOffers");

		return lo_mav;
	}
}