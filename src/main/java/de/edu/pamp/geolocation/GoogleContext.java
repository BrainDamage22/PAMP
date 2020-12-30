package de.edu.pamp.geolocation;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import de.edu.pamp.dto.Nutzer;

/**
 * @author Tobias
 *
 *         Services rund um die Behandlung von Google
 */
@Service
public class GoogleContext {
	private static GeoApiContext mo_context;

	/**
	 * Konstruktor
	 */
	public GoogleContext() {
	}

	/**
	 * Sofern noch keine Instanz existiert wird diese einmalig angelegt
	 */
	private static GeoApiContext getInstance() {
		if (mo_context == null) {
			mo_context = new GeoApiContext.Builder().apiKey(getAPIkey()).build();
		}

		return mo_context;
	}

	/**
	 * Google API Zugangsschlüssel lesen
	 * 
	 * @return API_KEY Zugangsschlüssel
	 */
	public static String getAPIkey() {
		return "AIzaSyBUJ-2JhImzFUO1ayDSAFUYW5BFQeM02Cs";
		
	}

	/**
	 * Liefert zu einem gesuchten Ort (Straße, Postleitzahl Stadt) die Geodaten
	 * zurück
	 * 
	 * @param iv_street     Straße
	 * @param iv_postalcode Postleitzahl
	 * @param iv_city       Stadt
	 * @return Abfrageergebnis von Google oder NULL
	 */
	public static GeocodingResult[] getGeocode(String iv_street, String iv_postalcode, String iv_city) {
		try {
			GeocodingResult lt_geocodingResult[] = GeocodingApi
					.geocode(getInstance(), iv_street + ", " + iv_postalcode + " " + iv_city).await();

			if (lt_geocodingResult.length == 1 && lt_geocodingResult[0].geometry != null) {

				return lt_geocodingResult;
			} else {
				return null;
			}
		} catch (ApiException e) {
		} catch (InterruptedException e) {
		} catch (IOException e) {
		}

		return null;
	}

	/**
	 * Liefert Längen- und Breitengrade eines Benutzers zurück
	 *
	 * @param io_user betroffener Nutzer
	 * @return Abfrageergebnis von Google oder NULL
	 */
	public static LatLng getGeocode(Nutzer io_user) {
		try {
			GeocodingResult lt_geocodingResult[] = GeocodingApi
					.geocode(getInstance(), io_user.getAdresse() + ", " + io_user.getPlz() + " " + io_user.getStadt())
					.await();

			if (lt_geocodingResult.length == 1 && lt_geocodingResult[0].geometry != null) {

				return lt_geocodingResult[0].geometry.location;
			} else {
				return null;
			}
		} catch (ApiException e) {
		} catch (InterruptedException e) {
		} catch (IOException e) {
		}

		return null;
	}

	/**
	 * Liefert zu einem gesuchten Ort (Postleitzahl Stadt) die Geodaten zurück
	 * 
	 * @param iv_postalcode Postleitzahl
	 * @param iv_city       Stadt
	 * @return Abfrageergebnis von Google oder NULL
	 */
	public static GeocodingResult[] getGeocode(String iv_postalcode, String iv_city) {
		try {
			GeocodingResult lt_geocodingResult[] = GeocodingApi.geocode(getInstance(), iv_postalcode + " " + iv_city)
					.await();

			if (lt_geocodingResult.length == 1 && lt_geocodingResult[0].geometry != null) {

				return lt_geocodingResult;
			} else {
				return null;
			}
		} catch (ApiException e) {
		} catch (InterruptedException e) {
		} catch (IOException e) {
		}

		return null;
	}

	/**
	 * Ermittlung der Distanz zwischen zwei Orten
	 * 
	 * @param iv_origins      Quellort
	 * @param iv_destinations Zielort
	 * @return Abfrageergebnis von Google oder NULL
	 */
	public static Distance getDistance(String iv_origins, String iv_destinations) {
		try {
			DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(getInstance()).origins(iv_origins)
					.destinations(iv_destinations).await();

			if (distanceMatrix.rows.length != 0 && distanceMatrix.rows[0].elements.length != 0) {
				return distanceMatrix.rows[0].elements[0].distance;
			}

		} catch (ApiException e) {
		} catch (InterruptedException e) {
		} catch (IOException e) {
		}

		return null;
	}
}