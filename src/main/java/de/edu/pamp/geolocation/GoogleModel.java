package de.edu.pamp.geolocation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import de.edu.pamp.dto.Angebot;
import de.edu.pamp.dto.Nutzer;

/**
 * @author Tobias
 *
 *         Services rund um die Behandlung von Google an der UI
 */
@Service
public class GoogleModel {

	/**
	 * Setzen des Zugangschlüssels an der UI
	 * 
	 * @param io_mav aktuelles Model
	 */
	public void setApiKey(ModelAndView io_mav) {
		io_mav.addObject("API_KEY", GoogleContext.getAPIkey());
	}

	/**
	 * Setzen des Zugangsschlüssels an der UI
	 * 
	 * @param io_model aktuelles Model
	 */
	public void setApiKey(Model io_model) {
		io_model.addAttribute("API_KEY", GoogleContext.getAPIkey());
	}

	/**
	 * Darstellung eines Nutzers auf der Google Maps
	 * 
	 * @param io_mav  aktuelles Model
	 * @param io_user darzustellender Nutzer
	 */
	public void showUserOnMap(ModelAndView io_mav, Nutzer io_user) {
		GoogleMarker lt_googleMarker[] = new GoogleMarker[1];
		List<LatLng> lt_locations = new ArrayList<LatLng>();

		GeocodingResult lt_geocodingResult[] = GoogleContext.getGeocode(io_user.getAdresse(), io_user.getPlz(),
				io_user.getStadt());

		if (lt_geocodingResult != null) {
			lt_locations.add(lt_geocodingResult[0].geometry.location);
			lt_googleMarker[0] = new GoogleMarker(io_user.getVorname() + " " + io_user.getNachname(),
					lt_geocodingResult[0].geometry.location);
		}

		io_mav.addObject("googleMarkers", lt_googleMarker);
		io_mav.addObject("locationList", lt_locations);

		centerGoogleMap(io_mav, io_user, 15);
	}

	/**
	 * Darstellung des Radius in Kilometer auf der Google Maps
	 * 
	 * @param io_mav    aktuelles Model
	 * @param iv_radius Radius in Meter
	 */
	public void showRadiusOnMap(ModelAndView io_mav, int iv_radius) {
		io_mav.addObject("googleMapsRadius", iv_radius * 1000);
	}

	/**
	 * Darstellung des Radius in Kilometer auf der Google Maps
	 * 
	 * @param io_model  aktuelles Model
	 * @param iv_radius Radius in Meter
	 */
	public void showRadiusOnMap(Model io_model, int iv_radius) {
		io_model.addAttribute("googleMapsRadius", iv_radius * 1000);
	}

	/**
	 * Google Maps anhand Geodaten eines Nutzers zentrieren
	 * 
	 * @param io_mav    aktuelles Model
	 * @param io_nutzer Nutzer
	 * @param iv_zoom   Maßstab der Darstellung
	 */
	public void centerGoogleMap(ModelAndView io_mav, Nutzer io_nutzer, int iv_zoom) {
		LatLng ls_currentUserPosition = GoogleContext.getGeocode(io_nutzer);

		if (ls_currentUserPosition != null) {
			io_mav.addObject("lat", ls_currentUserPosition.lat);
			io_mav.addObject("lng", ls_currentUserPosition.lng);
		}
		io_mav.addObject("googleMapsZoom", iv_zoom);
	}

	/**
	 * Darstellung von Angeboten auf der Google Maps
	 * 
	 * @param io_model aktuelles Model
	 * @param it_offer Angebotsliste
	 */
	public void showOffersOnMap(ModelAndView io_model, List<Angebot> it_offer) {
		GoogleMarker lt_googleMarker[] = new GoogleMarker[it_offer.size()];
		List<LatLng> lt_locations = new ArrayList<LatLng>();

		for (int i = 0; i < it_offer.size(); i++) {
			GeocodingResult lt_geocodingResult[] = GoogleContext.getGeocode(it_offer.get(i).getPlz(),
					it_offer.get(i).getStadt());

			if (lt_geocodingResult != null) {
				lt_locations.add(lt_geocodingResult[0].geometry.location);
				lt_googleMarker[i] = new GoogleMarker(it_offer.get(i).getTitel(),
						lt_geocodingResult[0].geometry.location);
			}
		}

		io_model.addObject("googleMarkers", lt_googleMarker);
		io_model.addObject("locationList", lt_locations);
	}

