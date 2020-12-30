package de.edu.pamp.geolocation;

import org.springframework.stereotype.Service;

import com.google.maps.model.LatLng;

/**
 * @author Tobias
 *
 *         Marker-Klasse für die Darstellung auf einer Google Maps
 */
@Service
public class GoogleMarker {

	private String mv_markerTitle;
	private LatLng ms_markerPosition;

	/**
	 * Konstruktor
	 */
	public GoogleMarker() {

	}

	/**
	 * Konstruktor
	 * 
	 * @param iv_title    Titel des Markers
	 * @param is_position Position des Markers
	 */
	public GoogleMarker(String iv_title, LatLng is_position) {
		setMv_markerTitle(iv_title);
		setMs_markerPosition(is_position);
	}

	/**
	 * Liefert den Titel zurück
	 * 
	 * @return mv_markerTitle
	 */
	public String getMv_markerTitle() {
		return mv_markerTitle;
	}

	/**
	 * Setzen des Titels
	 * 
	 * @param mv_markerTitle das zu setzende Objekt mv_markerTitle
	 */
	public void setMv_markerTitle(String mv_markerTitle) {
		this.mv_markerTitle = mv_markerTitle;
	}

	/**
	 * Liefert die Position zurück
	 * 
	 * @return mt_markerPosition
	 */
	public LatLng getMt_markerPosition() {
		return ms_markerPosition;
	}

	/**
	 * Setzen der Position
	 * 
	 * @param ms_markerPosition das zu setzende Objekt mt_markerPosition
	 */
	public void setMs_markerPosition(LatLng ms_markerPosition) {
		this.ms_markerPosition = ms_markerPosition;
	}
}