package de.edu.pamp.mail;

import javax.mail.MessagingException;

import de.edu.pamp.dto.Angebot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import de.edu.pamp.dto.Message;
import de.edu.pamp.dto.Nutzer;
import de.edu.pamp.dto.VerificationToken;
import de.edu.pamp.services.NutzerService;
import de.edu.pamp.services.VerificationTokenService;


@Service
public class NotificationService {

	private JavaMailSender javaMailSender;

	private static String local = "http://132.231.36.205:8080/";

	@Autowired
	private VerificationTokenService verificationTokenService;

	@Autowired
	private NutzerService nutzerService;

	@Autowired
	public NotificationService(JavaMailSender io_mailSender) {
		this.javaMailSender = io_mailSender;
	}

	public boolean sendRegistrationConfirmation(Nutzer io_user, String iv_registerUrl) throws MessagingException {
		boolean result = true;
		SimpleMailMessage lo_mail = new SimpleMailMessage();
		String text = "Sie haben es fast geschafft!" + "\n\n" + "Hallo " + io_user.getVorname() + "! \n\n"
				+ "Bitte bestätigen Sie Ihre neue bei PAMP hinterlegte E-Mail-Adresse durch Klicken auf diesen Link:"
				+ "\n" + local + iv_registerUrl + "\n" + "Dieser Link ist 24 Stunden gültig." + "\n\n"
				+ "Wir freuen uns, Sie bei PAMP begrüßen zu dürfen!" + "\n" + "PAMP Customer Service" + "\n\n"
				+ "Sie haben diese E-Mail erhalten, weil Sie eine Anfrage für ein PAMP.com Konto mit E-Mail-Adresse gestellt haben.\n"
				+ "Wenn dies nicht Ihre Absicht war, ignorieren Sie bitte diese Email.";
		lo_mail.setTo(io_user.getEmail());
		lo_mail.setFrom("PAMP-Customer-Service");
		lo_mail.setText(text);
		lo_mail.setSubject("Willkommen bei PAMP!");
		try {
			javaMailSender.send(lo_mail);
		} catch (MailException e) {
			result = false;
		}
		return result;
	}

	public boolean sendMessageReceivedInformation(String receiverId, String senderId, Message nachricht) {
		boolean result = true;
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		String subject = nutzerService.getName(senderId) + " hat Ihnen eine Nachricht geschrieben!";
		String message = "Hallo " + nutzerService.findUserById(receiverId).getVorname() + "! \n\n"
				+ nutzerService.getName(senderId) + " hat Ihnen eine Nachricht gesendet: \n\n" + "''"
				+ nachricht.getNachricht() + "''\n\n"
				+ "Um auf diese Nachricht zu antworten besuchen Sie bitte Ihr Postfach bei " + local + "\n\n"
				+ "Liebe Grüße, \n" + "PAMP Customer Service";
		mailMessage.setTo(receiverId);
		mailMessage.setFrom("PAMP-Customer-Service");
		mailMessage.setText(message);
		mailMessage.setSubject(subject);
		try {
			javaMailSender.send(mailMessage);
		} catch (MailException e) {
			result = false;
		}
		return result;
	}

	/**
	 * Senden einer E-Mail bei Löschen eines Angebots durch einen Administrator an
	 * den Inserenten
	 * 
	 * @param io_message Kontext der E-Mail
	 * @throws MailException Nachrichtenfehler
	 */
	public void sendMessageOfferDeletedByAdmin(Message io_message) throws MailException {
		SimpleMailMessage lo_smm = new SimpleMailMessage();

		lo_smm.setTo(io_message.getMessageTo());

		lo_smm.setFrom("PAMP-Customer-Service");

		lo_smm.setSubject(
				nutzerService.getName(io_message.getMessageFrom()) + " hat Ihnen eine Nachricht geschrieben!");

		lo_smm.setText("Hallo " + nutzerService.findUserById(io_message.getMessageTo()).getVorname() + "!\n\n"
				+ "Der Administrator '" + io_message.getSenderName() + "' hat Ihnen eine Nachricht gesendet: \n\n"
				+ "''" + io_message.getTitle() + "''\n\n"
				+ "Um den Grund für diese Aktion zu erfahren oder auf diese Nachricht zu antworten, besuchen Sie bitte Ihr Postfach bei "
				+ local + "\n\n" + "Liebe Grüße, \n" + "PAMP Customer Service");

		javaMailSender.send(lo_smm);
	}

