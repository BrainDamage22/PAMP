package de.edu.pamp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.edu.pamp.dto.Message;
import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.geolocation.GoogleModel;
import de.edu.pamp.mail.NotificationService;
import de.edu.pamp.services.AngebotService;
import de.edu.pamp.services.CommonService;
import de.edu.pamp.services.MessageService;
import de.edu.pamp.services.NutzerService;

/**
 * @author Lukas
 *         <p>
 *         Controller zum Sperren eines Nutzers
 */
@Controller
public class LockUserController {
	@Autowired
	private NutzerService mo_nutzerService;
	@Autowired
	private AngebotService mo_angebotService;
	@Autowired
	private MessageService mo_messageService;
	@Autowired
	private NotificationService mo_notificationService;

	/**
	 *
	 * Sperren eines Nutzers
	 *
	 * @param io_model  Aktuelles Model
	 * @param iv_reason Grund der Sperrung
	 * @param iv_userId UserId des Nutzers der gesperrt werden soll
	 * @return Zielseite
	 */
	@PostMapping("/lockUser")
	public ModelAndView lockUser(Model io_model, @RequestParam("u_reason") String iv_reason,
			@RequestParam("userId") String iv_userId) {

		ModelAndView lo_mav = new ModelAndView();
		Nutzer lo_currentUser = mo_nutzerService.getCurrentUser();
		Nutzer lo_user = mo_nutzerService.findUserById(iv_userId);

		if (!mo_nutzerService.isUserAdmin(lo_currentUser.getEmail())) {
			lo_mav.addObject("noAdmin", true);

		} else if (iv_reason.trim().isEmpty()) {
			lo_mav.addObject("noReason", true);

		} else {
			Message lo_message = new Message();
			boolean lf_locked = lo_user.isSperre();

			if (lf_locked) {
				lo_message.setTitle("Sie wurden entsperrt");
			} else {
				lo_message.setTitle("Sie wurden gesperrt");
			}

			lo_message.setMessageFrom(lo_currentUser.getEmail());
			lo_message.setMessageTo(lo_user.getEmail());
			lo_message.setNachricht(iv_reason);
			lo_message.setRead(false);
			lo_message.setSenderName(lo_currentUser.getVorname() + " " + lo_currentUser.getNachname());
			lo_message.setReceiverName(lo_user.getVorname() + " " + lo_user.getNachname());

			try {
				mo_notificationService.sendMessageUnLockInformation(lo_message);
				mo_messageService.createMessage(lo_message);

				if (lf_locked) {
					mo_nutzerService.setNutzerUnLocked(lo_user);
					mo_angebotService.enableAllOffers(lo_user);
					lo_mav.addObject("unlockEnabled", true);
				} else {
					mo_nutzerService.setNutzerLocked(lo_user);
					mo_angebotService.disableAllOffers(lo_user);
					lo_mav.addObject("lockEnabled", true);
				}

			} catch (MailException e) {
				lo_mav.addObject("MessageFailed", e.getMessage());
				lo_mav.addObject("sendMessageFailed", true);
			}
		}

		CommonService.prepModel(lo_mav, mo_messageService.getCountUnreadMessages(lo_currentUser.getEmail()),
				mo_nutzerService.isUserLocked(lo_currentUser.getEmail()),
				mo_nutzerService.isUserAdmin(lo_currentUser.getEmail()));

		GoogleModel lo_googleModel = new GoogleModel();
		lo_googleModel.setApiKey(io_model);
		lo_googleModel.showUserOnMap(io_model, lo_user);

		lo_mav.addObject("nutzer", lo_user);
		lo_mav.setViewName("viewUser");

		lo_mav.addObject("currentRating", mo_nutzerService.getUserRating(iv_userId));
		return lo_mav;
	}
}