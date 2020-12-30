/**
 * 
 */
package de.edu.pamp.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.edu.pamp.dto.Tag;
import de.edu.pamp.repository.TagRepository;

/**
 * 
 * @author Tobias
 *
 *         Services rum um die Behandlung von Tags eines Angebots
 */
@Service
public class TagService {

	@Autowired
	private TagRepository tagRepository;

	/**
	 * Anlage eines Tags
	 * 
	 * @param tag neuer Tag
	 */
	public void createTag(Tag tag) {
		tagRepository.save(tag);
	}

	/**
	 * Ermittlung aller, zu einem bestimmten Angebot zugehörigen, Tags
	 * 
	 * @param angebotsId eindeutige Identifikationsnummer eines Angebots
	 * @return Liste mit gefundenen Tags
	 */
	public List<Tag> getTagsForAngebot(int angebotsId) {
		List<Tag> all = tagRepository.findAll();
		List<Tag> forAngebot = new ArrayList<>();

		for (Tag tag : all) {
			if (tag.getAngebot().getAngebotId() == angebotsId) {
				forAngebot.add(tag);
			}
		}
		return forAngebot;
	}

	/**
	 * Löschen aller, zu einem bestimmten Angebot zugehörigen, Tags
	 * 
	 * @param iv_offerId eindeutige Identifikationsnummer eines Angebots
	 */
	public void deleteTagsByOfferId(int iv_offerId) {
		for (Tag lo_tag : getTagsForAngebot(iv_offerId)) {
			tagRepository.delete(lo_tag);
		}
	}

	/**
	 * Löschen eines Tags
	 * 
	 * @param tagId eindeutige Identifikationsnummer eines Tags
	 */
	public void deleteTagbyID(int tagId) {
		Tag tag = tagRepository.getOne(tagId);
		tagRepository.delete(tag);
	}
}