	/**
	 * Senden einer E-Mail beim Sperren oder Entsperren eines Nutzers durch den
	 * Administrator
	 * 
	 * @param io_message Kontext der E-Mail
	 * @throws MailException Nachrichtenfehler
	 */
	public void sendMessageUnLockInformation(Message io_message) throws MailException {
		SimpleMailMessage lo_smm = new SimpleMailMessage();

		lo_smm.setTo(io_message.getMessageTo());

		lo_smm.setFrom("PAMP-Customer-Service");

		lo_smm.setSubject(
				nutzerService.getName(io_message.getMessageFrom()) + " hat Ihnen eine Nachricht geschrieben!");

		lo_smm.setText("Hallo " + nutzerService.findUserById(io_message.getMessageTo()).getVorname() + "!\n\n"
				+ "Der Administrator '" + io_message.getSenderName() + "' hat Ihnen eine Nachricht gesendet: \n\n"
				+ "''" + io_message.getTitle() + "''\n\n"
				+ "Um den Grund für diese Aktion zu erfahren oder auf diese Nachricht zu antworten, besuchen Sie bitte Ihr Postfach bei "
				+ local + "\n\n" + "Liebe Grüße, \n" + "PAMP Customer Service");

		javaMailSender.send(lo_smm);
	}

	/**
	 * Erneutes Senden einer E-Mail zur Bestätigung der Registrierung
	 * 
	 * @param receiver eindeutige Identifikationsnummer des Empfängers
	 * @return TRUE, falls der Versand erfolgreich war. FALSE, falls der Versand
	 *         fehlschlug.
	 */
	public boolean resendRegistration(String receiver) {
		boolean result = true;
		Nutzer nutzer = nutzerService.findUserById(receiver);
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		VerificationToken verificationToken = verificationTokenService.findbyUser(nutzer);
		verificationTokenService.updateToken(verificationToken.getId());
		String subject = "Ihr neuer Registrierungslink!";
		String text = "Sie haben es fast geschafft!" + "\n\n" + "Hallo " + nutzer.getVorname() + "! \n\n"
				+ "Bitte bestätigen Sie Ihre bei PAMP hinterlegte E-Mail-Adresse durch Klicken auf diesen Link:" + "\n"
				+ local + "registrationConfirm.html?token=" + verificationToken.getToken() + "\n"
				+ "Dieser Link ist 24 Stunden gültig." + "\n\n" + "Wir freuen uns, Sie bei PAMP begrüßen zu dürfen!"
				+ "\n" + "PAMP Customer Service" + "\n\n"
				+ "Sie haben diese E-Mail erhalten, weil Sie eine Anfrage für ein PAMP.com Konto mit E-Mail-Adresse gestellt haben.\n"
				+ "Wenn dies nicht Ihre Absicht war, ignorieren Sie bitte diese Email.";
		mailMessage.setTo(nutzer.getEmail());
		mailMessage.setFrom("PAMP-Customer-Service");
		mailMessage.setText(text);
		mailMessage.setSubject(subject);
		try {
			javaMailSender.send(mailMessage);
		} catch (MailException e) {
			result = false;
		}
		return result;
	}

