/**
 * 
 */
package de.edu.pamp.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.edu.pamp.dto.Message;
import de.edu.pamp.repository.MessageRepository;

/**
 * 
 * @author Tobias
 *
 *         Services rum um die Behandlung von Nachrichten
 *
 */
@Service
public class MessageService {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private NutzerService nutzerService;

	/**
	 * Anlage einer neuen Nachricht
	 * 
	 * @param message neue Nachricht
	 */
	public void createMessage(Message message) {
		message.setSendOn(new Timestamp(System.currentTimeMillis()));
		messageRepository.save(message);
	}

	/**
	 * Löschen einer Nachricht aus dem Posteingang
	 * 
	 * @param io_message zu löschende Nachricht
	 */
	public void deleteMessageFromInbox(Message io_message) {
		Message lo_message = messageRepository.getOne(io_message.getMessageId());
		lo_message.setDelInbox(true);
		updateMessage(lo_message);

		deleteMessage(lo_message);
	}

	/**
	 * Löschen einer Nachricht aus dem Postausgang
	 * 
	 * @param io_message zu löschende Nachricht
	 */
	public void deleteMessageFromOutbox(Message io_message) {
		Message lo_message = messageRepository.getOne(io_message.getMessageId());
		lo_message.setDelOutbox(true);
		updateMessage(lo_message);

		deleteMessage(lo_message);
	}

	/**
	 * Eine Nachricht erst löschen, falls beide Parteien (Inbox/Outbox) die
	 * Nachricht als gelöscht markiert haben
	 * 
	 * @param message zu löschende Nachricht
	 */
	private void deleteMessage(Message message) {
		if (message.isDelInbox() && message.isDelOutbox()) {
			messageRepository.delete(message);
		}
	}

	/**
	 * Ermittlung aller Nachrichten zu einem gesuchten Nutzer
	 * 
	 * @param UserId eindeutige Identifikationsnummer des Nutzers
	 * @return Trefferliste
	 */
	public List<Message> findMessagesByUserId(String UserId) {
		List<Message> all = messageRepository.findAll();
		List<Message> userMessages = new ArrayList<>();

		for (Message message : all) {
			if (message.getMessageTo().equals(UserId)) {
				userMessages.add(message);
			}
		}

		return userMessages;
	}

	/**
	 * Ermittlung aller bereits gelesenen Nachrichten eines Nutzers
	 * 
	 * @param UserId eindeutige Identifikationsnummer des Nutzers
	 * @return Trefferliste
	 */
	public List<Message> findReadMessagesByUserId(String UserId) {
		List<Message> all = messageRepository.findAll();
		List<Message> readUserMessages = new ArrayList<>();

		for (Message message : all) {
			if (message.getMessageTo().equals(UserId) && !message.isDelInbox()) {
				if (message.isRead()) {
					message.setReceiverName(nutzerService.getName(message.getMessageTo()));
					message.setSenderName(nutzerService.getName(message.getMessageFrom()));
					readUserMessages.add(message);
				}
			}
		}

		sortMessagesByDate(readUserMessages);
		return readUserMessages;
	}

	/**
	 * Ermittlung aller ungelesener Nachrichten eines Nutzers
	 * 
	 * @param UserId eindeutige Identifikationsnummer des Nutzers
	 * @return Trefferliste
	 */
	public List<Message> findUnreadMessagesByUserId(String UserId) {
		List<Message> all = messageRepository.findAll();
		List<Message> unreadUserMessages = new ArrayList<>();

		for (Message message : all) {
			if (message.getMessageTo().equals(UserId) && !message.isDelInbox()) {
				if (!message.isRead()) {
					message.setReceiverName(nutzerService.getName(message.getMessageTo()));
					message.setSenderName(nutzerService.getName(message.getMessageFrom()));
					unreadUserMessages.add(message);
				}
			}
		}

		sortMessagesByDate(unreadUserMessages);
		return unreadUserMessages;
	}

	/**
	 * Ermittlung einer Nachricht
	 * 
	 * @param id eindeutige Identifikationsnummer der gesuchten Nachricht. Bei
	 *           Treffer werden der Nachricht der vollständige Name des Senders und
	 *           Empfängers angehängt.
	 * @return Nachricht oder NULL
	 */
	public Message findMessageById(int id) {
		Message message = messageRepository.findById(id).orElse(null);
		assert message != null;
		message.setSenderName(nutzerService.getName(message.getMessageFrom()));
		message.setReceiverName(nutzerService.getName(message.getMessageTo()));
		return message;
	}

	/**
	 * Änderung einer Nachricht
	 * 
	 * @param message zu ändernde Nachricht
	 */
	public void updateMessage(Message message) {
		messageRepository.save(message);
	}

	/**
	 * Liefert die Anzahl der ungelesenen Nachrichten eines Nutzers zurück
	 * 
	 * @param userId eindeutige Identifikationsnummer des Nutzers
	 * @return Anzahl ungelesener Nachrichten
	 */
	public int getCountUnreadMessages(String userId) {
		return findUnreadMessagesByUserId(userId).size();
	}

	/**
	 * Ermittlung aller gesendeter Nachrichten eines Nutzers
	 * 
	 * @param userId eindeutige Identifikation des Nutzers
	 * @return Trefferliste
	 */
	public List<Message> findSentMessages(String userId) {
		List<Message> all = messageRepository.findAll();
		List<Message> sentUserMessages = new ArrayList<>();

		for (Message message : all) {
			if (message.getMessageFrom().equals(userId) && !message.isDelOutbox()) {
				message.setReceiverName(nutzerService.getName(message.getMessageTo()));
				message.setSenderName(nutzerService.getName(message.getMessageFrom()));

				sentUserMessages.add(message);
			}
		}

		sortMessagesByDate(sentUserMessages);
		return sentUserMessages;
	}

	/**
	 * Nachrichten werden absteigend nach ihrem Zeitstempel sortiert
	 * 
	 * @param it_messages zu sortierende Nachrichtenliste
	 */
	public void sortMessagesByDate(List<Message> it_messages) {
		it_messages.sort(new Comparator<Message>() {
			@Override
			public int compare(Message m1, Message m2) {
				if (m1.getSendOn() == null || m2.getSendOn() == null) {
					return 0;
				}
				return m2.getSendOn().compareTo(m1.getSendOn());
			}
		});
	}
}