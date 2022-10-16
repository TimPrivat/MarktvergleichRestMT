package com.Skinbaron.Yes;

import java.util.ArrayList;

import org.json.simple.JSONObject;

/*
 * Nützliche Links:
 * STEAM API: https://steamapis.com/docs/market#stats
 * 
 */

public interface ScrapeSkinnames {

	/**
	 * Gbt eine ArrayList aller Liks zu den Skins auf CSGOStash zurück
	 * e.g.: List zu Ak-Redline
	 * KEINE DATEN/ Spezifische Skins
	 * @return
	 */
	public ArrayList<String> getAllSkinLinks();
	
	/**
	 * bekommt einen Link zu dem Skin auf CSGOSTash übergeben
	 * und ermittelt dann mithilfe der STeamAPi den Preis für die verschiedenen Conditions
	 * Hat die COnditions als Attribute
	 * (18 Attribute STeam Preis FN, Preis FN ST, Preis FN Souvenier) *3 Für die Marktplätze
	 * NIcht verwendete Werte werden null gesetzt
	 * 
	 * Ruft getSkinbaronPreis für die jeweiligen unterpreise auf
	 * @param Skinlink
	 * @return
	 */
	public JSONObject getItemPrices(String Skinlink);
	
	public String getSteamPrice(String Skinlink);
	
	/**
	 * Ermittelt mithilfe der übergebenen werte den günstigsten Preis des spezifizierten Items
	 * auf Skinbaron
	 * @return
	 */
	public String getSkinbaronPrice();
}
