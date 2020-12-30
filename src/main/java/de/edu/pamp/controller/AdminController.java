/**
 *
 */
package de.edu.pamp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.edu.pamp.dto.Angebot;
import de.edu.pamp.dto.Angebotskategorie;
import de.edu.pamp.geolocation.GoogleModel;
import de.edu.pamp.services.AngebotService;
import de.edu.pamp.services.AngebotskategorieService;
import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;

@Controller
/**
 * @author Tobias
 *
 *         Dieser Controller regelt die Funktionen eines Admins
 *
 */
public class AdminController {
	@Autowired
	private AngebotskategorieService mo_angebotskategorieService;
	@Autowired
	private MessageService mo_messageService;
	@Autowired
	private NutzerService mo_nutzerService;
	@Autowired
	private AngebotService mo_angebotService;

	/**
	 * Zeigt alle Angebote an
	 *
	 * @return Zielseite
	 */
	@GetMapping("/displayAllOffers")
	public ModelAndView displayAllOffers() {
		ModelAndView lo_mav = new ModelAndView();
		String lv_currentUserId = mo_nutzerService.getCurrentUserId();
		List<Angebot> lt_offers;

		CommonService.prepModel(lo_mav, mo_messageService.getCountUnreadMessages(lv_currentUserId),
				mo_nutzerService.isUserLocked(lv_currentUserId), mo_nutzerService.isUserAdmin(lv_currentUserId));

		if (mo_nutzerService.isUserAdmin(lv_currentUserId)) {
			lt_offers = mo_angebotService.findAll();

			GoogleModel lo_googleModel = new GoogleModel();
			lo_googleModel.setApiKey(lo_mav);
			lo_googleModel.showOffersOnMap(lo_mav, lt_offers);
			lo_googleModel.centerGoogleMap(lo_mav, mo_nutzerService.getCurrentUser(), 7);
		} else {
			lt_offers = new ArrayList<Angebot>();
		}

		lo_mav.addObject("allOffers", lt_offers);

		lo_mav.setViewName("displayAllOffers");
		return lo_mav;
	}

	/**
	 * Zeigt alle Angebotskategorien an
	 *
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/displayOfferCategories")
	public ModelAndView displayOfferCategories(Model io_model) {
		ModelAndView lo_mav = new ModelAndView();
		List<Angebotskategorie> lt_offerCategories = mo_angebotskategorieService.findAllOfferCategories();
		String lv_currentUserId = mo_nutzerService.getCurrentUserId();

		CommonService.prepModel(lo_mav, mo_messageService.getCountUnreadMessages(lv_currentUserId),
				mo_nutzerService.isUserLocked(lv_currentUserId), mo_nutzerService.isUserAdmin(lv_currentUserId));

		lo_mav.addObject("offerCategories", lt_offerCategories);
		lo_mav.addObject("offerCategory", new Angebotskategorie());
		lo_mav.setViewName("offerCategories");
		return lo_mav;
	}

	/**
	 * Fügt eine neue Angebotskategorie hinzu
	 *
	 * @param io_offerCategory Angebotskategorie die hinzugefügt werden soll
	 * @param io_bindingResult Systemseitige Validierung der Eingaben
	 * @return Zielseite
	 */
	@PostMapping("/createOfferCategory")
	public ModelAndView createOfferCategorie(@Valid Angebotskategorie io_offerCategory,
			BindingResult io_bindingResult) {
		ModelAndView lo_mav = new ModelAndView();
		String lv_currentUserId = mo_nutzerService.getCurrentUserId();

		CommonService.prepModel(lo_mav, mo_messageService.getCountUnreadMessages(lv_currentUserId),
				mo_nutzerService.isUserLocked(lv_currentUserId), mo_nutzerService.isUserAdmin(lv_currentUserId));

		if (mo_nutzerService.isUserAdmin(lv_currentUserId)) {
			if (!io_bindingResult.hasErrors()) {
				mo_angebotskategorieService.createOfferCategory(io_offerCategory);
			} else {
				lo_mav.addObject("noText", true);
			}
		}

		lo_mav.addObject("offerCategories", mo_angebotskategorieService.findAllOfferCategories());
		lo_mav.addObject("offerCategory", new Angebotskategorie());
		lo_mav.setViewName("offerCategories");

		return lo_mav;
	}

	/**
	 * Löscht eine Angebotskategorie
	 *
	 * @param iv_offerCategoryId Angebotskategorie, die gelöscht werden soll
	 * @return Zielseite
	 */
	@PostMapping("/deleteOfferCategory")
	public ModelAndView deleteOfferCategory(@RequestParam("offerCategoryId") String iv_offerCategoryId) {
		ModelAndView lo_mav = new ModelAndView();
		String lv_currentUserId = mo_nutzerService.getCurrentUserId();

		CommonService.prepModel(lo_mav, mo_messageService.getCountUnreadMessages(lv_currentUserId),
				mo_nutzerService.isUserLocked(lv_currentUserId), mo_nutzerService.isUserAdmin(lv_currentUserId));

		if (mo_nutzerService.isUserAdmin(lv_currentUserId)) {
			if (iv_offerCategoryId.isEmpty()) {
				lo_mav.addObject("emptyOfferCatId", true);
			} else {
				try {
					boolean isUsed = false;
					for (Angebot angebot : mo_angebotService.findAll()) {
						if (angebot.getAngebotsKategorieId().getAngebotsKategorieId() == Integer
								.parseInt(iv_offerCategoryId)) {
							isUsed = true;
						}
					}
					if (!isUsed) {
						mo_angebotskategorieService.deleteOfferCategory(Integer.parseInt(iv_offerCategoryId));
					} else {
						lo_mav.addObject("isUsed", true);
					}
				} catch (NumberFormatException lo_exception) {
				}
			}
		}

		lo_mav.addObject("offerCategories", mo_angebotskategorieService.findAllOfferCategories());
		lo_mav.addObject("offerCategory", new Angebotskategorie());
		lo_mav.setViewName("offerCategories");

		return lo_mav;
	}
}