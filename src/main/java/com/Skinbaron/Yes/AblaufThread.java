package com.Skinbaron.Yes;

import java.util.Map;

import org.json.simple.JSONObject;

import com.google.gson.Gson;

public class AblaufThread implements Runnable {

	private Thread t;
	private int modulo;
	private int rest;

	public AblaufThread(int modulo, int rest) {

		t = new Thread(this);
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.modulo = modulo;
		this.rest = rest;

	}

	public void start() {

		t.start();

	}

	public void run() {

		Map<String, Double> SkinBaronMap = SkinBaronRestApiApplication.SkinBaronMap;
		Map<String, Double> SkinPortMap = SkinBaronRestApiApplication.SkinPortMap;
		Map<String, Double> BitSkinsMap = SkinBaronRestApiApplication.BitSkinsMap;
		System.out.println("Modulo: " + modulo + " rest: " + rest);

		for (int i = 0; i < SkinBaronRestApiApplication.hashnames.size(); i++) {

			if (i % modulo == rest) {

				String name = SkinBaronRestApiApplication.hashnames.get(i);

				// System.out.println("Aktueller Name: " + name);
				Double Steamprice = SkinBaronRestApiApplication.getSteamPrice(name);

				// System.out.println("Aktueller Preis " + Steamprice);

				// Findet den SkinbaronPreis

				Double Skinbaronprice = null;

				try {

					Skinbaronprice = SkinBaronMap.get(name);

				} catch (Exception e) {
				}

				// Findet den SkinportPreis

				Double Skinportprice = null;

				try {

					Skinportprice = SkinPortMap.get(name);

				} catch (Exception e) {
				}
				
				//Findet den Bitskinspreis
				Double BitSkinsprice = null;

				try {

					BitSkinsprice = BitSkinsMap.get(name);

				} catch (Exception e) {
				}
				
				
				Skin a = new Skin(name, Steamprice, Skinportprice, Skinbaronprice,BitSkinsprice);

				System.out.println(i + "/" + SkinBaronRestApiApplication.hashnames.size() + " Skinname:" + a.markethash
						+ " Steampreis: " + a.Steampreis + " SkinbaronPreis:" + a.Skinbaronpreis + "SkinportPreis:"
						+ a.Skinportpreis + " SkinbaronDifferenzEuro:" + a.SkinBaronPreisdifferenzEuro
						+ " BitSkinsdifferenzProzent" + a.BitSkinsPreisdifferenzProzent+ " BitSkinsDifferenzEuro:" + a.BitSkinsPreisdifferenzEuro
						+ " BitSkinsdifferenzProzent" + a.BitSkinsPreisdifferenzProzent + "SkinPortDifferenzEuro:"
						+ a.SkinportPreisdifferenzEuro + " SkinportDifferenzProzent:" + a.SkinportPreisdifferenzProzent
						+ "EuroMarktplatz: " + a.Marktplatz + " GrößereDifferenz: " + a.groeßereDifferenzEuro);

				if (a.groeßereDifferenzProzent != null) {

					SkinBaronRestApiApplication.allSkins.add(a);

				}

			}
		}
		SkinBaronRestApiApplication.allThreadshavefinished++;

	}

}
