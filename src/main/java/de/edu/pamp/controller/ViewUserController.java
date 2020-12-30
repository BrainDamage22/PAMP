package de.edu.pamp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.geolocation.GoogleModel;
import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;

/**
 * 
 * @author Tobias
 *
 *         Services rund um die Behandlung der Nutzeranzeige
 */
@Controller
public class ViewUserController {

	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private MessageService messageService;

	/**
	 * Anzeige eines Nutzers an der UI
	 * 
	 * @param io_model aktuelles Model
	 * @param iv_email anzuzeigender Nutzer
	 * @return Zielseite
	 */
	@GetMapping("/viewUser")
	public String showUser(Model io_model, @RequestParam("email") String iv_email) {
		Nutzer lo_user = nutzerService.findUserById(iv_email);
		io_model.addAttribute("nutzer", lo_user);

		CommonService.prepModel(io_model, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		GoogleModel lo_googleModel = new GoogleModel();
		lo_googleModel.setApiKey(io_model);
		lo_googleModel.showUserOnMap(io_model, lo_user);

		io_model.addAttribute("currentRating", nutzerService.getUserRating(iv_email));

		return "viewUser";
	}
}