	/**
	 * Senden einer E-Mail für das Zurücksetzen des Passwortes
	 * 
	 * @param io_user        betroffener Nutzer
	 * @param iv_registerUrl Registrierungs-URL
	 * @return TRUE, falls der Versand erfolgreich war. FALSE, falls der Versand
	 *         fehlschlug.
	 * @throws MessagingException Nachrichtenfehler
	 */
	public boolean sendPasswortRecovery(Nutzer io_user, String iv_registerUrl) throws MessagingException {
		boolean result = true;
		SimpleMailMessage lo_mail = new SimpleMailMessage();
		String text = "Sie haben es fast geschafft!" + "\n\n" + "Hallo " + io_user.getVorname() + "! \n\n"
				+ "Mit diesem Link können sie ihr Passwort zurücksetzen" + "\n" + local + iv_registerUrl + "\n"
				+ "Dieser Link ist 24 Stunden gültig." + "\n\n" + "Wir freuen uns, Sie bei PAMP begrüßen zu dürfen!"
				+ "\n" + "PAMP Customer Service" + "\n\n"
				+ "Sie haben diese E-Mail erhalten, weil Sie eine Anfrage für eine Passwortänderung für ihr PAMP.com Konto gestellt haben.\n"
				+ "Wenn dies nicht Ihre Absicht war, ignorieren Sie bitte diese Email.";
		lo_mail.setTo(io_user.getEmail());
		lo_mail.setFrom("PAMP-Customer-Service");
		lo_mail.setText(text);
		lo_mail.setSubject("Passwort zurücksetzen");
		try {
			javaMailSender.send(lo_mail);
		} catch (MailException e) {
			result = false;
		}
		return result;
	}

	/**
	 * Senden einer E-Mail für die Kaufanfrage
	 * 
	 * @param kauefer Käufer
	 * @param angebot zu kaufendes Angebot
	 * @return TRUE, falls der Versand erfolgreich war. FALSE, falls der Versand
	 *         fehlschlug.
	 */
	public boolean sendBuyRequestMessage(Nutzer kauefer, Angebot angebot) {
		boolean result = true;
		SimpleMailMessage lo_mail = new SimpleMailMessage();
		String text = "Hallo " + angebot.getAnbieter().getVorname() + "! \n\n"
				+ "Sie haben eine neue Kaufanfrage für ihr Angebot " + angebot.getTitel() + " erhalten. \n\n"
				+ "Besuchen Sie ihr Konto unter " + local + " um die Kaufanfrage einzusehen.";
		lo_mail.setTo(angebot.getAnbieter().getEmail());
		lo_mail.setFrom("PAMP-Customer-Service");
		lo_mail.setText(text);
		lo_mail.setSubject("Neue Kaufanfrage für " + angebot.getTitel());
		try {
			javaMailSender.send(lo_mail);
		} catch (MailException e) {
			result = false;
		}
		return result;
	}

	/**
	 * Senden einer E-Mail für die Annahme/das Ablehner einer Kaufanfrage
	 * 
	 * @param kaeufer    interessierte Käufer
	 * @param verkaeufer Inserent
	 * @return TRUE, falls der Versand erfolgreich war. FALSE, falls der Versand
	 *         fehlschlug.
	 */
	public boolean sendBuyAcceptDeniedMessage(Nutzer kaeufer, Nutzer verkaeufer) {
		boolean result = true;
		SimpleMailMessage lo_mail = new SimpleMailMessage();
		String text = "Hallo " + kaeufer.getVorname() + "! \n\n" + verkaeufer.getVorname() + " "
				+ verkaeufer.getNachname() + " hat auf Ihre Kaufanfrage geantwortet. \n\n"
				+ "Besuchen Sie ihr Konto unter " + local + " um die Antwort einzusehen.";
		lo_mail.setTo(kaeufer.getEmail());
		lo_mail.setFrom("PAMP-Customer-Service");
		lo_mail.setText(text);
		lo_mail.setSubject("Der Verkäufer hat auf Ihre Kaufanfrage geantwortet!");
		try {
			javaMailSender.send(lo_mail);
		} catch (MailException e) {
			result = false;
		}
		return result;
	}
}