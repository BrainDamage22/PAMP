package de.edu.pamp.dto;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.CreationTimestamp;

/**
 * 
 * @author Tobias
 *
 *         Beschreibung der Entitaet: Nachricht
 */
@Entity
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int messageId;
	private String messageFrom;
	private String messageTo;

	private boolean delInbox = false;

	private boolean delOutbox = false;

	private boolean read = false;
	@NotEmpty
	private String title;
	@NotEmpty
	@Column(length = 1000)
	private String nachricht;
	@Transient
	private String senderName;
	@Transient
	private String receiverName;
	@CreationTimestamp
	private Timestamp sendOn;

	private boolean isBuyRequest = false;

	@ManyToOne
	@JoinColumn(name = "fk_angebotId")
	private Angebot offerId;

	/**
	 * Konstruktor
	 */
	public Message() {
	}

	/**
	 * Holen der eindeutigen Identifikationsnummer
	 * 
	 * @return eindeutigen Identifikationsnummer
	 */
	public int getMessageId() {
		return messageId;
	}

	/**
	 * Setzen der eindeutigen Identifikationsnummer
	 * 
	 * @param messageId das zu setzende Objekt messageId
	 */
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	/**
	 * Holen der eindeutigen Identifikationsnummer des Absenders
	 * 
	 * @return eindeutigen Identifikationsnummer des Absenders
	 */
	public String getMessageFrom() {
		return messageFrom;
	}

	/**
	 * Setzen der eindeutigen Identifikationsnummer des Absenders
	 * 
	 * @param messageFrom das zu setzende Objekt messageFrom
	 */
	public void setMessageFrom(String messageFrom) {
		this.messageFrom = messageFrom;
	}

	/**
	 * Holen der eindeutigen Identifikationsnummer des Empfängers
	 * 
	 * @return eindeutigen Identifikationsnummer des Empfängers
	 */
	public String getMessageTo() {
		return messageTo;
	}

	/**
	 * Setzen der eindeutigen Identifikationsnummer des Empfängers
	 * 
	 * @param messageTo eindeutigen Identifikationsnummer des Empfängers
	 */
	public void setMessageTo(String messageTo) {
		this.messageTo = messageTo;
	}

	/**
	 * Holen des Loesch-Kennzeichens für den Posteingang
	 * 
	 * @return Loesch-Kennzeichen für den Posteingang
	 */
	public boolean isDelInbox() {
		return delInbox;
	}

	/**
	 * Setzen des Loesch-Kennzeichens für den Posteingang
	 * 
	 * @param delInbox Loesch-Kennzeichens für den Posteingang
	 */
	public void setDelInbox(boolean delInbox) {
		this.delInbox = delInbox;
	}

	/**
	 * Holen des Loesch-Kennzeichens für den Postausgang
	 * 
	 * @return Loesch-Kennzeichen für den Postausgang
	 */
	public boolean isDelOutbox() {
		return delOutbox;
	}

	/**
	 * Setzen des Loesch-Kennzeichens für den Postausgang
	 * 
	 * @param delOutbox Loesch-Kennzeichens für den Postausgang
	 */
	public void setDelOutbox(boolean delOutbox) {
		this.delOutbox = delOutbox;
	}

	/**
	 * Holen des Gelesen-Kennzeichens
	 * 
	 * @return Gelesen-Kennzeichen
	 */
	public boolean isRead() {
		return read;
	}

	/**
	 * Setzen des Gelesen-Kennzeichens
	 * 
	 * @param read Gelesen-Kennzeichen
	 */
	public void setRead(boolean read) {
		this.read = read;
	}

	/**
	 * Holen des Titels
	 * 
	 * @return Titel
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setzen des Titels
	 * 
	 * @param title Titel
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Holen des Nachrichteninhalts
	 * 
	 * @return Nachrichteninhalt
	 */
	public String getNachricht() {
		return nachricht;
	}

	/**
	 * Setzen des Nachrichteninhalts
	 * 
	 * @param nachricht Nachrichteninhalt
	 */
	public void setNachricht(String nachricht) {
		this.nachricht = nachricht;
	}

	/**
	 * Holen des Sendernamens
	 * 
	 * @return Sendername
	 */
	public String getSenderName() {
		return senderName;
	}

	/**
	 * Setzen des Sendernamens
	 * 
	 * @param senderName Sendername
	 */
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	/**
	 * Holen des Empfängernamens
	 * 
	 * @return Empfängername
	 */
	public String getReceiverName() {
		return receiverName;
	}

	/**
	 * Setzen des Empfängernamens
	 * 
	 * @param receiverName Empfängername
	 */
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	/**
	 * Holen des Sendezeitpunkts
	 * 
	 * @return Sendezeitpunkt
	 */
	public Timestamp getSendOn() {
		return sendOn;
	}

	/**
	 * Setzen des Sendezeitpunkts
	 * 
	 * @param sendOn Sendezeitpunkt
	 */
	public void setSendOn(Timestamp sendOn) {
		this.sendOn = sendOn;
	}

	/**
	 * Holen des Kaufanfrage-Kennzeichens
	 * 
	 * @return Kaufanfrage-Kennzeichen
	 */
	public boolean isBuyRequest() {
		return isBuyRequest;
	}

	/**
	 * Setzen des Kaufanfrage-Kennzeichens
	 * 
	 * @param buyRequest Kaufanfrage-Kennzeichen (True/False)
	 */
	public void setBuyRequest(boolean buyRequest) {
		isBuyRequest = buyRequest;
	}

	/**
	 * Holen des dazugehörigen Angebots
	 * 
	 * @return dazugehöriges Angebot
	 */
	public Angebot getOfferId() {
		return offerId;
	}

	/**
	 * Setzen des dazugehörigen Angebots
	 * 
	 * @param offerId dazugehörige Angebot
	 */
	public void setOfferId(Angebot offerId) {
		this.offerId = offerId;
	}
}