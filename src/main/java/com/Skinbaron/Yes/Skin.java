package com.Skinbaron.Yes;

public class Skin {

	String markethash;
	Double Skinportpreis;
	Double Steampreis;
	Double SteampreisnachSteuern;
	Double Skinbaronpreis;

	public Skin(String markethash, Double Steampreis, Double SkinportPreis, Double SkinbaronPreis) {
		this.markethash = markethash;
		this.Steampreis = Steampreis;// *0.97; // Anpassung für Dollar Euro Wechselkurs

		this.Skinportpreis = SkinportPreis;
		this.Skinbaronpreis = SkinbaronPreis;
		SteampreisnachSteuern = Steampreis / 1.15;
		berechneZeug();

	}

	Double SkinportPreisdifferenzEuro;
	Double SkinportPreisdifferenzProzent;
	Double SkinBaronPreisdifferenzEuro;
	Double SkinBaronPreisdifferenzProzent;

	Double groeßereDifferenzProzent;
	Double groeßereDifferenzEuro;
	Double bessererWert;
	String MarktplatzProzent;
	String MarktplatzEuro;

	public void berechneZeug() {

		if (Skinportpreis == null) {

			SkinportPreisdifferenzEuro = null;
			SkinportPreisdifferenzProzent = null;

		}
		if (Skinbaronpreis == null) {

			SkinBaronPreisdifferenzEuro = null;
			SkinBaronPreisdifferenzProzent = null;

		}

		if ((Skinportpreis == null && Skinbaronpreis == null) || Steampreis == 0) {
			groeßereDifferenzEuro = null;
			groeßereDifferenzProzent = null;
			MarktplatzProzent = null;

		} else if (Skinportpreis != null && Skinbaronpreis == null) {

			SkinportPreisdifferenzEuro = (Steampreis / 1.15) - Skinportpreis;
			SkinportPreisdifferenzProzent = ((Steampreis / 1.15) / Skinportpreis) * 100;
			SkinportPreisdifferenzProzent = SkinportPreisdifferenzProzent - 100;
			groeßereDifferenzProzent = SkinportPreisdifferenzProzent;
			MarktplatzProzent = "SkinPort";

		} else if (Skinportpreis == null && Skinbaronpreis != null) {

			SkinBaronPreisdifferenzEuro = (Steampreis / 1.15) - Skinbaronpreis;
			SkinBaronPreisdifferenzProzent = ((Steampreis / 1.15) / Skinbaronpreis) * 100;
			SkinBaronPreisdifferenzProzent = SkinBaronPreisdifferenzProzent - 100;
			groeßereDifferenzProzent = SkinBaronPreisdifferenzProzent;
			MarktplatzProzent = "SkinBaron";

		} else {

			SkinportPreisdifferenzEuro = (Steampreis / 1.15) - Skinportpreis;
			SkinportPreisdifferenzProzent = ((Steampreis / 1.15) / Skinportpreis) * 100;
			SkinportPreisdifferenzProzent = SkinportPreisdifferenzProzent - 100;

			SkinBaronPreisdifferenzEuro = (Steampreis / 1.15) - Skinbaronpreis;
			SkinBaronPreisdifferenzProzent = ((Steampreis / 1.15) / Skinbaronpreis) * 100;
			SkinBaronPreisdifferenzProzent = SkinBaronPreisdifferenzProzent - 100;

			if (SkinportPreisdifferenzProzent > SkinBaronPreisdifferenzProzent) {

				groeßereDifferenzProzent = SkinportPreisdifferenzProzent;
				MarktplatzProzent = "SkinPort";
			} else {

				groeßereDifferenzProzent = SkinBaronPreisdifferenzProzent;
				MarktplatzProzent = "SkinBaron";

			}

		}
		if (MarktplatzProzent != null) {
			if (MarktplatzProzent.equals("SkinBaron")) {

				bessererWert = Skinbaronpreis;

			} else {

				bessererWert = Skinportpreis;

			}

		} else {

			bessererWert = null;

		}

		if (SkinBaronPreisdifferenzEuro != null && SkinportPreisdifferenzEuro != null) {

			if (SkinBaronPreisdifferenzEuro > SkinportPreisdifferenzEuro) {
				groeßereDifferenzEuro = SkinBaronPreisdifferenzEuro;
				MarktplatzEuro = "SkinBaron";
			} else {
				groeßereDifferenzEuro = SkinportPreisdifferenzEuro;
				MarktplatzEuro = "SkinPort";
			}

		} else if (SkinBaronPreisdifferenzEuro != null) {

			groeßereDifferenzEuro = SkinBaronPreisdifferenzEuro;
			MarktplatzEuro = "SkinBaron";

		} else if (SkinportPreisdifferenzEuro != null) {

			groeßereDifferenzEuro = SkinportPreisdifferenzEuro;
			MarktplatzEuro = "SkinPort";

		}

	}

}
