package de.edu.pamp.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.geolocation.GoogleModel;
import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;

/**
 * 
 * @author Tobias
 *
 *         Services rund um die Listdarstellung von Nutzern
 */
@Controller
public class UserListController {

	@Autowired
	private NutzerService nutzerService;
	@Autowired
	private MessageService messageService;

	/**
	 * Ausgabe aller User
	 *
	 * @return Zielseite
	 */
	@GetMapping("/displayUserList")
	public ModelAndView showAllUsers() {
		ModelAndView lo_mav = new ModelAndView();
		List<Nutzer> lt_user;

		CommonService.prepModel(lo_mav, messageService.getCountUnreadMessages(nutzerService.getCurrentUserId()),
				nutzerService.isUserLocked(nutzerService.getCurrentUserId()),
				nutzerService.isUserAdmin(nutzerService.getCurrentUserId()));

		if (nutzerService.isUserAdmin(nutzerService.getCurrentUserId())) {
			lt_user = nutzerService.getAllUsers();

			lt_user.removeIf(nutzer -> nutzer.getEmail().equals(nutzerService.getCurrentUser().getEmail()));

			GoogleModel lo_googleModel = new GoogleModel();
			lo_googleModel.setApiKey(lo_mav);
			lo_googleModel.showUsersOnMap(lo_mav, lt_user);
			lo_googleModel.centerGoogleMap(lo_mav, nutzerService.getCurrentUser(), 7);
		} else {
			lt_user = new ArrayList<Nutzer>();
		}

		lo_mav.addObject("users", lt_user);
		lo_mav.setViewName("displayUserList");

		return lo_mav;
	}
}