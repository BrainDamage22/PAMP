package de.edu.pamp.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;

/**
 * 
 * @author Tobias
 *
 *         Eigenschaft der Entität: Tag
 */
@Entity
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int tagId;
	@NotEmpty
	private String tag;
	@ManyToOne
	@JoinColumn(name = "fk_angebottag")
	private Angebot angebot;

	/**
	 * Konstruktor
	 */
	public Tag() {
	}

	/**
	 * Holen der eindeutigen Identifikationsnummer
	 * 
	 * @return eindeutige Identifikationsnummer
	 */
	public int getTagId() {
		return tagId;
	}

	/**
	 * Setzen der eindeutigen Identifikationsnummer
	 * 
	 * @param tagId Eindeutige Identifikationsnummer
	 */
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	/**
	 * Holen des Tags
	 * 
	 * @return Tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Setzen des Tags
	 * 
	 * @param tag Tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * Holen des dazugehörigen Angebots
	 * 
	 * @return dazugehöriges Angebot
	 */
	public Angebot getAngebot() {
		return angebot;
	}

	/**
	 * Setzen des dazugehörigen Angebots
	 * 
	 * @param angebot dazugehöriges Angebot
	 */
	public void setAngebot(Angebot angebot) {
		this.angebot = angebot;
	}
}