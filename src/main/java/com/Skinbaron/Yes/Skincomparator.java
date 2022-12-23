package com.Skinbaron.Yes;

import java.util.Comparator;

public class Skincomparator implements Comparator<Skin> {

	@Override
	public int compare(Skin o1, Skin o2) {

		if (o1.groeßereDifferenzProzent == o2.groeßereDifferenzProzent)
			return 0;

		if (o1.groeßereDifferenzProzent < o2.groeßereDifferenzProzent)
			return 1;

		if (o1.groeßereDifferenzProzent > o2.groeßereDifferenzProzent)
			return -1;

		return 0;
	}

}
