package de.edu.pamp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import de.edu.pamp.dto.Angebot;
import de.edu.pamp.services.AngebotService;
import de.edu.pamp.services.AngebotskategorieService;
import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;
import de.edu.pamp.validation.NutzerValidation;

/**
 * Controller zum Erstellen eines Angebotes
 *
 * @author Lukas
 */
@Controller
public class OfferCreateController {

	@Autowired
	private AngebotService angebotService;
	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private NutzerValidation nutzerValidation;
	@Autowired
	private MessageService messageService;
	@Autowired
	private AngebotskategorieService angeobtstypService;

	/**
	 * Aufbauen des Formulars zur Erstellung eines Angebotes
	 *
	 * @param io_model aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/createOffer")
	public String createOfferForm(Model io_model) {
		CommonService.prepModel(io_model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		io_model.addAttribute("angebot", new Angebot());
		io_model.addAttribute("allOfferCat", angeobtstypService.findAllOfferCategories());

		return "createOfferForm";
	}

	/**
	 * Post Methode zum Anlegen eines neuen Angebotes
	 *
	 * @param io_offer         Angebot, das angelegt werden soll
	 * @param io_bindingResult Systemseiteige Validierung der Eingaben
	 * @param io_model         Aktuelles Model
	 * @return Zielseite
	 */
	@PostMapping("/createOffer")
	public ModelAndView createOffer(@Valid Angebot io_offer, BindingResult io_bindingResult, Model io_model) {
		ModelAndView modelAndView = new ModelAndView();

		CommonService.prepModel(modelAndView, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		if (nutzerService.isUserLocked(nutzerService.getCurrentUserId())) {
			modelAndView.setViewName("dashboard");

		} else {

			if (io_bindingResult.hasErrors()) {
				modelAndView.addObject("allOfferCat", angeobtstypService.findAllOfferCategories());
				modelAndView.setViewName("createOfferForm");
				return modelAndView;
			}

			if (!nutzerValidation.isPostalcodeValid(io_offer.getPlz())) {
				modelAndView.addObject("allOfferCat", angeobtstypService.findAllOfferCategories());
				modelAndView.addObject("invalidPLZ", true);
				modelAndView.setViewName("createOfferForm");
				return modelAndView;
			}

			io_offer.setAnbieter(nutzerService.findUserById(nutzerService.getCurrentUserId()));
			angebotService.createOffer(io_offer);
			modelAndView.setViewName("redirect:viewOffer");
			modelAndView.addObject("offerId", io_offer.getAngebotId());
		}

		return modelAndView;
	}
}