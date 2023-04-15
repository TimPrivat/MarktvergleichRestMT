package com.Skinbaron.Yes;

import java.util.ArrayList;
import java.util.Collections;

public class Skin {

	String markethash;
	Double Skinportpreis;
	Double Steampreis;
	Double SteampreisnachSteuern;
	Double Skinbaronpreis;

	Double SkinportPreisdifferenzEuro;
	Double SkinportPreisdifferenzProzent;
	Double SkinBaronPreisdifferenzEuro;
	Double SkinBaronPreisdifferenzProzent;

	Double groeßereDifferenzProzent;
	Double groeßereDifferenzEuro;
	Double bessererWert;
	String Marktplatz;
	Double Prozentsum = 0.0;

	public Skin(String markethash, Double Steampreis, Double SkinportPreis, Double SkinbaronPreis) {
		this.markethash = markethash;
		this.Steampreis = Steampreis;// *0.97; // Anpassung für Dollar Euro Wechselkurs

		this.Skinportpreis = SkinportPreis;
		this.Skinbaronpreis = SkinbaronPreis;
		SteampreisnachSteuern = Steampreis / 1.15;
		// berechneZeug();
		//System.out.println(markethash + " Skinbaronprice: " + SkinbaronPreis + " Skinportprice: " + SkinportPreis);

		SkinportPreisdifferenzEuro = (Skinportpreis != null) ? (Steampreis / 1.15) - Skinportpreis : null;
		SkinportPreisdifferenzProzent = (Skinportpreis != null) ? ((Steampreis / 1.15) / Skinportpreis) * 100 : null;
		SkinBaronPreisdifferenzEuro = (SkinbaronPreis != null) ? (Steampreis / 1.15) - Skinbaronpreis : null;
		SkinBaronPreisdifferenzProzent = (SkinbaronPreis != null) ? ((Steampreis / 1.15) / SkinbaronPreis) * 100 : null;

		ArrayList<Double> Prozentdifferenzen = new ArrayList<>();
		Prozentdifferenzen.add(SkinportPreisdifferenzProzent);
		Prozentdifferenzen.add(SkinBaronPreisdifferenzProzent);

		Prozentdifferenzen.forEach(x -> {
			try {
				Prozentsum += x;
			} catch (Exception e) {
			}
			;
		});

		Double maxWert = null;

		try {
			maxWert = Collections.max(Prozentdifferenzen);
		} catch (Exception e) {
		}

		if (maxWert == SkinportPreisdifferenzProzent) {
			Marktplatz = "SkinPort";
			groeßereDifferenzProzent = SkinportPreisdifferenzProzent;
			groeßereDifferenzEuro = SkinportPreisdifferenzEuro;
			bessererWert = SkinportPreis;

		} else if (maxWert == SkinBaronPreisdifferenzProzent) {

			Marktplatz = "SkinBaron";
			groeßereDifferenzProzent = SkinBaronPreisdifferenzProzent;
			groeßereDifferenzEuro = SkinBaronPreisdifferenzEuro;
			bessererWert = SkinbaronPreis;

		}

	}

}
