package de.edu.pamp.controller;

import de.edu.pamp.services.AngebotskategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;

/**
 * @author Tobias
 *         <p>
 *         Controller für die Verarbeitung des Dashboards
 */
@Controller
public class DashboardController {

	@Autowired
	private MessageService messageService;
	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private AngebotskategorieService angebotskategorieService;

	/**
	 * Initialisierung des Dashboards
	 *
	 * @param io_model aktuelles Model
	 * @return veraendertes Model (Zielseite)
	 */
	@GetMapping("/dashboard")
	public ModelAndView getDashboard(Model io_model) {
		ModelAndView lo_mav = new ModelAndView();

		CommonService.prepModel(lo_mav, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		prepModel(lo_mav);

		lo_mav.addObject("searchOfferCategories", angebotskategorieService.findAllOfferCategories());
		io_model.addAttribute("nutzer", nutzerService.getCurrentUser());
		lo_mav.setViewName("dashboard");
		return lo_mav;
	}

	private void prepModel(ModelAndView io_mav) {
		io_mav.addObject("protocol", true);
	}
}