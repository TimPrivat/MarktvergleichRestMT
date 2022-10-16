package com.Skinbaron.Yes;

public class SkinBaronSkin {

	Number lowestPrice;
	Double maxWear;
	Double minWear;
	
	String exterior;
	String imageUrl;
	String name;
	String marketHashName;

	Boolean statTrak;
	Boolean souvenir;
	
	@Override 
	public String toString() {
		
		String s=""+ marketHashName +"\nSouvenier: "+ souvenir+ "\nStatTrak: "+ statTrak+"\n";
		return s;
		
	}
}
