package de.edu.pamp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.edu.pamp.dto.Angebot;
import de.edu.pamp.geolocation.GoogleModel;
import de.edu.pamp.services.AngebotskategorieService;
import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;
import de.edu.pamp.services.SearchService;

/**
 * 
 * @author Tobias
 *
 *         Services rund um die Suche
 */
@Controller
public class SearchController {

	@Autowired
	private MessageService messageService;
	@Autowired
	private AngebotskategorieService angebotskategorieService;
	@Autowired
	private SearchService searchService;
	@Autowired
	private NutzerService nutzerService;

	private String dummy = "keine Angaben";

	private String h_searchString;
	private String h_searchRadius;
	private String h_searchRating;
	private String h_searchAmountTo;
	private String h_searchCategorie;
	private int r_searchRadius;

	private List<Angebot> foundOffers = new ArrayList<>();
	private boolean sortbypriceascending = false;
	private boolean sortbybewertungascending = true;
	private boolean sortbyradiusascending = false;

	/*
	 * Ausgabe aller Angebote (ausgenommen des aktuellen Benutzers)
	 *
	 * @param io_model Model
	 *
	 * @return Name der Page
	 */

	/**
	 * Suchen und Anzeigen aller Angebote (ausgenommen des aktuellen Nutzers)
	 * 
	 * @param iv_searchString   Schlagwörter
	 * @param iv_searchRadius   Radius (in KM)
	 * @param iv_searchCategory eindeutige Identifikationsnummer der Kategorie
	 * @param iv_searchRating   Rating (ab)
	 * @param iv_searchAmountTo Betrag (bis)
	 * @param io_model          aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/search")
	public String showAllOffers(@RequestParam("searchString") String iv_searchString,
			@RequestParam("searchRadius") String iv_searchRadius, @RequestParam("searchTypes") String iv_searchCategory,
			@RequestParam("searchRating") String iv_searchRating,
			@RequestParam("searchAmountTo") String iv_searchAmountTo, Model io_model) {

		int iv_i_searchCategory;
		int searchRating;
		int searchRadius;
		double searchAmountTo;

		try {
			searchRating = Integer.parseInt(iv_searchRating);

		} catch (Exception e) {
			searchRating = 0;
		}

		try {
			searchRadius = Integer.parseInt(iv_searchRadius);

		} catch (Exception e) {
			searchRadius = 0;
		}

		r_searchRadius = searchRadius;

		try {
			searchAmountTo = Double.parseDouble(iv_searchAmountTo);

		} catch (Exception e) {
			searchAmountTo = 0;
		}

		if (iv_searchCategory.equals("Alle Kategorien")) {
			iv_i_searchCategory = 0;
		} else {
			iv_i_searchCategory = Integer.parseInt(iv_searchCategory);

		}

		List<Angebot> lt_offer;

		lt_offer = searchService.search(iv_searchString, searchRadius, iv_i_searchCategory, searchAmountTo,
				searchRating);

		foundOffers.clear();
		foundOffers.addAll(lt_offer);

		io_model.addAttribute("offers", lt_offer);

		CommonService.prepModel(io_model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		if (iv_searchString.trim().isEmpty()) {
			io_model.addAttribute("searchString", dummy);
			h_searchString = dummy;
		} else {
			io_model.addAttribute("searchString", iv_searchString);
			h_searchString = iv_searchString;
		}

		if (iv_searchRadius.trim().isEmpty()) {
			io_model.addAttribute("searchRadius", dummy);
			h_searchRadius = dummy;
		} else {
			io_model.addAttribute("searchRadius", iv_searchRadius + " km");
			h_searchRadius = iv_searchRadius + " km";
		}

		if (iv_searchAmountTo.trim().isEmpty()) {
			io_model.addAttribute("searchAmountTo", dummy);
			h_searchAmountTo = dummy;
		} else {
			io_model.addAttribute("searchAmountTo", iv_searchAmountTo + " €");
			h_searchAmountTo = iv_searchAmountTo + " €";
		}

		if (iv_i_searchCategory == 0) {
			io_model.addAttribute("searchCategories", "Alle Kategorien");
			h_searchCategorie = "Alle Kategorien";
		} else {
			io_model.addAttribute("searchCategories",
					angebotskategorieService.findOfferCategorieById(iv_i_searchCategory).getBezeichnung());
			h_searchCategorie = angebotskategorieService.findOfferCategorieById(iv_i_searchCategory).getBezeichnung();
		}

		switch (iv_searchRating) {
		case "1":
			io_model.addAttribute("searchRating", "1 Stern");
			h_searchRating = "1 Stern";
			break;

		case "2":
			io_model.addAttribute("searchRating", "2 Sterne");
			h_searchRating = "2 Sterne";
			break;

		case "3":
			io_model.addAttribute("searchRating", "3 Sterne");
			h_searchRating = "3 Sterne";
			break;

		case "4":
			io_model.addAttribute("searchRating", "4 Sterne");
			h_searchRating = "4 Sterne";
			break;

		case "5":
			io_model.addAttribute("searchRating", "5 Sterne");
			h_searchRating = "5 Sterne";
			break;

		default:
			io_model.addAttribute("searchRating", "Alle Bewertungen");
			h_searchRating = "Alle Bewertungen";
			break;
		}

		GoogleModel lo_googleModel = new GoogleModel();
		lo_googleModel.setApiKey(io_model);
		lo_googleModel.showOffersOnMap(io_model, lt_offer);
		lo_googleModel.showRadiusOnMap(io_model, searchRadius);
		lo_googleModel.centerGoogleMap(io_model, nutzerService.getCurrentUser(), getZoom(r_searchRadius));

		return "search";
	}

	/**
	 * Abhängig des gesetzten Radius wird der Zoom der Google Maps festgelegt
	 * 
	 * @param iv_radius Radius (in KM)
	 * @return Zoomfaktor
	 */
	private int getZoom(int iv_radius) {
		if (iv_radius == 0) {
			return 8;
		}

		if (iv_radius <= 1) {
			return 13;
		} else if (iv_radius <= 3) {
			return 12;
		} else if (iv_radius <= 6) {
			return 11;
		} else if (iv_radius <= 13) {
			return 10;
		} else if (iv_radius <= 25) {
			return 9;
		} else if (iv_radius <= 50 || iv_radius == 0) {
			return 8;
		} else if (iv_radius <= 100) {
			return 7;
		} else {
			return 6;
		}
	}

