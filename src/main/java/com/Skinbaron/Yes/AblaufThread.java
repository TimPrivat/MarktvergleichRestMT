package com.Skinbaron.Yes;

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

		System.out.println("Modulo: " + modulo + " rest: " + rest);

		for (int i = 0; i < SkinBaronRestApiApplication.hashnames.size(); i++) {

			if (i % modulo == rest) {

				String name = SkinBaronRestApiApplication.hashnames.get(i);

				// System.out.println("Aktueller Name: " + name);
				Double Steamprice = SkinBaronRestApiApplication.getSteamPrice(name);
				// System.out.println("Aktueller Preis " + Steamprice);

				// Findet den SkinbaronPreis

				Double Skinbaronprice = null;

				for (int o = 0; o < SkinBaronRestApiApplication.Skinbaronarr.size(); o++) {

					JSONObject sb = (JSONObject) SkinBaronRestApiApplication.Skinbaronarr.get(o);
					String sbname = (String) sb.get("marketHashName");

					String asString = sb.toJSONString();

					Gson gson = new Gson();
					SkinBaronSkin s = gson.fromJson(sb.toString(), SkinBaronSkin.class);

					String FN = "(Factory New)";
					String MW = "(Minimal Wear)";
					String FT = "(Field-Tested)";
					String WW = "(Minimal Wear)";
					String BS = "(Battle-Scarred)";

					// Es gibt sonderfälle, wo skins in der Skinbaronfile stehen, aber weder ST
					// noch Souv sind, aber als souvenier gezählt werden und den preis von billigen
					// Items verfälschen

					/*
					 * if ((sbname.contains(FN) || sbname.contains(MW) || sbname.contains(FT) ||
					 * sbname.contains(WW) || sbname.contains(BS)) && (s.souvenir == null &&
					 * s.statTrak == null)) { continue;
					 * 
					 * }
					 */

					if (s.souvenir != null) {
						if (!s.souvenir && sbname.contains("Souvenir")) {
							sbname = sbname.replaceAll("Souvenir ", "");
						}
						if (s.souvenir && !sbname.contains("Souvenir")) {
							sbname = "Souvenir " + sbname;
						}
					}
					if (s.statTrak != null) {

						if (!s.statTrak && sbname.contains("StatTrak™")) {
							sbname = sbname.replaceAll("StatTrak™ ", "");
						}
						if (s.statTrak && !sbname.contains("StatTrak™")) {
							sbname = "StatTrak™ " + sbname;
						}
					}

					if (sbname.equals(name)) {
						try {

							boolean wear = false;

							try {

								Double w = (Double) sb.get("minWear");

								if (w != null || w != 0.0) {

									wear = true;

								}

							} catch (Exception e) {

							}

							if (sb.get("statTrak") == null && sb.get("souvenir") == null && (wear))
								continue;

							Skinbaronprice = (Double) sb.get("lowestPrice");

						} catch (ClassCastException e) {
							Long tmp = (Long) sb.get("lowestPrice");
							Skinbaronprice = tmp * 1.0;

						}

						break;
					}

				}

				// Findet den SkinportPreis

				Double SkinportPreis = null;

				for (int o = 0; o < SkinBaronRestApiApplication.Skinportarr.size(); o++) {

					JSONObject sp = (JSONObject) SkinBaronRestApiApplication.Skinportarr.get(o);
					if (sp.get("market_hash_name").equals(name)) {

						try {

							SkinportPreis = (Double) sp.get("min_price_skinport");

						} catch (ClassCastException e) {

							Long tmp = (Long) sp.get("min_price_skinport");
							SkinportPreis = tmp * 1.0;

						}
						break;
					}

				}

				Skin a = new Skin(name, Steamprice, SkinportPreis, Skinbaronprice);

				System.out.println(i + "/" + SkinBaronRestApiApplication.hashnames.size() + " Skinname:" + a.markethash
						+ " Steampreis: " + a.Steampreis + " SkinbaronPreis:" + a.Skinbaronpreis + " SkinportPreis:"
						+ a.Skinportpreis + " SkinbaronDifferenzEuro:" + a.SkinBaronPreisdifferenzEuro
						+ " SkinBarondifferenzProzent" + a.SkinBaronPreisdifferenzProzent + " SkinPortDifferenzEuro:"
						+ a.SkinportPreisdifferenzEuro + " SkinportDifferenzProzent:"
						+ a.SkinportPreisdifferenzProzent);

				if (a.groeßereDifferenzProzent != null) {

					SkinBaronRestApiApplication.allSkins.add(a);

				}

			}
		}
		SkinBaronRestApiApplication.allThreadshavefinished++;

	}

}
