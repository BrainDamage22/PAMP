/**
 * 
 */
package de.edu.pamp.services;

import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Tobias
 * 
 *         Hilfsklasse für das Vorbelegen des Models mit Informationen die
 *         jederzeit benötigt werden
 */
public class CommonService {

	/**
	 * Konstruktor
	 */
	public CommonService() {
	}

	/**
	 * Setzen der für den Header benötigten Informationen
	 * 
	 * @param io_model              aktuelles Model dem die Informationen angefügt
	 *                              werden sollen
	 * @param iv_unreadMessageCount Anzahl ungelesener Nachrichten
	 * @param if_userLocked         Kennzeichen, ob aktueller Nutzer die Rolle
	 *                              "LOCKED" enthält
	 * @param if_userAdmin          Kennzeichen, ob aktueller Nutzer die Rolle
	 *                              "ADMIN" enthält
	 */
	public static void prepModel(Model io_model, int iv_unreadMessageCount, boolean if_userLocked,
			boolean if_userAdmin) {
		updateModel(io_model, iv_unreadMessageCount);
		io_model.addAttribute("isUserLocked", if_userLocked);
		io_model.addAttribute("isUserAdmin", if_userAdmin);
	}

	/**
	 * Setzen der für den Header benötigten Informationen
	 * 
	 * @param io_modelAndView       aktuelles Model dem die Informationen angefügt
	 *                              werden sollen
	 * @param iv_unreadMessageCount Anzahl ungelesener Nachrichten
	 * @param if_userLocked         Kennzeichen, ob aktueller Nutzer die Rolle
	 *                              "LOCKED" enthält
	 * @param if_userAdmin          Kennzeichen, ob aktueller Nutzer die Rolle
	 *                              "ADMIN" enthält
	 */
	public static void prepModel(ModelAndView io_modelAndView, int iv_unreadMessageCount, boolean if_userLocked,
			boolean if_userAdmin) {

		updateModel(io_modelAndView, iv_unreadMessageCount);
		io_modelAndView.addObject("isUserLocked", if_userLocked);
		io_modelAndView.addObject("isUserAdmin", if_userAdmin);
	}

	/**
	 * Setzt den Zähler für die ungelesenen Nachrichten neu. Dies kann nötig sein,
	 * wenn Aktionen nach dem Aufruf der prepModel einen Einfluss auf diesen haben
	 * 
	 * @param io_model              aktuelles Model dem die Informationen angefügt
	 *                              werden sollen
	 * @param iv_unreadMessageCount Anzahl ungelesener Nachrichten
	 */
	public static void updateModel(Model io_model, int iv_unreadMessageCount) {
		io_model.addAttribute("unreadMessageCount", iv_unreadMessageCount);
	}

	/**
	 * Setzt den Zähler für die ungelesenen Nachrichten neu. Dies kann nötig sein,
	 * wenn Aktionen nach dem Aufruf der prepModel einen Einfluss auf diesen haben
	 * 
	 * @param io_modelAndView       aktuelles Model dem die Informationen angefügt
	 *                              werden sollen
	 * @param iv_unreadMessageCount Anzahl ungelesener Nachrichten
	 */
	public static void updateModel(ModelAndView io_modelAndView, int iv_unreadMessageCount) {
		io_modelAndView.addObject("unreadMessageCount", iv_unreadMessageCount);
	}
}