package de.edu.pamp.dto;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * 
 * @author Tobias
 *
 *         Beschreibung der Entität: Rolle
 */
@Entity
public class Role {

	@Id
	private String name;
	@ManyToMany(mappedBy = "roles")
	private List<Nutzer> users;

	/**
	 * Holen der Rollenname
	 * 
	 * @return Rollenname
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setzen des Rollennamens
	 * 
	 * @param iv_name Rollenname
	 */
	public void setName(String iv_name) {
		this.name = iv_name;
	}

	/**
	 * Holen der zugeordneten Nutzer
	 * 
	 * @return Nutzerliste
	 */
	public List<Nutzer> getUsers() {
		return users;
	}

	/**
	 * Setzen der zugeordneten Nutzer
	 * 
	 * @param it_users Nutzerliste
	 */
	public void setUsers(List<Nutzer> it_users) {
		this.users = it_users;
	}

	/**
	 * Konstruktor
	 * 
	 * @param iv_name  Rollenname
	 * @param it_users zugeordnete Nutzer
	 */
	public Role(String iv_name, List<Nutzer> it_users) {
		this.name = iv_name;
		this.users = it_users;
	}

	/**
	 * Konstruktor
	 */
	public Role() {
	}

	/**
	 * Konstruktor
	 * 
	 * @param iv_name Rollenname
	 */
	public Role(String iv_name) {
		this.name = iv_name;
	}
}