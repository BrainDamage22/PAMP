package de.edu.pamp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
 *         Services rund um die Behandlung der Angebotsanzeige beim
 *         Kaufabschluss
 */
@Controller
public class ViewOfferToBuyController {
	@Autowired
	private AngebotService angebotService;
	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private MessageService messageService;

	private Angebot mo_offer;

	/**
	 * Anzeige der Angebotsvorschau für den Kaufabschluss
	 * 
	 * @param io_model   aktuelles Model
	 * @param iv_offerId anzuzeigendes Angebot
	 * @return Zielseite
	 */
	@GetMapping("/displayOfferToBuy")
	public ModelAndView showOfferToBuy(Model io_model, @RequestParam("offerId") int iv_offerId) {
		Nutzer lo_user = nutzerService.getCurrentUser();
		mo_offer = angebotService.findOfferById(iv_offerId);

		ModelAndView modelAndView = new ModelAndView("displayOfferToBuy");

		CommonService.prepModel(modelAndView, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		if (mo_offer.getAnbieter().getEmail().equals(lo_user.getEmail())) {
			// Kauf des eigenen Angebots ist nicht erlaubt
			modelAndView.addObject("angebot", new Angebot());
			modelAndView.addObject("nutzer", new Nutzer());
			modelAndView.addObject("sold", false);
			modelAndView.addObject("authorized", true);
			modelAndView.addObject("selfOffer", true);

		} else if (mo_offer.getKaeufer() != null) {
			modelAndView.addObject("sold", true);
			modelAndView.addObject("selfOffer", false);

			if (mo_offer.getKaeufer().getEmail().equals(lo_user.getEmail())) {
				// Artikel bereits gekauft
				modelAndView.addObject("authorized", true);
				modelAndView.addObject("angebot", mo_offer);
				modelAndView.addObject("sold", true);

			} else {
				// Artikel von anderem Benutzer gekauft - nicht berechtigt!
				modelAndView.addObject("angebot", new Angebot());
				modelAndView.addObject("authorized", false);
			}
		} else {
			// unverkaufter Artikel
			modelAndView.addObject("sold", false);
			modelAndView.addObject("selfOffer", false);
			modelAndView.addObject("authorized", true);
			modelAndView.addObject("angebot", mo_offer);
			modelAndView.addObject("nutzer", lo_user);
		}

		return modelAndView;
	}

	/**
	 * Behandlung der Kaufabwicklung
	 * 
	 * @param io_nutzer aktueller Nutzer (Käufer)
	 * @param io_model  aktuelles Model
	 * @return Zielseite
	 */
	@RequestMapping("/displayOfferToBuy")
	public ModelAndView updateOffer(Nutzer io_nutzer, Model io_model) {
		ModelAndView modelAndView = new ModelAndView();
		Nutzer lo_currentUser = nutzerService.getCurrentUser();

		CommonService.prepModel(modelAndView, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		if (new BCryptPasswordEncoder().matches(io_nutzer.getPassword(), lo_currentUser.getPassword())) {
			// gueltiges Passwort - Kauf abschliessen
			mo_offer.setKaeufer(lo_currentUser);
			angebotService.updateOfferToSold(mo_offer);

			Message lo_message = new Message();
			lo_message.setTitle("Kaufabwicklung - " + mo_offer.getTitel());

			modelAndView.addObject("message", lo_message);
			modelAndView.addObject("receiverId", mo_offer.getAnbieter().getEmail());
			modelAndView.setViewName("redirect:sendMessage");
		} else {
			// ungueltiges Passwort
			modelAndView.addObject("angebot", mo_offer);
			modelAndView.addObject("nutzer", io_nutzer);
			modelAndView.addObject("selfOffer", false);
			modelAndView.addObject("sold", false);
			modelAndView.addObject("authorized", true);
			modelAndView.addObject("nomatch", true);
		}

		return modelAndView;
	}
}