	/**
	 * Darstellung von Nutzern auf der Google Maps
	 * 
	 * @param io_mav  aktuelles Model
	 * @param it_user Nutzerliste
	 */
	public void showUsersOnMap(ModelAndView io_mav, List<Nutzer> it_user) {
		GoogleMarker lt_googleMarker[] = new GoogleMarker[it_user.size()];
		List<LatLng> lt_locations = new ArrayList<LatLng>();

		for (int i = 0; i < it_user.size(); i++) {
			GeocodingResult lt_geocodingResult[] = GoogleContext.getGeocode(it_user.get(i).getAdresse(),
					it_user.get(i).getPlz(), it_user.get(i).getStadt());

			if (lt_geocodingResult != null) {
				lt_locations.add(lt_geocodingResult[0].geometry.location);
				lt_googleMarker[i] = new GoogleMarker(it_user.get(i).getVorname() + " " + it_user.get(i).getNachname(),
						lt_geocodingResult[0].geometry.location);
			}
		}

		io_mav.addObject("googleMarkers", lt_googleMarker);
		io_mav.addObject("locationList", lt_locations);
	}

	/**
	 * Darstellung eines Nutzers auf der Google Maps
	 * 
	 * @param io_model aktuelles Model
	 * @param io_user  darzustellender Nutzer
	 */
	public void showUserOnMap(Model io_model, Nutzer io_user) {
		GoogleMarker lt_googleMarker[] = new GoogleMarker[1];
		List<LatLng> lt_locations = new ArrayList<LatLng>();

		GeocodingResult lt_geocodingResult[] = GoogleContext.getGeocode(io_user.getAdresse(), io_user.getPlz(),
				io_user.getStadt());

		if (lt_geocodingResult != null) {
			lt_locations.add(lt_geocodingResult[0].geometry.location);
			lt_googleMarker[0] = new GoogleMarker(io_user.getVorname() + " " + io_user.getNachname(),
					lt_geocodingResult[0].geometry.location);
		}

		io_model.addAttribute("googleMarkers", lt_googleMarker);
		io_model.addAttribute("locationList", lt_locations);

		centerGoogleMap(io_model, io_user, 15);
	}

	public void centerGoogleMap(Model io_model, Nutzer io_nutzer, int iv_zoom) {
		LatLng ls_currentUserPosition = GoogleContext.getGeocode(io_nutzer);

		if (ls_currentUserPosition != null) {
			io_model.addAttribute("lat", ls_currentUserPosition.lat);
			io_model.addAttribute("lng", ls_currentUserPosition.lng);
		}
		io_model.addAttribute("googleMapsZoom", iv_zoom);
	}

	/**
	 * Darstellung von Angeboten auf der Google Maps
	 * 
	 * @param io_model aktuelles Model
	 * @param it_offer Angebotsliste
	 */
	public void showOffersOnMap(Model io_model, List<Angebot> it_offer) {
		GoogleMarker lt_googleMarker[] = new GoogleMarker[it_offer.size()];
		List<LatLng> lt_locations = new ArrayList<LatLng>();

		for (int i = 0; i < it_offer.size(); i++) {
			GeocodingResult lt_geocodingResult[] = GoogleContext.getGeocode(it_offer.get(i).getPlz(),
					it_offer.get(i).getStadt());

			if (lt_geocodingResult != null) {
				lt_locations.add(lt_geocodingResult[0].geometry.location);
				lt_googleMarker[i] = new GoogleMarker(it_offer.get(i).getTitel(),
						lt_geocodingResult[0].geometry.location);
			}
		}

		io_model.addAttribute("googleMarkers", lt_googleMarker);
		io_model.addAttribute("locationList", lt_locations);
	}

	/**
	 * Darstellung von Nutzern auf der Google Maps
	 * 
	 * @param io_model aktuelles Model
	 * @param it_user  Nutzerliste
	 */
	public void showUsersOnMap(Model io_model, List<Nutzer> it_user) {
		GoogleMarker lt_googleMarker[] = new GoogleMarker[it_user.size()];
		List<LatLng> lt_locations = new ArrayList<LatLng>();

		for (int i = 0; i < it_user.size(); i++) {
			GeocodingResult lt_geocodingResult[] = GoogleContext.getGeocode(it_user.get(i).getAdresse(),
					it_user.get(i).getPlz(), it_user.get(i).getStadt());

			if (lt_geocodingResult != null) {
				lt_locations.add(lt_geocodingResult[0].geometry.location);
				lt_googleMarker[i] = new GoogleMarker(it_user.get(i).getVorname() + " " + it_user.get(i).getNachname(),
						lt_geocodingResult[0].geometry.location);
			}
		}

		io_model.addAttribute("googleMarkers", lt_googleMarker);
		io_model.addAttribute("locationList", lt_locations);
	}
}