	/**
	 * Sortierung des Suchergebnisses anhand des Preises
	 * 
	 * @param model aktuelles Model
	 * @return Zielseite
	 */
	@RequestMapping(value = "/search", params = "preis", method = RequestMethod.POST)
	public String sortByPrice(Model model) {

		CommonService.prepModel(model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		foundOffers.sort(new Comparator<Angebot>() {
			@Override
			public int compare(Angebot o1, Angebot o2) {
				return Double.compare(o1.getBetrag(), o2.getBetrag());
			}
		});

		if (sortbypriceascending) {
			Collections.reverse(foundOffers);
			sortbypriceascending = false;
		} else {
			sortbypriceascending = true;
		}

		prepSearchModel(model);
		return "search";
	}

	/**
	 * Sortierung des Suchergebnisses anhand der Bewertungen
	 * 
	 * @param model aktuelles Model
	 * @return Zielseite
	 */
	@RequestMapping(value = "/search", params = "bewertung", method = RequestMethod.POST)
	public String sortByBewertung(Model model) {

		CommonService.prepModel(model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		foundOffers.sort(new Comparator<Angebot>() {
			@Override
			public int compare(Angebot o1, Angebot o2) {
				return Double.compare(nutzerService.getUserRating(o1.getAnbieter().getEmail()),
						nutzerService.getUserRating(o2.getAnbieter().getEmail()));
			}
		});

		if (sortbybewertungascending) {
			Collections.reverse(foundOffers);
			sortbybewertungascending = false;
		} else {
			sortbybewertungascending = true;
		}

		prepSearchModel(model);
		return "search";
	}

	/**
	 * Sortierung des Suchergebnisses anhand des Radius
	 * 
	 * @param model aktuelles Model
	 * @return Zielseite
	 */
	@RequestMapping(value = "/search", params = "radius", method = RequestMethod.POST)
	public String sortByRadius(Model model) {

		CommonService.prepModel(model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		foundOffers.sort(new Comparator<Angebot>() {
			@Override
			public int compare(Angebot o1, Angebot o2) {
				return Long.compare(o1.getDistancelong(), o2.getDistancelong());
			}
		});

		if (sortbyradiusascending) {
			Collections.reverse(foundOffers);
			sortbyradiusascending = false;
		} else {
			sortbyradiusascending = true;
		}

		prepSearchModel(model);
		return "search";
	}

	/**
	 * Vorbereiten des aktuellen Models mit benötigten Informationen
	 * 
	 * @param model aktuelles Model
	 */
	private void prepSearchModel(Model model) {
		model.addAttribute("offers", foundOffers);
		model.addAttribute("searchRating", h_searchRating);
		model.addAttribute("searchCategories", h_searchCategorie);
		model.addAttribute("searchAmountTo", h_searchAmountTo);
		model.addAttribute("searchRadius", h_searchRadius);
		model.addAttribute("searchString", h_searchString);

		GoogleModel lo_googleModel = new GoogleModel();
		lo_googleModel.setApiKey(model);
		lo_googleModel.showOffersOnMap(model, foundOffers);
		lo_googleModel.showRadiusOnMap(model, r_searchRadius);
		lo_googleModel.centerGoogleMap(model, nutzerService.getCurrentUser(), getZoom(r_searchRadius));
	}
}