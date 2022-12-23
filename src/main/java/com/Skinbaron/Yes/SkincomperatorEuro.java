package com.Skinbaron.Yes;

import java.util.Comparator;

public class SkincomperatorEuro implements Comparator<Skin> {

	@Override
	public int compare(Skin o1, Skin o2) {

		if (o1.groeßereDifferenzEuro == o2.groeßereDifferenzEuro)
			return 0;

		if (o1.groeßereDifferenzEuro < o2.groeßereDifferenzEuro)
			return 1;

		if (o1.groeßereDifferenzEuro > o2.groeßereDifferenzEuro)
			return -1;

		return 0;
	}

}
