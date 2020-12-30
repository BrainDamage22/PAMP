package de.edu.pamp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;

/**
 * Controller für die Verarbeitung simpler Seiten
 * 
 * @author Tobias
 *
 */
@Controller
public class IndexController {

	@Autowired
	private MessageService mo_messageService;
	@Autowired
	private NutzerService mo_nutzerService;

	/**
	 * Anzeigen der Startseite
	 *
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/")
	public String retIndex(Model io_model) {
		prepareModel(io_model);
		return "index.html";
	}

	/**
	 * Anzeigen der Startseite
	 *
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/index")
	public String index(Model io_model) {
		prepareModel(io_model);
		return "index.html";
	}

	/**
	 * Anzeigen der Success-Seite
	 *
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/success")
	public String retSuccess(Model io_model) {
		prepareModel(io_model);
		return "success";
	}

	/**
	 * Anzeigen der Fehlerseite
	 *
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/failure")
	public String retFailure(Model io_model) {
		prepareModel(io_model);
		return "error";
	}

	/**
	 * Anzeigen des Impressums
	 *
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/impressum")
	public String retImpressum(Model io_model) {
		prepareModel(io_model);
		return "impressum";
	}

	/**
	 * Anzeigen der Agb-Seite
	 *
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/agbs")
	public String retAgbs(Model io_model) {
		prepareModel(io_model);
		return "agbs";
	}

	/**
	 * Anzeigen der Kontakt-Seite
	 *
	 * @param io_model Aktuelles Model
	 * @return Zielseite
	 */
	@GetMapping("/contacts")
	public String retContacts(Model io_model) {
		prepareModel(io_model);
		return "contacts";
	}

	/**
	 * Bereitet das übergebene Model vor
	 *
	 * @param io_model Aktuelles Model
	 */
	private void prepareModel(Model io_model) {
		if (!mo_nutzerService.getCurrentUserId().isEmpty()) {
			CommonService.prepModel(io_model,
					mo_messageService.getCountUnreadMessages(mo_nutzerService.getCurrentUserId()),
					mo_nutzerService.isUserLocked(mo_nutzerService.getCurrentUserId()),
					mo_nutzerService.isUserAdmin(mo_nutzerService.getCurrentUserId()));
		}
	}
}